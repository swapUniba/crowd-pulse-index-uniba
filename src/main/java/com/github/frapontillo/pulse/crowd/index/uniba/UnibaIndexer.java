package com.github.frapontillo.pulse.crowd.index.uniba;

import com.github.frapontillo.pulse.crowd.data.entity.Message;
import com.github.frapontillo.pulse.crowd.index.uniba.rest.Index;
import com.github.frapontillo.pulse.crowd.index.uniba.rest.IndexRequest;
import com.github.frapontillo.pulse.crowd.index.uniba.rest.IndexResponse;
import com.github.frapontillo.pulse.crowd.index.uniba.rest.UnibaIndexService;
import com.github.frapontillo.pulse.rx.PulseSubscriber;
import com.github.frapontillo.pulse.rx.RxUtil;
import com.github.frapontillo.pulse.spi.IPlugin;
import com.github.frapontillo.pulse.util.ConfigUtil;
import com.github.frapontillo.pulse.util.DateUtil;
import com.github.frapontillo.pulse.util.PulseLogger;
import org.apache.logging.log4j.Logger;
import retrofit.RestAdapter;
import rx.Observable;
import rx.observables.GroupedObservable;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Properties;

/**
 * Plugin that performs message indexing by relying on the Akka Indexing platform built by
 * Alessandro Natilla and Angelo Impedovo.
 *
 * @author Francesco Pontillo
 */
public class UnibaIndexer extends IPlugin<Message, Message, IndexParameters> {
    private static final String PLUGIN_NAME = "index-uniba";
    private static final Logger logger = PulseLogger.getLogger(UnibaIndexer.class);
    private static final int MAX_BUFFER = 20;
    private static final List<String> SUPPORTED_LANGS = Arrays.asList("it", "en");

    private UnibaIndexService service;

    @Override public String getName() {
        return PLUGIN_NAME;
    }

    @Override public IndexParameters getNewParameter() {
        return new IndexParameters();
    }

    @Override
    protected Observable.Operator<Message, Message> getOperator(IndexParameters parameters) {
        return null;
    }

    @Override public Observable.Transformer<Message, Message> transform(IndexParameters params) {
        return messageObservable -> {
            Observable<Observable<Message>> grouped = messageObservable
                    // group the messages by language
                    // then, for each language, buffer the messages, index them, then flatten
                    // them again within the group
                    .groupBy(Message::getLanguage)
                    .lift(subscriber -> new PulseSubscriber<GroupedObservable<String, Message>>(
                            subscriber) {
                        @Override public void onNext(GroupedObservable<String, Message> group) {
                            Observable<Message> messages = group.buffer(MAX_BUFFER)
                                    .lift(getBufferedOperator(params, group.getKey()))
                                    .compose(RxUtil.flatten());
                            subscriber.onNext(messages);
                        }

                        @Override public void onCompleted() {
                            reportPluginAsCompleted();
                            super.onCompleted();
                        }

                        @Override public void onError(Throwable e) {
                            reportPluginAsErrored();
                            super.onError(e);
                        }
                    });
            // in the end, merge all of the groups
            return Observable.merge(grouped);
        };
    }

    /**
     * Create a schema using the input parameters and returns its name once it is created on the
     * indexing service.
     *
     * @param parameters The input {@link IndexParameters}.
     *
     * @return The name of the created schema, containing the date and time of creation.
     */
    private String getSchemaName(IndexParameters parameters) {
        Index currentIndex = new Index();
        String schemaName = "schema-" + DateUtil.getUnixEpoch(new Date());
        currentIndex.setId(schemaName);
        currentIndex.setSchema(parameters.getSchema());
        IndexResponse response = getService().createIndex(currentIndex);
        // if the creation fails, notice self
        if (response.hasErrored()) {
            logger.error(String.format("Could not create an indexing schema (\"%s\").",
                    response.getError()));
        }
        return schemaName;
    }

    /**
     * Builds an {@link rx.Observable.Operator} that, from buffered {@link Message}s of the same
     * language, calls the indexing Web service and indexes the {@link Message}s.
     *
     * @param parameters The indexing parameters.
     * @param language   The language of the incoming messages.
     *
     * @return A buffered operator that will put back into the stream all indexed messages.
     */
    private Observable.Operator<List<Message>, List<Message>> getBufferedOperator(
            IndexParameters parameters, String language) {

        // if the language is supported, index the contents
        if (SUPPORTED_LANGS.contains(language)) {
            return subscriber -> new PulseSubscriber<List<Message>>(subscriber) {

                @Override public void onNext(List<Message> messages) {
                    messages.forEach(m -> reportElementAsStarted(m.getId()));

                    // feed the created model with the new message
                    IndexRequest req = new IndexRequest();
                    req.setId(getSchemaName(parameters));
                    req.setLang(language);
                    req.setContents(messages);

                    // make the request
                    IndexResponse res = getService().index(req);
                    // if the response errors, let the pipeline handle it
                    if (res.hasErrored()) {
                        logger.error(
                                String.format("Could not index message (\"%s\").", res.getError()));
                    }

                    messages.forEach(m -> reportElementAsEnded(m.getId()));
                    subscriber.onNext(messages);
                }

            };
        }

        // if the language is not supported, flow elements through
        return subscriber -> new PulseSubscriber<List<Message>>(subscriber) {
            @Override public void onNext(List<Message> messages) {
                messages.forEach(m -> reportElementAsStarted(m.getId()));
                messages.forEach(m -> reportElementAsEnded(m.getId()));
                subscriber.onNext(messages);
            }
        };
    }

    private UnibaIndexService getService() {
        if (service == null) {
            Properties props = ConfigUtil.getPropertyFile(this.getClass(), "index.properties");
            String baseUrl = props.getProperty("index.base");
            RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint(baseUrl).build();
            service = restAdapter.create(UnibaIndexService.class);
        }
        return service;
    }
}

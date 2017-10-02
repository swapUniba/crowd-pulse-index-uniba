package com.github.frapontillo.pulse.crowd.index.uniba;

import com.github.frapontillo.pulse.spi.IPluginConfig;
import com.github.frapontillo.pulse.spi.PluginConfigHelper;
import com.github.frapontillo.pulse.util.PulseLogger;
import com.google.gson.JsonElement;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.List;

/**
 * Indexing parameters class for the Uniba Indexing Web Service.
 *
 * @author Francesco Pontillo
 */
public class IndexParameters implements IPluginConfig<IndexParameters> {
    private String schema;

    private final static List<String> SCHEMAS = Arrays.asList("TFIDF", "BM25", "TF");
    private final static Logger logger = PulseLogger.getLogger(IndexParameters.class);

    /**
     * Get the schema to use for indexing.
     *
     * @return The indexing schema.
     */
    public String getSchema() {
        return schema;
    }

    /**
     * Set the schema to use for indexing.
     *
     * @param schema The indexing schema, can be one of "TFIDF", "BM25", "TF".
     */
    public void setSchema(String schema) {
        this.schema = schema;
    }

    @Override public IndexParameters buildFromJsonElement(JsonElement json) {
        IndexParameters parameters = PluginConfigHelper.buildFromJson(json, IndexParameters.class);
        String candidateSchema = parameters.getSchema();
        if (!SCHEMAS.contains(candidateSchema)) {
            logger.warn(
                    "The indexing schema you specified ({}) is not supported. Defaulting to TFIDF.",
                    candidateSchema);
            candidateSchema = "TFIDF";
        }
        parameters.setSchema(candidateSchema);
        return parameters;
    }
}

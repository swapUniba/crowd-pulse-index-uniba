package com.github.frapontillo.pulse.crowd.index.uniba.rest;

import com.github.frapontillo.pulse.crowd.data.entity.Message;

import java.util.ArrayList;
import java.util.List;

/**
 * Models a request of indexing by specifying the ID of the index to use, the language of the
 * elements and a list of contents to be indexed.
 *
 * @author Francesco Pontillo
 */
public class IndexRequest {
    private String id;
    private String lang;
    private List<TextContent> content;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public List<TextContent> getContent() {
        return content;
    }

    public void setContent(List<TextContent> content) {
        this.content = content;
    }

    public void setContents(List<Message> messages) {
        this.content = new ArrayList<>(messages.size());
        messages.forEach(
                message -> this.content.add(new TextContent(message.getoId(), message.getText())));
    }
}

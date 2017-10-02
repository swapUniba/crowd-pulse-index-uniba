package com.github.frapontillo.pulse.crowd.index.uniba.rest;

import java.util.HashMap;

/**
 * Element to index, containing an ID and a textual content.
 *
 * @author Francesco Pontillo
 */
public class TextContent {
    private String id;
    private String content;
    private HashMap<String, Integer> targets;

    public TextContent(String id, String content) {
        this.id = id;
        this.content = content;
        this.targets = new HashMap<>();
        this.targets.put(id, 1);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public HashMap<String, Integer> getTargets() {
        return targets;
    }

    public void setTargets(HashMap<String, Integer> targets) {
        this.targets = targets;
    }
}

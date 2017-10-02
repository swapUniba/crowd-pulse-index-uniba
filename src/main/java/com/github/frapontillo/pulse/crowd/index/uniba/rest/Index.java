package com.github.frapontillo.pulse.crowd.index.uniba.rest;

/**
 * Class that models an index by specifying an ID and the weighting schema (TFIDF, BM25, TF).
 *
 * @author Francesco Pontillo
 */
public class Index {
    private String id;
    private String schema;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }
}

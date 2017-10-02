package com.github.frapontillo.pulse.crowd.index.uniba.rest;

import com.github.frapontillo.pulse.util.StringUtil;

import java.util.List;

/**
 * Models the outcome of an operation performed by the Uniba indexing service.
 *
 * @author Francesco Pontillo
 */
public class IndexResponse {
    private String OK;
    private String error;
    private List<IndexDocument> succeeded;
    private List<IndexDocument> unsucceeded;

    public String getOK() {
        return OK;
    }

    public void setOK(String OK) {
        this.OK = OK;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public List<IndexDocument> getSucceeded() {
        return succeeded;
    }

    public void setSucceeded(List<IndexDocument> succeeded) {
        this.succeeded = succeeded;
    }

    public List<IndexDocument> getUnsucceeded() {
        return unsucceeded;
    }

    public void setUnsucceeded(List<IndexDocument> unsucceeded) {
        this.unsucceeded = unsucceeded;
    }

    /**
     * Check if the response contains an error.
     *
     * @return {@code true} if the response has errored, {@code false} otherwise.
     */
    public boolean hasErrored() {
        return !StringUtil.isNullOrEmpty(error);
    }
}

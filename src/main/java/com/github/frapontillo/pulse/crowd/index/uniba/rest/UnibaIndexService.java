package com.github.frapontillo.pulse.crowd.index.uniba.rest;

import retrofit.http.Body;
import retrofit.http.POST;

/**
 * Retrofit service interface to access the Uniba Indexing service.
 *
 * @author Francesco Pontillo
 */
public interface UnibaIndexService {
    @POST("/rest/create") IndexResponse createIndex(@Body Index index);

    @POST("/rest/indexcontents") IndexResponse index(@Body IndexRequest request);
}

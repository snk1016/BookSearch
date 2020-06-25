package com.example.test.librarysearch.services

import retrofit2.http.GET
import retrofit2.http.Query

interface NetworkService {
    @GET("/v3/search/book")
    suspend fun book(@Query("target") target: String, @Query("query") query: String
                     , @Query("size") size: Int, @Query("page") page: Int): List<String>
}

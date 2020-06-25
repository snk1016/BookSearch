package com.example.test.librarysearch.services

import com.example.test.librarysearch.model.response.Response
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query

interface NetworkService {
    @GET("/v3/search/book")
    fun searchBook(@Query("target") target: String, @Query("query") query: String
                     , @Query("size") size: Int, @Query("page") page: Int): Observable<Response>
}

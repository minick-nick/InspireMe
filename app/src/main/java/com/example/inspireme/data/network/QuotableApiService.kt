package com.example.inspireme.data.network

import com.example.inspireme.data.network.model.NetworkQuotation
import com.example.inspireme.data.network.model.SearchResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface QuotableApiService {

    @GET("quotes/random")
    suspend fun getRandomQuotations(@Query("limit") limit: Int): List<NetworkQuotation>

    @GET("quotes/{id}")
    suspend fun getQuotation(@Path("id") id: String): NetworkQuotation

    @GET("search/quotes")
    suspend fun searchQuotations(@Query("query") query: String): SearchResponse

    @GET("quotes")
    suspend fun searchQuotationsWithTagOf(
        @Query("tags") tags: String,
        @Query("limit") limit: Int = 100
    ): SearchResponse
}
package com.example.inspireme.data.network

import com.example.inspireme.data.Quotation
import retrofit2.http.GET
import retrofit2.http.Query

interface QuotableApiService {

    @GET("quotes/random")
    suspend fun getRandomQuotations(@Query("limit") limit: Int): List<Quotation.NetworkQuotation>
}
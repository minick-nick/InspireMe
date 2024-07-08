package com.example.inspireme.data.network

import com.example.inspireme.data.Quotation

interface QuotationsRepository {
    suspend fun getRandomQuotations(limit: Int): List<Quotation.NetworkQuotation>
}

class NetworkQuotationsRepository(
    private val quotableApiService: QuotableApiService
): QuotationsRepository {
    override suspend fun getRandomQuotations(limit: Int): List<Quotation.NetworkQuotation> = quotableApiService.getRandomQuotations(limit)
}
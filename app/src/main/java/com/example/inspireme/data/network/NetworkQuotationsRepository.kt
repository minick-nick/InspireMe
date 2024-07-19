package com.example.inspireme.data.network

import com.example.inspireme.data.network.model.NetworkQuotation
import com.example.inspireme.data.network.model.SearchResponse

interface QuotationsRepository {
    suspend fun getRandomQuotations(limit: Int): List<NetworkQuotation>
    suspend fun getQuotation(id: String): NetworkQuotation
    suspend fun getQuotations(ids: List<String>): List<NetworkQuotation>
    suspend fun searchQuotations(query: String): SearchResponse
    suspend fun searchQuotationsWithCategoryOf(category: String): SearchResponse
}

class NetworkQuotationsRepository(
    private val quotableApiService: QuotableApiService
): QuotationsRepository {
    override suspend fun getRandomQuotations(limit: Int): List<NetworkQuotation> = quotableApiService.getRandomQuotations(limit)
    override suspend fun getQuotation(id: String): NetworkQuotation = quotableApiService.getQuotation(id)
    override suspend fun getQuotations(ids: List<String>): List<NetworkQuotation> = ids.map { getQuotation(it) }
    override suspend fun searchQuotations(query: String): SearchResponse = quotableApiService.searchQuotations(query)
    override suspend fun searchQuotationsWithCategoryOf(category: String) = quotableApiService.searchQuotationsWithTagOf(category)
}
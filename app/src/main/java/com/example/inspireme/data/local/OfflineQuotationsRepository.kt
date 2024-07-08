package com.example.inspireme.data.local

import com.example.inspireme.data.Quotation
import kotlinx.coroutines.flow.Flow

interface QuotationRepository {
    fun getRandomQuotation(): Flow<List<Quotation.LocalQuotation>>

    suspend fun insertQuotation(quotation: Quotation.LocalQuotation)
}

class OfflineQuotationRepository(private val quotationDao: QuotationDao) : QuotationRepository {
    override fun getRandomQuotation(): Flow<List<Quotation.LocalQuotation>> = quotationDao.getRandomQuotation()

    override suspend fun insertQuotation(quotation: Quotation.LocalQuotation) = quotationDao.insert(quotation)
}
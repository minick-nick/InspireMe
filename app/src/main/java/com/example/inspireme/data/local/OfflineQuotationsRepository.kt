package com.example.inspireme.data.local

import com.example.inspireme.data.local.model.LocalQuotation
import com.example.inspireme.data.local.model.Tag
import kotlinx.coroutines.flow.Flow

interface QuotationRepository {
    fun getAllQuotations(): Flow<List<LocalQuotation>>
    fun getAllQuotationsIds(): Flow<List<String>>
    suspend fun insertQuotation(quotation: LocalQuotation)
    suspend fun unsavedQuotation(id: String)
    fun getAllTags(): Flow<List<Tag>>
}

class OfflineQuotationRepository(
    private val quotationDao: QuotationDao
) : QuotationRepository {
    override fun getAllQuotations(): Flow<List<LocalQuotation>> = quotationDao.getAll()
    override fun getAllQuotationsIds(): Flow<List<String>> = quotationDao.getAllIds()
    override suspend fun insertQuotation(quotation: LocalQuotation) = quotationDao.insert(quotation)
    override suspend fun unsavedQuotation(id: String) = quotationDao.delete(id)
    override fun getAllTags() = quotationDao.getAllTags()
}
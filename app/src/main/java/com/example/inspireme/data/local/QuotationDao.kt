package com.example.inspireme.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.inspireme.data.Quotation
import kotlinx.coroutines.flow.Flow

@Dao
interface QuotationDao {

    @Query("SELECT * FROM quotation")
    fun getRandomQuotation(): Flow<List<Quotation.LocalQuotation>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(quotation: Quotation.LocalQuotation)
}
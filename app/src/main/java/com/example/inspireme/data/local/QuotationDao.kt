package com.example.inspireme.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.inspireme.data.local.model.LocalQuotation
import com.example.inspireme.data.local.model.Tag
import kotlinx.coroutines.flow.Flow

@Dao
interface QuotationDao {

    @Query("SELECT * FROM quotation")
    fun getAll(): Flow<List<LocalQuotation>>

    @Query("SELECT id FROM quotation")
    fun getAllIds(): Flow<List<String>>

    @Query("SELECT * FROM quotation WHERE id = :id")
    fun get(id: String): Flow<LocalQuotation>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(quotation: LocalQuotation)

    @Query("DELETE FROM quotation WHERE id = :id")
    suspend fun delete(id: String)

    @Query("SELECT * FROM tag")
    fun getAllTags(): Flow<List<Tag>>
}
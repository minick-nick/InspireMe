package com.example.inspireme.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "quotation")
data class LocalQuotation(
    @PrimaryKey
    val id: String,
    val content: String,
    val author: String,
    val authorSlug: String
)
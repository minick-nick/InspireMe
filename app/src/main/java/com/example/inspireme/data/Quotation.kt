package com.example.inspireme.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

sealed class Quotation {
    @Serializable
    data class NetworkQuotation(
        @SerialName("_id")
        val id: String,
        val content: String,
        val author: String,
        val tags: List<String>,
        val authorSlug: String
    ) : Quotation()

    @Entity(tableName = "quotation")
    data class LocalQuotation(
        @PrimaryKey
        val id: String,
        val content: String,
        val author: String,
        //val tags: List<String>,
        val authorSlug: String
    ) : Quotation()
}
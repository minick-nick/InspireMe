package com.example.inspireme.data.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NetworkQuotation(
    @SerialName("_id")
    val id: String,
    val content: String,
    val author: String,
    val tags: List<String>,
    val authorSlug: String
)
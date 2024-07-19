package com.example.inspireme.data.network.model

import kotlinx.serialization.Serializable

@Serializable
data class SearchResponse(
    val count: Int,
    val results: List<NetworkQuotation>
)

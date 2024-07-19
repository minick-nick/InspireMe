package com.example.inspireme.ui

import com.example.inspireme.data.local.model.LocalQuotation
import com.example.inspireme.data.network.model.NetworkQuotation

data class QuotationDetails(
    val id: String = "",
    val content: String = "",
    val author: String = "",
    val tags: List<String> = emptyList(),
    val authorSlug: String = "",
    val isSaved: Boolean = false,
)

val QuotationDetails.authorImageSrc: String
    get() = "https://images.quotable.dev/profile/200/$authorSlug.jpg"

fun QuotationDetails.toSavable(): LocalQuotation {
    return LocalQuotation(
        id = id,
        content = content,
        author = author,
        authorSlug = authorSlug
    )
}

fun LocalQuotation.toQuotationDetails(isSaved: Boolean): QuotationDetails {
    return QuotationDetails(
        id = id,
        content = content,
        author = author,
        tags = emptyList(),
        authorSlug = authorSlug,
        isSaved = isSaved
    )
}

fun NetworkQuotation.toQuotationDetails(isSaved: Boolean): QuotationDetails {
    return QuotationDetails(
        id = id,
        content = content,
        author = author,
        tags = emptyList(),
        authorSlug = authorSlug,
        isSaved = isSaved
    )
}
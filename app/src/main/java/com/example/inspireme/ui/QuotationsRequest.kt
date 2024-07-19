package com.example.inspireme.ui

sealed interface QuotationsRequest {
    data class Success(val quotations: List<QuotationDetails>) : QuotationsRequest
    data object Loading : QuotationsRequest
    data object Error : QuotationsRequest
}
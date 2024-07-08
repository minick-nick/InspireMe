package com.example.inspireme.ui

import com.example.inspireme.data.Quotation

sealed interface Event {
    object ReloadRandomQuotations : Event
    data class SaveQuotation(val quotation: Quotation.NetworkQuotation) : Event
}
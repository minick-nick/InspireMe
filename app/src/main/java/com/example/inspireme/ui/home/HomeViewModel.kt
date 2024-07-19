package com.example.inspireme.ui.home

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.inspireme.InspireMeApplication
import com.example.inspireme.RANDOM_QUOTATIONS_LIMIT
import com.example.inspireme.data.local.QuotationRepository
import com.example.inspireme.data.network.QuotationsRepository
import com.example.inspireme.ui.QuotationDetails
import com.example.inspireme.ui.QuotationsRequest
import com.example.inspireme.ui.ViewModelUtils
import com.example.inspireme.ui.toQuotationDetails
import com.example.inspireme.ui.toSavable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


class HomeViewModel(
    private val networkQuotationsRepository: QuotationsRepository,
    private val offlineQuotationsRepository: QuotationRepository
): ViewModel() {
    private val _uiState: MutableStateFlow<HomeUiState> = MutableStateFlow(HomeUiState())

    val uiState: StateFlow<HomeUiState> = _uiState
        .combine(offlineQuotationsRepository.getAllQuotations()) { uiState, savedQuotations ->
            uiState.copy(
                savedQuotations = savedQuotations.map { it.toQuotationDetails(isSaved = true) }
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = _uiState.value
        )


    init {
        getRandomQuotations()
    }


    fun getRandomQuotations(limit: Int = RANDOM_QUOTATIONS_LIMIT) {
        viewModelScope.launch {
            _uiState.update { it.copy(randomQuotationsRequest = QuotationsRequest.Loading) }

            val idsOfSavedQuotations = offlineQuotationsRepository
                .getAllQuotationsIds()
                .first()

            _uiState.update {
                it.copy(
                    randomQuotationsRequest = ViewModelUtils.catchExceptions {
                        val results = networkQuotationsRepository
                            .getRandomQuotations(limit)
                            .map { quotation ->
                                quotation.toQuotationDetails(
                                    isSaved = idsOfSavedQuotations.contains(quotation.id)
                                )
                            }

                        QuotationsRequest.Success(results)
                    }
                )
            }
        }
    }

    fun saveQuotation(quotation: QuotationDetails) {
        updateQuotationDetails(quotation, true)
        viewModelScope.launch {
            offlineQuotationsRepository.insertQuotation(quotation.toSavable())
        }
    }

    fun unsavedQuotation(quotation: QuotationDetails) {
        updateQuotationDetails(quotation, false)
        viewModelScope.launch {
            offlineQuotationsRepository.unsavedQuotation(quotation.id)
        }
    }

    fun shareQuotation(context: Context, quotation: QuotationDetails) {
        ViewModelUtils.shareQuotation(context, quotation)
    }

    private fun updateQuotationDetails(target: QuotationDetails, isSaved: Boolean) {
        if (uiState.value.randomQuotationsRequest is QuotationsRequest.Success) {
            val output = ViewModelUtils.updateQuotationDetails(
                (uiState.value.randomQuotationsRequest as QuotationsRequest.Success).quotations,
                target,
                isSaved
            )
            _uiState.update { it.copy(randomQuotationsRequest = QuotationsRequest.Success(output)) }
        }
    }

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L

        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = this[APPLICATION_KEY] as InspireMeApplication

                HomeViewModel(
                    networkQuotationsRepository = application.container.networkQuotationsRepository,
                    offlineQuotationsRepository = application.container.offlineQuotationRepository
                )
            }
        }
    }
}

data class HomeUiState(
    val randomQuotationsRequest: QuotationsRequest = QuotationsRequest.Loading,
    val savedQuotations: List<QuotationDetails> = emptyList()
)
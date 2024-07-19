package com.example.inspireme.ui.search

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.inspireme.InspireMeApplication
import com.example.inspireme.data.local.QuotationRepository
import com.example.inspireme.data.local.model.Tag
import com.example.inspireme.data.network.QuotationsRepository
import com.example.inspireme.ui.QuotationDetails
import com.example.inspireme.ui.QuotationsRequest
import com.example.inspireme.ui.ViewModelUtils
import com.example.inspireme.ui.toQuotationDetails
import com.example.inspireme.ui.toSavable
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SearchViewModel(
    private val networkQuotationsRepository: QuotationsRepository,
    private val offlineQuotationsRepository: QuotationRepository
): ViewModel()  {
    private val _uiState: MutableStateFlow<SearchUiState> = MutableStateFlow(SearchUiState())
    var searchKeywords: String by mutableStateOf("")
        private set

    val uiState: StateFlow<SearchUiState> = _uiState
        .combine(offlineQuotationsRepository.getAllTags()) { uiState, tags ->
            uiState.copy(categories = tags)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = _uiState.value
        )

    private lateinit var searchingJob: Job

    fun searchQuotations() {
        if (searchKeywords.isNotEmpty()) {
            _uiState.update {
                it.copy(
                    isSearching = true,
                    isSearchingUsingCategory = false
                )
            }

           searchingJob = viewModelScope.launch {
               _uiState.update { it.copy(searchResults = QuotationsRequest.Loading) }

                val idsOfSavedQuotations = offlineQuotationsRepository
                    .getAllQuotationsIds()
                    .first()

               _uiState.update {
                   it.copy(
                       searchResults = ViewModelUtils.catchExceptions {
                           val results = networkQuotationsRepository
                               .searchQuotations(searchKeywords)
                               .results
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
    }

    fun searchQuotationsUsingCategories(categories: String) {
        _uiState.update { it.copy(selectedCategory = categories) }

        if (categories.isNotEmpty()) {
            _uiState.update {
                it.copy(
                    isSearching = true,
                    isSearchingUsingCategory = true
                )
            }


            searchingJob = viewModelScope.launch {
                _uiState.update { it.copy(searchResults = QuotationsRequest.Loading) }

                val idsOfSavedQuotations = offlineQuotationsRepository
                    .getAllQuotationsIds()
                    .first()

                _uiState.update {
                    it.copy(
                        searchResults = ViewModelUtils.catchExceptions {
                            val results = networkQuotationsRepository
                                .searchQuotationsWithCategoryOf(uiState.value.selectedCategory)
                                .results
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
    }

    fun onClear() {
        updateSearchKeywords("")
        _uiState.update {
            it.copy(
                selectedCategory = "",
                isSearching = false
            )
        }

        if (searchingJob.isActive) {
            searchingJob.cancel()
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
        if (uiState.value.searchResults is QuotationsRequest.Success) {
            val output = ViewModelUtils.updateQuotationDetails(
                (uiState.value.searchResults as QuotationsRequest.Success).quotations,
                target,
                isSaved
            )
            _uiState.update { it.copy(searchResults = QuotationsRequest.Success(output)) }
        }
    }

    fun updateSearchKeywords(keywords: String) {
         searchKeywords = keywords
    }

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L

        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = this[APPLICATION_KEY] as InspireMeApplication

                SearchViewModel(
                    networkQuotationsRepository = application.container.networkQuotationsRepository,
                    offlineQuotationsRepository = application.container.offlineQuotationRepository
                )
            }
        }
    }
}

data class SearchUiState(
    val selectedCategory: String = "",
    val searchResults: QuotationsRequest = QuotationsRequest.Loading,
    val isSearching: Boolean = false,
    val isSearchingUsingCategory: Boolean = false,
    val categories: List<Tag> = emptyList()
)



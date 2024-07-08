package com.example.inspireme.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.inspireme.InspireMeApplication
import com.example.inspireme.RANDOM_QUOTATIONS_LIMIT
import com.example.inspireme.data.Quotation
import com.example.inspireme.data.local.QuotationRepository
import com.example.inspireme.data.network.QuotationsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

sealed interface QuotationsRequestState {
    data class Success(val quotations: List<Quotation.NetworkQuotation>) : QuotationsRequestState
    object Error : QuotationsRequestState
    object Loading : QuotationsRequestState
}

class AppViewModel(
    private val networkQuotationsRepository: QuotationsRepository,
    private val offlineQuotationsRepository: QuotationRepository
) : ViewModel()  {

    var quotationsRequestState: QuotationsRequestState by mutableStateOf(QuotationsRequestState.Loading)
        private set

    val uiState: StateFlow<UiState> = offlineQuotationsRepository.getRandomQuotation()
        .map { UiState(quotationsRequestState, it) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UiState(QuotationsRequestState.Loading, emptyList())
        )



    init {
        getRandomQuotations()
    }

    fun onEvent(event: Event) {
        when(event) {
            is Event.ReloadRandomQuotations -> getRandomQuotations()
            is Event.SaveQuotation -> saveQuotation(event.quotation)
        }
    }

    private fun getRandomQuotations(limit: Int = RANDOM_QUOTATIONS_LIMIT) {
        viewModelScope.launch {
            quotationsRequestState = QuotationsRequestState.Loading
            quotationsRequestState = try {
                QuotationsRequestState.Success(networkQuotationsRepository.getRandomQuotations(limit))
            } catch (e: IOException) {
                QuotationsRequestState.Error
            } catch (e: HttpException) {
                QuotationsRequestState.Error
            }
        }
    }

    private fun saveQuotation(quotation: Quotation.NetworkQuotation) {
        viewModelScope.launch {
            offlineQuotationsRepository.insertQuotation(quotation.toSavable())
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = this[APPLICATION_KEY] as InspireMeApplication
                val networkQuotationsRepository = application.container.networkQuotationsRepository
                val offlineQuotationsRepository = application.container.offlineQuotationRepository

                AppViewModel(
                    networkQuotationsRepository = networkQuotationsRepository,
                    offlineQuotationsRepository = offlineQuotationsRepository
                )
            }
        }
    }
}

fun Quotation.NetworkQuotation.toSavable(): Quotation.LocalQuotation {
    return Quotation.LocalQuotation(
        id = id,
        content = content,
        author = author,
        authorSlug = authorSlug
    )
}

data class UiState(
    val quotationsRequestState: QuotationsRequestState,
    val savedRandomQuotations: List<Quotation.LocalQuotation>
)
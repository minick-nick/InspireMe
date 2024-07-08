package com.example.inspireme

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.inspireme.ui.AppViewModel
import com.example.inspireme.ui.HomeScreen
import com.example.inspireme.ui.SearchButton
import com.example.inspireme.ui.SearchScreen

enum class Screen {
    HomeScreen,
    SearchScreen
}

@Composable
fun InspireMeApp(
    modifier: Modifier = Modifier,
    appViewModel: AppViewModel = viewModel(factory = AppViewModel.Factory)
) {
    var currentScreen by remember { mutableStateOf(Screen.HomeScreen) }
    val uiState by appViewModel.uiState.collectAsState()

    Scaffold(
        modifier = modifier,
        floatingActionButton = {
            when(currentScreen) {
                Screen.HomeScreen -> {
                    SearchButton(
                        onSearch = { currentScreen = Screen.SearchScreen }
                    )
                }
                Screen.SearchScreen -> {}
            }
        }
    ) {
        when(currentScreen) {
            Screen.HomeScreen -> {
                HomeScreen(
                    modifier.padding(it),
                    randomQuotationsRequestState = uiState.quotationsRequestState,
                    onEvent = appViewModel::onEvent,
                    savedQuotations = uiState.savedRandomQuotations
                )
            }
            Screen.SearchScreen -> {
                SearchScreen(
                    onBack = { currentScreen = Screen.HomeScreen },
                    modifier = Modifier
                        .padding(
                            top = it.calculateTopPadding(),
                            start = dimensionResource(R.dimen.small_padding),
                            end = dimensionResource(R.dimen.small_padding)
                        )
                )
            }
        }
    }
}
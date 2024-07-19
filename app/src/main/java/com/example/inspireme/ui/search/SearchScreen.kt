package com.example.inspireme.ui.search

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.inspireme.FloatingActionSearchButton
import com.example.inspireme.R
import com.example.inspireme.data.local.model.Tag
import com.example.inspireme.ui.ErrorScreen
import com.example.inspireme.ui.LoadingScreen
import com.example.inspireme.ui.NoMatchesFoundScreen
import com.example.inspireme.ui.QuotationDetails
import com.example.inspireme.ui.QuotationsRequest
import com.example.inspireme.ui.home.QuotationsList
import com.example.inspireme.ui.theme.InspireMeTheme

@Composable
fun SearchScreen(
    viewModel: SearchViewModel = viewModel(factory = SearchViewModel.Factory),
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler(onBack = onBack)

    val uiState = viewModel.uiState.collectAsState()
    val context = LocalContext.current

    Scaffold(modifier = modifier) { padding ->
        SearchBody(
            searchUiState = uiState.value,
            searchKeywords = viewModel.searchKeywords,
            onSelectCategory = { viewModel.searchQuotationsUsingCategories(it) },
            onUpdateSearchKeywords = viewModel::updateSearchKeywords,
            onSearch = { viewModel.searchQuotations() },
            onSaveQuotation = { quotation ->
                if (quotation.isSaved) viewModel.unsavedQuotation(quotation)
                else viewModel.saveQuotation(quotation)
            },
            onShareQuotation = { viewModel.shareQuotation(context, it) },
            onBack = onBack,
            onClear = viewModel::onClear,
            modifier = modifier
                .padding(
                    top = padding.calculateTopPadding(),
                    start = dimensionResource(R.dimen.small_padding),
                    end = dimensionResource(R.dimen.small_padding)
                )
        )
    }
}

@Composable
fun SearchBody(
    searchUiState: SearchUiState,
    searchKeywords: String,
    onSelectCategory: (String) -> Unit,
    onUpdateSearchKeywords: (String) -> Unit,
    onSearch: () -> Unit,
    onSaveQuotation: (QuotationDetails) -> Unit,
    onShareQuotation: (QuotationDetails) -> Unit,
    onBack: () -> Unit,
    onClear: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.small_padding)),
        modifier = modifier.fillMaxSize()
    ) {
        SearchBar(
            searchKeywords = searchKeywords,
            isClearIconShown = searchUiState.isSearching,
            onUpdateSearchKeywords = onUpdateSearchKeywords,
            onSearch = onSearch,
            onBack = onBack,
            onClear = onClear
        )

        if (searchUiState.isSearching) {
            when (searchUiState.searchResults) {
                is QuotationsRequest.Success -> {
                    if (searchUiState.searchResults.quotations.isNotEmpty()) {
                        if (searchUiState.isSearchingUsingCategory) {
                            Text(
                                text = stringResource(R.string.quotations_under_category, searchUiState.selectedCategory),
                                style = MaterialTheme.typography.titleMedium
                            )
                        } else {
                            Text(
                                text = stringResource(R.string.search_results),
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                        QuotationsList(
                            quotations = searchUiState.searchResults.quotations,
                            onSaveQuotation = onSaveQuotation,
                            onShareQuotation = onShareQuotation
                        )
                    } else {
                        NoMatchesFoundScreen()
                    }
                }
                is QuotationsRequest.Loading -> LoadingScreen()
                is QuotationsRequest.Error -> ErrorScreen(
                    onRetry = {
                        if(searchUiState.isSearchingUsingCategory) onSelectCategory(searchUiState.selectedCategory)
                        else onSearch()
                    }
                )
            }
        } else {
            Text(
                text = stringResource(R.string.categories),
                style = MaterialTheme.typography.titleMedium
            )
            CategoriesList(
                categories = searchUiState.categories,
                onSelectCategory = {
                    onSelectCategory(it)
                }
            )
        }
    }
}

@Composable
fun SearchBar(
    searchKeywords: String,
    isClearIconShown: Boolean,
    onBack: () -> Unit,
    onClear: () -> Unit,
    onSearch: () -> Unit,
    onUpdateSearchKeywords: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
        ,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.small_padding))
    ) {
        IconButton(
            onClick = {
                if (isClearIconShown) onClear()
                else onBack()
            },
            modifier = Modifier.size(28.dp)
        ) {
            Icon(
                imageVector = if(isClearIconShown) Icons.Outlined.Close else Icons.Outlined.ArrowBack,
                contentDescription = stringResource(R.string.close_icon)
            )
        }

        TextField(
            value = searchKeywords,
            onValueChange = onUpdateSearchKeywords,
            keyboardActions = KeyboardActions(
                onSearch = { onSearch() },
            ),
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Search
            ),
            textStyle = MaterialTheme.typography.labelLarge,
            placeholder = {
                Text(
                    text = stringResource(R.string.search_quotations),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            shape = RoundedCornerShape(8.dp),
            singleLine = true,
            modifier = Modifier
                .height(50.dp)
                .weight(1f)
        )
        IconButton(
            onClick = onSearch,
            modifier = Modifier.size(28.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.Search,
                contentDescription = stringResource(R.string.search_icon)
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CategoriesList(
    categories: List<Tag>,
    onSelectCategory: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(
            space = dimensionResource(R.dimen.extra_small_padding),
            alignment = Alignment.CenterHorizontally
        ),
        verticalArrangement = Arrangement.spacedBy(
            dimensionResource(R.dimen.extra_small_padding)
        ),
        modifier = modifier
            .padding(bottom = dimensionResource(R.dimen.large_padding))
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        categories.forEach { category ->
            CategoryButton(
                onClick = { onSelectCategory(category.name) },
                categoryName = category.name)
        }
    }
}

@Composable
fun CategoryButton(
    onClick: () -> Unit,
    categoryName: String,
    modifier: Modifier = Modifier
) {
    OutlinedButton(
        onClick = onClick,
        shape = RoundedCornerShape(8.dp),
        contentPadding = PaddingValues(dimensionResource(R.dimen.medium_padding)),
        modifier = modifier
    ) {
        Text(categoryName)
    }
}


@Preview
@Composable
fun SearchScreenPreview() {
    InspireMeTheme {
        Surface {
            SearchBody(
                searchKeywords = "Love",
                searchUiState = SearchUiState(
                    searchResults = QuotationsRequest.Loading,
                    categories = listOf(
                        Tag("", "Age"),
                        Tag("", "Athletics"),
                        Tag("", "Business"),
                        Tag("", "Change"),
                        Tag("", "Character"),
                        Tag("", "Competitions"),
                        Tag("", "Conservative"),
                        Tag("", "Courage"),
                        Tag("", "Creativity"),
                        Tag("", "Education"),
                    ),
                    selectedCategory = "",
                    isSearching = false,
                    isSearchingUsingCategory = false,
                ),
                onSelectCategory = { },
                onUpdateSearchKeywords = { },
                onSearch = { },
                onSaveQuotation = { },
                onShareQuotation = { },
                onClear = { },
                onBack = { }
            )
        }
    }
}


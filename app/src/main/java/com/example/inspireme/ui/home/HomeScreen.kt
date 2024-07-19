package com.example.inspireme.ui.home

import android.content.Intent
import androidx.annotation.StringRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.outlined.Bookmark
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.inspireme.FloatingActionSearchButton
import com.example.inspireme.R
import com.example.inspireme.ui.ErrorScreen
import com.example.inspireme.ui.LoadingScreen
import com.example.inspireme.ui.QuotationDetails
import com.example.inspireme.ui.QuotationsRequest
import com.example.inspireme.ui.ViewModelUtils
import com.example.inspireme.ui.authorImageSrc
import com.example.inspireme.ui.theme.InspireMeTheme
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import kotlinx.coroutines.launch

enum class Tab(@StringRes val title: Int) {
    Random(R.string.random),
    Saved(R.string.saved)
}

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = viewModel(factory = HomeViewModel.Factory),
    navigateToSearch: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState = viewModel.uiState.collectAsState()
    val context = LocalContext.current

    Scaffold(
        floatingActionButton = {
            FloatingActionSearchButton(onSearch = navigateToSearch)
                               },
        modifier = modifier
    ) {
        HomeBody(
            homeUiState = uiState.value,
            onSaveQuotation = { quotation ->
                if (quotation.isSaved) viewModel.unsavedQuotation(quotation)
                else viewModel.saveQuotation(quotation)
            },
            onShareQuotation = { viewModel.shareQuotation(context, it) },
            onReloadRandomQuotations = { viewModel.getRandomQuotations() },
            modifier = Modifier.padding(it)
        )
    }
}

@Composable
fun HomeBody(
    homeUiState: HomeUiState,
    onSaveQuotation: (QuotationDetails) -> Unit,
    onShareQuotation: (QuotationDetails) -> Unit,
    onReloadRandomQuotations: () -> Unit,
    modifier: Modifier = Modifier
) {
    TabsContainer(
        tabs = Tab.values(),
        modifier = modifier
    ) { currentTab ->
        when (currentTab) {
            Tab.Random -> {
                val swipeRefreshState = rememberSwipeRefreshState(
                    isRefreshing = homeUiState.randomQuotationsRequest is QuotationsRequest.Loading
                )

                when(homeUiState.randomQuotationsRequest) {
                    is QuotationsRequest.Success -> {
                        SwipeRefresh(
                            state = swipeRefreshState,
                            onRefresh = onReloadRandomQuotations
                        ) {
                            QuotationsList(
                                quotations = homeUiState.randomQuotationsRequest.quotations,
                                onSaveQuotation = onSaveQuotation,
                                onShareQuotation = onShareQuotation
                            )
                        }
                    }
                    is QuotationsRequest.Loading -> {
                        LoadingScreen()
                    }
                    is QuotationsRequest.Error -> {
                        ErrorScreen(onRetry = onReloadRandomQuotations)
                    }
                }
            }
            Tab.Saved -> {
                QuotationsList(
                    quotations = homeUiState.savedQuotations,
                    onSaveQuotation = onSaveQuotation,
                    onShareQuotation = onShareQuotation
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TabsContainer(
    tabs: Array<Tab>,
    modifier: Modifier = Modifier,
    content: @Composable (Tab) -> Unit
) {
    val pagerState = rememberPagerState(pageCount = { tabs.size })
    val coroutineScope = rememberCoroutineScope()

    Column(modifier = modifier) {
        TabRow(
            selectedTabIndex = pagerState.currentPage
        ) {
            tabs.forEachIndexed { index, tab ->
                Tab(
                    selected = (pagerState.currentPage == index),
                    text = { Text(stringResource(tab.title)) },
                    onClick = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(index)
                        }
                    }
                )
            }
        }
        HorizontalPager(state = pagerState) {
            content(Tab.values()[pagerState.currentPage])
        }
    }
}

@Composable
fun QuotationsList(
    quotations: List<QuotationDetails>,
    onSaveQuotation: (QuotationDetails) -> Unit,
    onShareQuotation: (QuotationDetails) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier = modifier) {
        items(quotations) { quotation ->
            Quotation(
                quotation = quotation,
                onSave = { onSaveQuotation(quotation) },
                onShare = { onShareQuotation(quotation) }
            )
            Divider(
                thickness = 0.5.dp,
                color = MaterialTheme.colorScheme.outlineVariant
            )
        }
    }
}

@Composable
fun Quotation(
    quotation: QuotationDetails,
    onSave: () -> Unit,
    onShare: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(dimensionResource(R.dimen.medium_padding)),
        horizontalArrangement = Arrangement
            .spacedBy(dimensionResource(R.dimen.small_padding))
    ) {
        AsyncImage(
            model = ImageRequest.Builder(context = LocalContext.current)
                .data(quotation.authorImageSrc)
                .crossfade(true)
                .build()
            ,
            modifier = Modifier
                .clip(CircleShape)
                .size(40.dp),
            contentDescription = null
        )
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.extra_small_padding))
        ) {
            Text(
                text = quotation.author,
                style = MaterialTheme.typography.titleMedium
            )
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = Color.Transparent
                ),
                border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Text(
                    text = quotation.content,
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(
                            horizontal = dimensionResource(R.dimen.large_padding),
                            vertical = 36.dp
                        )
                        .fillMaxWidth()
                )
            }
            Options(
                iconButtonModifier = Modifier.size(dimensionResource(R.dimen.medium_icon_button_size)),
                quotation = quotation,
                onSave = onSave,
                onShare = onShare
            )
        }
    }
}

@Composable
fun Options(
    iconButtonModifier: Modifier,
    quotation: QuotationDetails,
    onSave: () -> Unit,
    onShare: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(
            space = dimensionResource(R.dimen.small_padding),
            alignment = Alignment.End
        )
    ) {
        IconToggleButton(
            checked = quotation.isSaved,
            onCheckedChange = { onSave() },
            modifier = iconButtonModifier
        ) {
            if (quotation.isSaved) {
                Icon(
                    imageVector = Icons.Outlined.Bookmark,
                    contentDescription = null,
                    tint = Color(0xFF2178B7)
                )
            } else {
                Icon(
                    imageVector = Icons.Filled.BookmarkBorder,
                    contentDescription = null
                )
            }
        }
        IconButton(
            onClick = onShare,
            modifier = iconButtonModifier
        ) {
            Icon(
                imageVector = Icons.Outlined.Share,
                contentDescription = null
            )
        }
    }
}

@Preview
@Composable
fun HomeScreenPreview() {
    InspireMeTheme {
        Surface {
            HomeBody(
                homeUiState = HomeUiState(
                    savedQuotations = emptyList(),
                    randomQuotationsRequest = QuotationsRequest.Success(
                        listOf(
                            QuotationDetails(
                                id = "",
                                author = "Bruce Lee",
                                content = "Mistakes are always forgivable, if one has the courage to admit them.",
                                tags = listOf(),
                                authorSlug = "",
                                isSaved = true
                            ),
                            QuotationDetails(
                                id = "",
                                author = "Laozi",
                                content = "He who controls others may be powerful, but he who has mastered himself is mightier still.",
                                tags = listOf(),
                                authorSlug = "",
                                isSaved = false
                            )
                        )
                    )
                ),
                onSaveQuotation = { },
                onShareQuotation = { },
                onReloadRandomQuotations = { }
            )
        }
    }
}

@Preview
@Composable
fun QuotationPreview() {
    InspireMeTheme {
        Surface {
            Quotation(
                quotation = QuotationDetails(
                    id = "",
                    author = "Bruce Lee",
                    content = "Mistakes are always forgivable, if one has the courage to admit them.",
                    tags = listOf(),
                    authorSlug = "",
                    isSaved = true
                ),
                onSave = {},
                onShare = {}
            )
        }
    }
}

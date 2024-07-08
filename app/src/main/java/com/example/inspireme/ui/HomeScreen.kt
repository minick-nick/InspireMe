package com.example.inspireme.ui

import androidx.annotation.StringRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.Bookmark
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.inspireme.R
import com.example.inspireme.data.Quotation
import com.example.inspireme.ui.theme.InspireMeTheme
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import kotlinx.coroutines.launch


@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    onEvent: (Event) -> Unit,
    randomQuotationsRequestState: QuotationsRequestState,
    savedQuotations: List<Quotation.LocalQuotation>
) {
    val tabs = listOf(
        Tab(title = R.string.random),
        Tab(title = R.string.favorites),
        Tab(title = R.string.saved)
    )
    Tabs(
        modifier = modifier,
        tabs = tabs
    ) { currentPage ->
        when (currentPage) {
            0 -> {
                val swipeRefreshState = rememberSwipeRefreshState(
                    isRefreshing = randomQuotationsRequestState is QuotationsRequestState.Loading
                )

                when(randomQuotationsRequestState) {
                    is QuotationsRequestState.Success -> {
                        SwipeRefresh(
                            state = swipeRefreshState,
                            onRefresh = { onEvent(Event.ReloadRandomQuotations) }
                        ) {
                            QuotationsList(
                                quotations = randomQuotationsRequestState.quotations,
                                onEvent = onEvent
                            )
                        }
                    }
                    is QuotationsRequestState.Loading -> {
                        LoadingScreen()
                    }
                    is QuotationsRequestState.Error -> {
                        ErrorScreen(onEvent = onEvent)
                    }
                }
            }
            1 -> {
                Text("Current page is $currentPage")
            }
            2 -> {
                QuotationsList(
                    quotations = savedQuotations,
                    onEvent = onEvent
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Tabs(
    modifier: Modifier = Modifier,
    tabs: List<Tab>,
    content: @Composable (Int) -> Unit
) {
    val pagerState = rememberPagerState(pageCount = { tabs.size })
    val coroutineScope = rememberCoroutineScope()

    Column(modifier = modifier) {
        TabRow(selectedTabIndex = pagerState.currentPage) {
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
            content(pagerState.currentPage)
        }
    }
}

@Composable
fun QuotationsList(
    modifier: Modifier = Modifier,
    quotations: List<Quotation>,
    onEvent: (Event) -> Unit
) {
    LazyColumn(modifier = modifier) {
        items(quotations) {
            Quotation(
                quotation = it,
                onEvent = onEvent
            )
            Divider(
                thickness = 0.5.dp,
                color = MaterialTheme.colorScheme.outlineVariant
            )
        }
    }
}

@Composable
fun SearchButton(
    modifier: Modifier = Modifier,
    onSearch: () -> Unit
) {
    FloatingActionButton(
        onClick = onSearch,
        modifier = modifier
    ) {
        Icon(
            imageVector = Icons.Rounded.Search,
            contentDescription = null
        )
    }
}

@Composable
fun Quotation(
    quotation: Quotation,
    onEvent: (Event) -> Unit,
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
                .data("https://images.quotable.dev/profile/200/${
                    when(quotation) {
                        is Quotation.LocalQuotation -> {quotation.authorSlug}
                        is Quotation.NetworkQuotation -> {quotation.authorSlug}
                    }
                }.jpg")
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
                text = when(quotation) {
                    is Quotation.LocalQuotation -> {quotation.author}
                    is Quotation.NetworkQuotation -> {quotation.author}
                },
                style = MaterialTheme.typography.titleMedium
            )
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = Color.Transparent
                ),
                border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Text(
                    text = when(quotation) {
                        is Quotation.LocalQuotation -> {quotation.content}
                        is Quotation.NetworkQuotation -> {quotation.content}
                    },
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(
                            horizontal = dimensionResource(R.dimen.large_padding),
                            vertical = 36.dp
                        )
                )
            }
            Options(
                iconButtonModifier = Modifier.size(dimensionResource(R.dimen.medium_icon_button_size)),
                onSave = {
                    when(quotation) {
                        is Quotation.LocalQuotation -> { }
                        is Quotation.NetworkQuotation ->  onEvent(Event.SaveQuotation(quotation))
                    }
                         },
                onFavorite = {  },
                onShare = {  }
            )
        }
    }
}

@Composable
fun Options(
    modifier: Modifier = Modifier,
    iconButtonModifier: Modifier,
    onSave: () -> Unit,
    onFavorite: () -> Unit,
    onShare: () -> Unit
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(
            space = dimensionResource(R.dimen.small_padding),
            alignment = Alignment.End
        )
    ) {
        var isSaved by remember { mutableStateOf(false) }
        var isFavorite by remember { mutableStateOf(false) }


        IconToggleButton(
            checked = isSaved,
            onCheckedChange = {
                isSaved = it
            },
            modifier = iconButtonModifier
        ) {
            if (isSaved) {
                Icon(
                    imageVector = Icons.Outlined.Bookmark,
                    contentDescription = null,
                    tint = Color(0xFF2178B7)
                )
            } else {
                onSave()
                Icon(
                    imageVector = Icons.Filled.BookmarkBorder,
                    contentDescription = null
                )
            }
        }

        IconToggleButton(
            checked = isFavorite,
            onCheckedChange = { isFavorite = it },
            modifier = iconButtonModifier
        ) {
            if (isFavorite) {
                Icon(
                    imageVector = Icons.Filled.Favorite,
                    contentDescription = null,
                    tint = Color(0xFFF51B1B)
                )
            } else {
                onFavorite()
                Icon(
                    imageVector = Icons.Outlined.FavoriteBorder,
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

@Composable
fun LoadingScreen(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize()) {
        Image(
            modifier = Modifier
                .size(300.dp)
                .align(Alignment.Center),
            painter = painterResource(R.drawable.loading_img),
            contentDescription = stringResource(R.string.loading)
        )
    }
}

@Composable
fun ErrorScreen(
    onEvent: (Event) -> Unit,
    modifier: Modifier = Modifier)
{
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_connection_error), contentDescription = ""
        )
        Text(
            text = stringResource(R.string.loading_failed),
            style = LocalTextStyle.current.copy(
                fontFamily = FontFamily.Default
            ),
            modifier = Modifier.padding(16.dp))
        Button(onClick = { onEvent(Event.ReloadRandomQuotations) }) {
            Text(stringResource(R.string.retry))
        }
    }
}

data class Tab(@StringRes val title: Int)

@Preview
@Composable
fun HomeScreenPreview() {
    InspireMeTheme {
        Surface {
            HomeScreen(
                randomQuotationsRequestState = QuotationsRequestState.Loading,
                savedQuotations = listOf(),
                onEvent = {}
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
                quotation = Quotation.NetworkQuotation(
                    id = "",
                    author = "",
                    content = "",
                    tags = listOf(),
                    authorSlug = ""
                ),
                onEvent = {}
            )
        }
    }
}

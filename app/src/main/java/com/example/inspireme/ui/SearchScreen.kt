package com.example.inspireme.ui

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.inspireme.R
import com.example.inspireme.ui.theme.InspireMeTheme


@Composable
fun SearchScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement
            .spacedBy(dimensionResource(R.dimen.small_padding)),
        modifier = modifier
            .fillMaxSize()
    ) {
        val categorySuggestions = listOf(
            "Age",
            "Athletics",
            "Business",
            "Change",
            "Character",
            "Competition",
            "Conservative",
            "Courage",
            "Creativity",
            "Education",
            "Ethics",
            "Failure"
        )
        SearchBar(
            keywords = "",
            onKeywordsChange = {},
            onBack = onBack,
            onSearch = {}
        )
        Text(
            text = stringResource(R.string.categories),
            style = MaterialTheme.typography.titleMedium
        )
        CategoriesList(
            categorySuggestions = categorySuggestions
        )
    }
}

@Composable
fun SearchBar(
    onBack: () -> Unit,
    onSearch: () -> Unit,
    keywords: String,
    onKeywordsChange: (String) -> Unit,
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
            onClick = onBack,
            modifier = Modifier.size(28.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.Close,
                contentDescription = stringResource(R.string.close_icon)
            )
        }
        TextField(
            value = keywords,
            onValueChange = onKeywordsChange,
            placeholder = {
                Text(
                    text = stringResource(R.string.search_quotations),
                    style = MaterialTheme.typography.labelLarge
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
    modifier: Modifier = Modifier,
    categorySuggestions: List<String>
) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(
            dimensionResource(R.dimen.small_padding)
        ),
        verticalArrangement = Arrangement.spacedBy(
            dimensionResource(R.dimen.extra_small_padding)
        ),
        modifier = modifier
            .fillMaxSize()
    ) {
        categorySuggestions.forEach { category ->
            CategoryButton(
                onClick = { /*TODO*/ },
                categoryName = category)
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
            SearchScreen(
                onBack = {}
            )
        }
    }
}


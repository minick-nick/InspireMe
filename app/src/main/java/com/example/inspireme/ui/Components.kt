package com.example.inspireme.ui

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.startActivity
import com.example.inspireme.R
import com.example.inspireme.ui.theme.InspireMeTheme


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
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
)
{
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_connection_error),
            contentDescription = stringResource(R.string.connection_error)
        )
        Text(
            text = stringResource(R.string.loading_failed),
            style = LocalTextStyle.current.copy(
                fontFamily = FontFamily.Default
            ),
            modifier = Modifier.padding(dimensionResource(R.dimen.large_padding)))
        Button(onClick = onRetry) {
            Text(stringResource(R.string.retry))
        }
    }
}

@Composable
fun NoMatchesFoundScreen(
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxSize()
    ) {
        Image(
            painter = painterResource(R.drawable.no_results_found),
            contentDescription = stringResource(R.string.no_results_found),
            modifier = Modifier.size(100.dp)
        )
        Text(
            text = stringResource(R.string.no_matches_found),
            textAlign = TextAlign.Center,
            style = LocalTextStyle.current.copy(
                fontFamily = FontFamily.Default
            ),
            modifier = Modifier.padding(top = dimensionResource(R.dimen.large_padding))
        )
    }
}

@Preview(showBackground = true)
@Composable
fun LoadingScreenPreview() {
    InspireMeTheme {
        Surface {
            LoadingScreen()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ErrorScreenPreview() {
    InspireMeTheme {
        Surface {
            ErrorScreen(onRetry = { })
        }
    }
}

@Preview(showBackground = true)
@Composable
fun NoMatchesFoundScreenPreview() {
    InspireMeTheme {
        Surface {
            NoMatchesFoundScreen()
        }
    }
}

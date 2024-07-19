package com.example.inspireme

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.inspireme.ui.home.HomeScreen
import com.example.inspireme.ui.search.SearchScreen

enum class Screen {
    HomeScreen,
    SearchScreen
}

@Composable
fun InspireMeApp(
    modifier: Modifier = Modifier,
) {
    val navController: NavHostController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.HomeScreen.name
    ) {
        composable(route = Screen.HomeScreen.name) {
            HomeScreen(navigateToSearch = { navController.navigate(Screen.SearchScreen.name) })
        }
        composable(route = Screen.SearchScreen.name) {
            SearchScreen(onBack = { navController.popBackStack() })
        }
    }
}


@Composable
fun FloatingActionSearchButton(
    onSearch: () -> Unit,
    modifier: Modifier = Modifier
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

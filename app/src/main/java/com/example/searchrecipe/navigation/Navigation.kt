package com.example.searchrecipe.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.example.common.navigation.NavigationSubGraphRoute

@Composable
fun RecipeNavigation(
    modifier: Modifier = Modifier,
    navigationSubGraphs: NavigationSubGraphs
){
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = NavigationSubGraphRoute.Search.route,
    ){
        navigationSubGraphs.searchFeatureApi.registerGraph(
            navGraphBuilder = this,
            navHostController = navController
        )
        navigationSubGraphs.mediaPlayerApi.registerGraph(
            navGraphBuilder = this,
            navHostController = navController
        )
    }
}
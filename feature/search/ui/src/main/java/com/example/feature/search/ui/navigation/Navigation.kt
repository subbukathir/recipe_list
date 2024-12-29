package com.example.feature.search.ui.navigation

import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.example.common.navigation.FeatureApi
import com.example.common.navigation.NavigationRoute
import com.example.common.navigation.NavigationSubGraphRoute
import com.example.feature.search.ui.screens.details.RecipeDetail
import com.example.feature.search.ui.screens.details.RecipeDetailViewModel
import com.example.feature.search.ui.screens.details.RecipeDetailsScreen
import com.example.feature.search.ui.screens.favorite.Favorite
import com.example.feature.search.ui.screens.favorite.FavoriteScreen
import com.example.feature.search.ui.screens.favorite.FavoriteViewModel
import com.example.feature.search.ui.screens.recipe_list.RecipeList
import com.example.feature.search.ui.screens.recipe_list.RecipeListScreen
import com.example.feature.search.ui.screens.recipe_list.RecipeListViewModel

interface SearchFeatureApi : FeatureApi

class SearchFeatureApiImpl : SearchFeatureApi {
    override fun registerGraph(
        navGraphBuilder: NavGraphBuilder,
        navHostController: NavHostController
    ) {
        navGraphBuilder.navigation(
            route = NavigationSubGraphRoute.Search.route,
            startDestination = NavigationRoute.RecipeList.route,
        ) {
            composable(route = NavigationRoute.RecipeList.route) {
                val viewModel: RecipeListViewModel = hiltViewModel<RecipeListViewModel>()
                RecipeListScreen(
                    navHostController = navHostController,
                    viewModel = viewModel,
                    onClickOfFavorite = {
                        viewModel.onEvent(RecipeList.Event.GoToFavorite)
                    },
                    onClick = { mealId ->
                        viewModel.onEvent(RecipeList.Event.GoToRecipeDetail(mealId))
                    }
                )
            }

            composable(route = NavigationRoute.RecipeDetails.route) {
                val mealId = it.arguments?.getString("id") ?: ""
                val viewModel = hiltViewModel<RecipeDetailViewModel>()

                LaunchedEffect(key1 = mealId) {
                    mealId?.let { id ->
                        viewModel.onEvent(RecipeDetail.Event.FetchRecipeDetails(id))
                    }
                }
                RecipeDetailsScreen(viewModel = viewModel,
                    navHostController = navHostController,
                    onBackPressed = { viewModel.onEvent(RecipeDetail.Event.GoToRecipeListScreen) },
                    onDeletedClicked = {
                        viewModel.onEvent(RecipeDetail.Event.DeleteRecipe(it))
                    },
                    onFavouriteClicked = {
                        viewModel.onEvent(RecipeDetail.Event.InsertRecipe(it))
                    },
                    onWatchYoutubeClicked = { videoId ->
                        viewModel.onEvent(RecipeDetail.Event.GoToMediaPlayerScreen(videoId))
                    }
                )
            }

            composable(route = NavigationRoute.Favorite.route) {
                val viewModel = hiltViewModel<FavoriteViewModel>()
                FavoriteScreen(
                    viewModel = viewModel,
                    navHostController = navHostController,
                    onClick = { mealId ->
                        viewModel.onEvent(Favorite.Event.GoToRecipeDetail(mealId))
                    },
                    onNavigationClick = {
                        viewModel.onEvent(Favorite.Event.GoToRecipeListScreen)
                    },
                    onDeleteRecipe = {
                        viewModel.onEvent(Favorite.Event.DeleteRecipe(it))
                    }
                )
            }
        }
    }

}
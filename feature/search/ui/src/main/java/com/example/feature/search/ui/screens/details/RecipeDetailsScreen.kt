@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.feature.search.ui.screens.details

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.common.navigation.NavigationRoute
import com.example.common.utils.UiText
import com.example.search.domain.model.RecipeDetails
import kotlinx.coroutines.flow.collectLatest

@Composable
fun RecipeDetailsScreen(
    modifier: Modifier = Modifier,
    viewModel: RecipeDetailViewModel,
    navHostController: NavHostController,
    onBackPressed: () -> Unit,
    onFavouriteClicked: (RecipeDetails) -> Unit,
    onDeletedClicked: (RecipeDetails) -> Unit,
    onWatchYoutubeClicked: (String) -> Unit
) {

    val uiState = viewModel.uiState.collectAsState()

    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(key1 = viewModel.viewModelScope) {
        viewModel.navigation.flowWithLifecycle(lifecycleOwner.lifecycle)
            .collectLatest { navigation ->
                when (navigation) {
                    is RecipeDetail.Navigation.GoToRecipeListScreen -> navHostController.popBackStack()
                    is RecipeDetail.Navigation.GoToMediaPlayerScreen -> {
                        navHostController.navigate(
                            NavigationRoute.MediaPlayer.sendVideoId(navigation.videoId)
                        )
                    }
                }
            }
    }


    Scaffold(
        modifier = Modifier,
        topBar = {
            TopAppBar(
                modifier = Modifier,
                title = {
                    Text(
                        text = uiState.value.data?.strMeal.toString(),
                        style = MaterialTheme.typography.titleLarge,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1,
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                    )
                },
                navigationIcon = {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = null,
                        modifier = Modifier
                            .clickable { onBackPressed() }
                    )
                },
                actions = {
                    IconButton(
                        onClick = {
                            uiState.value.data?.let {
                                onFavouriteClicked(it)
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null
                        )
                    }
                    IconButton(
                        onClick = {
                            uiState.value.data?.let {
                                onDeletedClicked(it)
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = null
                        )
                    }
                },
            )
        }
    ) {

        if (uiState.value.isLoading) {
            Box(
                modifier = Modifier.padding(it).fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        if (uiState.value.error !is UiText.Idle) {
            Box(
                modifier = Modifier
                    .padding(it)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(text = uiState.value.error.toString() ?: "Unknown error")
            }
        }

        uiState.value.data?.let { recipeDetail ->
            Column(
                modifier = Modifier
                    .padding(it)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {

                AsyncImage(
                    model = recipeDetail.strMealThumb,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(350.dp),
                    contentScale = ContentScale.Crop,
                    contentDescription = "Recipe Image"
                )

                Spacer(modifier = Modifier.height(24.dp))

                Column(
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Text(
                        text = recipeDetail.strInstructions,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    recipeDetail.ingredientsPair.forEach {
                        if (it.first.isNotEmpty() || it.second.isNotEmpty()) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                AsyncImage(
                                    model = getIngredientsImageUrl(it.first),
                                    contentDescription = null,
                                    modifier = Modifier.size(60.dp)
                                        .clip(CircleShape)
                                )
                                Text(
                                    text = it.second,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    if(recipeDetail.strYoutube.isNotEmpty()){
                        Text(
                            text = "Watch youtube video",
                            modifier = Modifier
                                .clickable {
                                    onWatchYoutubeClicked(recipeDetail.strYoutube.split("v=").last())
                                }
                        )

                        Spacer(modifier = Modifier.height(32.dp))
                    }
                }

            }
        }
    }
}

fun getIngredientsImageUrl(name: String): String =
    "https://www.themealdb.com/images/ingredients/$name.png"
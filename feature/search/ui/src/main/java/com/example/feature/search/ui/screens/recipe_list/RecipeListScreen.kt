@file:OptIn(ExperimentalLayoutApi::class)

package com.example.feature.search.ui.screens.recipe_list

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.flowWithLifecycle
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.common.navigation.NavigationRoute
import com.example.common.utils.UiText
import kotlinx.coroutines.flow.collectLatest

@Composable
fun  RecipeListScreen(
    modifier: Modifier = Modifier,
    viewModel: RecipeListViewModel = hiltViewModel(),
    navHostController: NavHostController,
    onClick: (String) -> Unit = {},
    onClickOfFavorite: () -> Unit
){
    val query = remember { mutableStateOf("") }
    val uiState = viewModel.uiState.collectAsState()

    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(key1 = viewModel.navigation){
        viewModel.navigation.flowWithLifecycle(lifecycleOwner.lifecycle)
            .collectLatest {
                when(it){
                    is RecipeList.Navigation.GoToRecipeDetails -> {
                        navHostController.navigate(NavigationRoute.RecipeDetails.sendId(it.id))
                    }

                    RecipeList.Navigation.GoToFavorite -> {
                        navHostController.navigate(NavigationRoute.Favorite.route)
                    }
                }
            }
    }

    Scaffold(
        modifier = modifier
            .padding(horizontal = 16.dp, vertical = 8.dp),
        topBar = {
            TextField(
                value = query.value,
                onValueChange = {
                    query.value = it
                    viewModel.onEvent(RecipeList.Event.SearchRecipe(query.value))
                },
                placeholder = { Text(
                    text = "Search Recipe...",
                    style = MaterialTheme.typography.bodySmall
                ) },
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                ),
                modifier = Modifier.fillMaxWidth()
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    onClickOfFavorite()
                }
            ){
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "Goto favorite"
                )
            }
        }
    ){
        if(uiState.value.isLoading){
            Box(
                modifier = Modifier
                    .padding(it)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ){
                CircularProgressIndicator()
            }
        }

        if(uiState.value.error !is UiText.Idle){
            Box(
                modifier = Modifier
                    .padding(it)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ){
                Text(text = uiState.value.error.toString() ?: "Unknown error")
            }
        }

        uiState.value.data?.let { list ->
            LazyColumn(
                modifier = Modifier
                    .padding(it)
                    .fillMaxSize()
            ){
                items(list){
                    Card(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 12.dp, vertical = 4.dp)
                            .clickable { onClick(it.idMeal ) },
                        shape = RoundedCornerShape(12.dp)
                    ){
                         AsyncImage(
                             model = it.strMealThumb,
                             contentDescription = null,
                             modifier = Modifier.fillMaxWidth()
                                 .height(250.dp),
                             contentScale = ContentScale.Crop
                         )

                        Spacer(modifier = Modifier.height(12.dp))

                        Column(
                            modifier = Modifier
                                .padding(vertical = 12.dp)
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                        ) {
                            Text(
                                text = it.strMeal,
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = it.strInstructions,
                                style = MaterialTheme.typography.bodyMedium,
                                overflow = TextOverflow.Ellipsis,
                                maxLines = 4
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            if(it.strTags.isNotEmpty()) {
                                FlowRow {
                                    it.strTags.split(",")
                                        .onEach {
                                            Box(
                                                modifier = Modifier
                                                    .wrapContentSize()
                                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                                                    .background(
                                                        color = Color.White,
                                                        shape = RoundedCornerShape(24.dp)
                                                    )
                                                    .clip(RoundedCornerShape(24.dp))
                                                    .border(
                                                        width = 1.dp,
                                                        color = Color.Red ,
                                                        shape = RoundedCornerShape(24.dp)
                                                    ),
                                                contentAlignment = Alignment.Center
                                            ){
                                                Text(
                                                    text = it,
                                                    style = MaterialTheme.typography.bodySmall,
                                                    modifier = Modifier.padding(
                                                        vertical = 6.dp,
                                                        horizontal = 12.dp
                                                    )
                                                )
                                            }
                                        }
                                }
                            }
                        }

                    }
                }
                item {
                    Spacer(modifier = Modifier.height(66.dp))
                }
            }
        }
    }
}
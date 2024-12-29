package com.example.feature.search.ui.screens.favorite

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.flowWithLifecycle
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.common.navigation.NavigationRoute
import com.example.common.utils.UiText
import com.example.search.domain.model.Recipe
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun FavoriteScreen(
    viewModel: FavoriteViewModel,
    navHostController: NavHostController,
    onClick: (String) -> Unit,
    onNavigationClick: () -> Unit,
    onDeleteRecipe: (Recipe) -> Unit
){

    val showDropDownMenu = rememberSaveable { mutableStateOf(false) }

    val selectedIndex = rememberSaveable { mutableStateOf(-1) }

    val uiState = viewModel.uiState.collectAsState()

    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(key1 = viewModel.navigation){
        viewModel.navigation.flowWithLifecycle(lifecycleOwner.lifecycle)
            .collectLatest {
                when(it){
                    is Favorite.Navigation.GoToRecipeDetails -> {
                        navHostController.navigate(NavigationRoute.RecipeDetails.sendId(it.id))
                    }

                    Favorite.Navigation.GoToRecipeListScreen -> {
                        navHostController.popBackStack()
                    }
                }
            }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Favorite",
                        style = MaterialTheme.typography.titleLarge,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            onNavigationClick()
                        }
                    ){
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = null
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            showDropDownMenu.value = showDropDownMenu.value.not()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Overflow Icon"
                        )
                    }

                    if(showDropDownMenu.value){
                     DropdownMenu(
                         expanded = showDropDownMenu.value,
                         onDismissRequest = { showDropDownMenu.value = showDropDownMenu.value.not() }
                     ) {
                         DropdownMenuItem(
                             text = { Text(text = "Alphabetical") },
                             onClick = {
                                 selectedIndex.value = 0
                                 showDropDownMenu.value = showDropDownMenu.value.not()
                                 viewModel.onEvent(Favorite.Event.AlphabeticalSort)
                             },
                             leadingIcon = {
                                 RadioButton(
                                     selected = selectedIndex.value == 0,
                                     onClick = {
                                         selectedIndex.value = 0
                                         showDropDownMenu.value = showDropDownMenu.value.not()
                                         viewModel.onEvent(Favorite.Event.AlphabeticalSort)
                                     }
                                 )
                             }
                         )

                         DropdownMenuItem(
                             text = { Text(text = "Less Ingredients") },
                             onClick = {
                                 selectedIndex.value = 1
                                 showDropDownMenu.value = showDropDownMenu.value.not()
                                 viewModel.onEvent(Favorite.Event.LessIngredientsSort)
                             },
                             leadingIcon = {
                                 RadioButton(
                                     selected = selectedIndex.value == 1,
                                     onClick = {
                                         selectedIndex.value = 1
                                         showDropDownMenu.value = showDropDownMenu.value.not()
                                         viewModel.onEvent(Favorite.Event.LessIngredientsSort)
                                     }
                                 )
                             }
                         )

                         DropdownMenuItem(
                             text = { Text(text = "Reset") },
                             onClick = {
                                 selectedIndex.value = 2
                                 showDropDownMenu.value = showDropDownMenu.value.not()
                                 viewModel.onEvent(Favorite.Event.ResetSort)
                             },
                             leadingIcon = {
                                 RadioButton(
                                     selected = selectedIndex.value == 2,
                                     onClick = {
                                         selectedIndex.value = 2
                                         showDropDownMenu.value = showDropDownMenu.value.not()
                                         viewModel.onEvent(Favorite.Event.ResetSort)
                                     }
                                 )
                             }
                         )

                     }
                    }
                }
            )

        }
    ){

        if(uiState.value.isLoading){
            Box(
                modifier = Modifier.fillMaxSize()
                    .padding(it)
            ){
                CircularProgressIndicator()
            }
        }

        if(uiState.value.error !is UiText.Idle){
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it)
            ){
                Text(
                    modifier = Modifier
                        .wrapContentSize(),
                    text = uiState.value.error.toString(),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        val list = uiState.value.data ?: emptyList()
        if(list.isNotEmpty()){
            LazyColumn(
                modifier = Modifier
                    .padding(it)
                    .fillMaxSize()
            ){
                items(list){ recipe ->
                    Card(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 12.dp, vertical = 4.dp)
                            .clickable { onClick(recipe.idMeal ) },
                        shape = RoundedCornerShape(12.dp)
                    ){
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(),
                        ){
                            AsyncImage(
                                model = recipe.strMealThumb,
                                contentDescription = null,
                                modifier = Modifier.fillMaxWidth()
                                    .height(250.dp),
                                contentScale = ContentScale.Crop
                            )
                            IconButton(
                                onClick = {
                                    onDeleteRecipe(recipe)
                                },
                                modifier = Modifier
                                    .padding(16.dp)
                                    .background(color = Color.White, shape = CircleShape)
                                    .align(Alignment.TopEnd)){
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Delete Recipe",
                                    tint = Color.Red
                                )
                            }

                        }


                        Spacer(modifier = Modifier.height(12.dp))

                        Column(
                            modifier = Modifier
                                .padding(vertical = 12.dp)
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                        ) {
                            Text(
                                text = recipe.strMeal,
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = recipe.strInstructions,
                                style = MaterialTheme.typography.bodyMedium,
                                overflow = TextOverflow.Ellipsis,
                                maxLines = 4
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            if(recipe.strTags.isNotEmpty()) {
                                FlowRow {
                                    recipe.strTags.split(",")
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
        }else{
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it),
                contentAlignment = Alignment.Center){
                Text(
                    text = "No favorite recipe found",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}


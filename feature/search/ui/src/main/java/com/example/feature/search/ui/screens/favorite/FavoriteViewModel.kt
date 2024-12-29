package com.example.feature.search.ui.screens.favorite

import androidx.compose.runtime.collectAsState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.common.utils.UiText
import com.example.search.domain.model.Recipe
import com.example.search.domain.use_cases.DeleteRecipeUseCase
import com.example.search.domain.use_cases.GetAllRecipesFromLocalDBUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoriteViewModel @Inject constructor(
    private val deleteRecipeUseCase: DeleteRecipeUseCase,
    private val getAllRecipesFromLocalDBUseCase: GetAllRecipesFromLocalDBUseCase
) : ViewModel(){

    private var originalList: List<Recipe> = mutableListOf<Recipe>()

    private val _uiState = MutableStateFlow(Favorite.UiState())
    val uiState:StateFlow<Favorite.UiState> = _uiState.asStateFlow()

    private val _navigation = Channel<Favorite.Navigation>()
    val navigation: Flow<Favorite.Navigation> = _navigation.receiveAsFlow()

    init {
        getAllRecipes()
    }

    fun onEvent(event: Favorite.Event){
        when(event){
            is Favorite.Event.DeleteRecipe -> {
                deleteRecipeUseCase.invoke(event.recipe).launchIn(viewModelScope)
            }

            is Favorite.Event.GoToRecipeDetail -> {
                viewModelScope.launch {
                    _navigation.send(Favorite.Navigation.GoToRecipeDetails(event.id))
                }
            }

            Favorite.Event.AlphabeticalSort -> alphabeticalOrder()
            Favorite.Event.LessIngredientsSort -> lessIngredients()
            Favorite.Event.ResetSort -> resetSort()
            Favorite.Event.GoToRecipeListScreen -> {
                viewModelScope.launch {
                    _navigation.send(Favorite.Navigation.GoToRecipeListScreen)
                }
            }
        }
    }

    private fun getAllRecipes(){
        viewModelScope.launch {
            getAllRecipesFromLocalDBUseCase.invoke().collectLatest { list ->
                originalList = list
                _uiState.value = Favorite.UiState(data = list)
            }
        }
    }

    private fun alphabeticalOrder(){
        _uiState.update {
            Favorite.UiState(data = originalList.sortedBy { it.strMeal })
        }
    }

    private fun lessIngredients(){
        _uiState.update {
            Favorite.UiState(data = originalList.sortedBy { it.strInstructions.length })
        }
    }

    private fun resetSort(){
        _uiState.update {
            Favorite.UiState(data = originalList)
        }
    }
}

object Favorite{
    data class UiState(
        val isLoading: Boolean = false,
        val data: List<Recipe>? = null,
        val error: UiText = UiText.Idle
    )

    sealed interface Navigation {
        data class GoToRecipeDetails(val id: String) : Navigation
        data object GoToRecipeListScreen : Navigation
    }

    sealed interface Event {
        data object AlphabeticalSort: Event
        data object LessIngredientsSort: Event
        data object ResetSort: Event

        data class GoToRecipeDetail(val id: String) : Event
        data object GoToRecipeListScreen : Event

        data class DeleteRecipe(val recipe: Recipe) : Event
    }
}
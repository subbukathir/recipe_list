package com.example.feature.search.ui.screens.recipe_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.common.utils.NetworkResult
import com.example.common.utils.UiText
import com.example.search.domain.model.Recipe
import com.example.search.domain.use_cases.GetAllRecipeUseCase
import com.example.search.domain.use_cases.GetAllRecipesFromLocalDBUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecipeListViewModel @Inject constructor(
    private val getAllRecipeUseCase: GetAllRecipeUseCase,
    private val getAllRecipesFromLocalDBUseCase: GetAllRecipesFromLocalDBUseCase
): ViewModel() {

    private val _uiState = MutableStateFlow(RecipeList.UiState())
    val uiState:StateFlow<RecipeList.UiState> = _uiState.asStateFlow()

    private val _navigation = Channel<RecipeList.Navigation>()
    val navigation: Flow<RecipeList.Navigation> = _navigation.receiveAsFlow()

    fun onEvent(event: RecipeList.Event) {
        when (event) {
            is RecipeList.Event.SearchRecipe -> {
                search(event.query)
            }

            is RecipeList.Event.GoToRecipeDetail -> {
                viewModelScope.launch {
                    _navigation.send(RecipeList.Navigation.GoToRecipeDetails(event.id))
                }
            }

            RecipeList.Event.GoToFavorite -> viewModelScope.launch {
                _navigation.send(RecipeList.Navigation.GoToFavorite)
            }
        }
    }

    private fun search(query: String) = getAllRecipeUseCase.invoke(query)
        .onEach { result ->
            when(result){
                is NetworkResult.Success -> {
                    _uiState.update { RecipeList.UiState(data = result.data) }
                }
                is NetworkResult.Error -> {
                    _uiState.update { RecipeList.UiState(error = UiText.RemoteString(result.message ?: "Unknown Error")) }
                }
                is NetworkResult.Loading -> {
                    _uiState.update { RecipeList.UiState(isLoading = true) }
                }
            }
        }.launchIn(viewModelScope)

}

object RecipeList {
    data class UiState(
        val isLoading: Boolean = false,
        val data: List<Recipe>? = null,
        val error: UiText = UiText.Idle
    )

    sealed interface Navigation{
        data class GoToRecipeDetails(val id: String): Navigation
        data object GoToFavorite : Navigation
    }

    sealed interface Event {
        data class SearchRecipe(val query: String) : Event

        data class GoToRecipeDetail(val id: String) : Event
        data object GoToFavorite : Event
    }
}
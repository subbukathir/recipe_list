package com.example.feature.search.ui.screens.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.common.utils.NetworkResult
import com.example.common.utils.UiText
import com.example.search.domain.model.Recipe
import com.example.search.domain.model.RecipeDetails
import com.example.search.domain.use_cases.DeleteRecipeUseCase
import com.example.search.domain.use_cases.GetRecipeDetailsUseCase
import com.example.search.domain.use_cases.InsertRecipeUseCase
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
class RecipeDetailViewModel @Inject constructor(
    private val getRecipeDetailsUseCase: GetRecipeDetailsUseCase,
    private val insertRecipeUseCase: InsertRecipeUseCase,
    private val deleteRecipeUseCase: DeleteRecipeUseCase,
): ViewModel() {

    private val _uiState = MutableStateFlow(RecipeDetail.UiState())
    val uiState: StateFlow<RecipeDetail.UiState> = _uiState.asStateFlow()

    private val _navigation = Channel<RecipeDetail.Navigation>()
    val navigation:Flow<RecipeDetail.Navigation> = _navigation.receiveAsFlow()

    fun onEvent(event: RecipeDetail.Event){
        when(event){
            is RecipeDetail.Event.FetchRecipeDetails -> recipeDetails(event.id)
            is RecipeDetail.Event.GoToRecipeListScreen -> {
                viewModelScope.launch {
                    _navigation.send(RecipeDetail.Navigation.GoToRecipeListScreen)
                }
            }
            is RecipeDetail.Event.DeleteRecipe -> deleteRecipeUseCase.invoke(event.recipeDetails.toRecipe()).launchIn(viewModelScope)
            is RecipeDetail.Event.InsertRecipe -> insertRecipeUseCase.invoke(event.recipeDetails.toRecipe()).launchIn(viewModelScope)
            is RecipeDetail.Event.GoToMediaPlayerScreen -> {
                viewModelScope.launch {
                    _navigation.send(RecipeDetail.Navigation.GoToMediaPlayerScreen(event.videoId))
                }
            }
        }
    }

    private fun recipeDetails(id: String) = getRecipeDetailsUseCase.invoke(id)
        .onEach { result ->
            when(result){
                is NetworkResult.Success -> {
                    _uiState.update { RecipeDetail.UiState(data = result.data) }
                }
                is NetworkResult.Error -> _uiState.update { RecipeDetail.UiState(error = UiText.RemoteString(result.message ?: "Unknown Error")) }
                is NetworkResult.Loading -> _uiState.update { RecipeDetail.UiState(isLoading = true) }
            }
        }.launchIn(viewModelScope)
}

private fun RecipeDetails.toRecipe() = Recipe(
    idMeal = idMeal,
    strArea = strArea,
    strMeal = strMeal,
    strMealThumb = strMealThumb,
    strCategory = strCategory,
    strTags = strTags,
    strYoutube = strYoutube,
    strInstructions = strInstructions
)

object RecipeDetail{
    data class UiState(
        val isLoading: Boolean = false,
        val data: RecipeDetails? = null,
        val error: UiText = UiText.Idle
    )

    sealed interface Navigation{
        data object GoToRecipeListScreen : Navigation
        data class GoToMediaPlayerScreen(val videoId: String) : Navigation
    }

    sealed interface Event{
        data class FetchRecipeDetails(val id: String): Event

        data class InsertRecipe(val recipeDetails: RecipeDetails) : Event
        data class DeleteRecipe(val recipeDetails: RecipeDetails) : Event

        data object GoToRecipeListScreen : Event
        data class GoToMediaPlayerScreen(val videoId: String) : Event
    }
}
package com.example.search.data.repository

import com.example.search.data.local.RecipeDAO
import com.example.search.data.mappers.toDomain
import com.example.search.data.remote.SearchApiService
import com.example.search.domain.model.Recipe
import com.example.search.domain.model.RecipeDetails
import com.example.search.domain.repository.SearchRepository
import kotlinx.coroutines.flow.Flow

class SearchRepoImpl(
    private val searchApiService: SearchApiService,
    private val recipeDAO: RecipeDAO
) : SearchRepository {
    override suspend fun getRecipes(query: String): Result<List<Recipe>> {
        return try {
            val response = searchApiService.getRecipes(query)
            if (response.isSuccessful) {
                response.body()?.meals?.let {
                    Result.success(it.toDomain())
                } ?: run { Result.failure(Exception("Error occured")) }
            } else Result.failure(Exception("Error occured"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getRecipeDetails(query: String): Result<RecipeDetails> {
        return try {
            val response = searchApiService.getRecipeDetails(query)
            if (response.isSuccessful) {
                response.body()?.meals?.firstOrNull()?.let {
                    Result.success(it.toDomain())
                } ?: run { Result.failure(Exception("Error occured")) }
            } else Result.failure(Exception("Error occured"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun insertRecipe(recipe: Recipe) {
        recipeDAO.insertRecipe(recipe)
    }

    override suspend fun deleteRecipe(recipe: Recipe) {
        recipeDAO.deleteRecipe(recipe)
    }

    override fun getAllRecipes(): Flow<List<Recipe>> {
        return recipeDAO.getAllRecipes()
    }

    override suspend fun updateRecipe(recipe: Recipe) {
        recipeDAO.updateRecipe(recipe)
    }
}
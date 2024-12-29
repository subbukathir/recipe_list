package com.example.search.domain.use_cases

import com.example.common.utils.NetworkResult
import com.example.search.domain.model.Recipe
import com.example.search.domain.repository.SearchRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class GetAllRecipeUseCase @Inject constructor(private val searchRepository: SearchRepository) {
    operator fun invoke(query: String) = flow<NetworkResult<List<Recipe>>> {
        emit(NetworkResult.Loading())
        val result = searchRepository.getRecipes(query)
        if(result.isSuccess){
            emit(NetworkResult.Success(data = result.getOrThrow()))
        }else{
            emit(NetworkResult.Error(result.exceptionOrNull()?.message))
        }

        }.catch {
            emit(NetworkResult.Error(it.message))
    }.flowOn(Dispatchers.IO)
}
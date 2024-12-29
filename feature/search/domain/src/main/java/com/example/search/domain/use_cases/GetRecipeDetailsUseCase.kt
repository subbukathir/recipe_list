package com.example.search.domain.use_cases

import com.example.common.utils.NetworkResult
import com.example.search.domain.model.RecipeDetails
import com.example.search.domain.repository.SearchRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class GetRecipeDetailsUseCase @Inject constructor(private val searchRepository: SearchRepository)  {

    operator fun invoke(id: String) = flow<NetworkResult<RecipeDetails>> {
        emit(NetworkResult.Loading())
        val result = searchRepository.getRecipeDetails(id)

        if(result.isSuccess){
            emit(NetworkResult.Success(result.getOrThrow()))
        }else{
            emit(NetworkResult.Error(message = result.exceptionOrNull()?.localizedMessage))
        }
    }.catch {
        emit(NetworkResult.Error(message = it.localizedMessage))
    }.flowOn(Dispatchers.IO)
}
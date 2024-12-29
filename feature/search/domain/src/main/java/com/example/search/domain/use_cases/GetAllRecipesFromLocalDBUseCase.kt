package com.example.search.domain.use_cases

import com.example.search.domain.repository.SearchRepository
import javax.inject.Inject

class GetAllRecipesFromLocalDBUseCase @Inject constructor(private val searchRepository: SearchRepository) {

    operator fun invoke() = searchRepository.getAllRecipes()
}
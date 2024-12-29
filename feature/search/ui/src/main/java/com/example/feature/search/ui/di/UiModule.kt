package com.example.feature.search.ui.di

import com.example.feature.search.ui.navigation.SearchFeatureApi
import com.example.feature.search.ui.navigation.SearchFeatureApiImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
object UiModule {

    @Provides
    fun provideSearchFeatureApi(): SearchFeatureApi = SearchFeatureApiImpl()
}
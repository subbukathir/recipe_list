package com.example.searchrecipe.di

import android.content.Context
import com.example.feature.search.ui.navigation.SearchFeatureApi
import com.example.mediaplayer.navigation.MediaPlayerApi
import com.example.searchrecipe.local.AppDatabase
import com.example.searchrecipe.navigation.NavigationSubGraphs
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object AppModule {

    @Provides
    fun provideNavigationSubGraphs(
        searchFeatureApi: SearchFeatureApi,
        mediaPlayerApi: MediaPlayerApi
    ): NavigationSubGraphs {
        return NavigationSubGraphs(
            searchFeatureApi = searchFeatureApi,
            mediaPlayerApi = mediaPlayerApi
        )
    }

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context) = AppDatabase.getInstance(context)

    @Provides
    fun provideRecipeDao(appDatabase: AppDatabase) = appDatabase.getRecipeDao()
}
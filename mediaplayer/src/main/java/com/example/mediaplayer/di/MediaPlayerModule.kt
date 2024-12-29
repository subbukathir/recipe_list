package com.example.mediaplayer.di

import com.example.mediaplayer.navigation.MediaPlayerApi
import com.example.mediaplayer.navigation.MediaPlayerApiImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
object MediaPlayerModule {

    @Provides
    fun providesMediaPlayerFeatureApi(): MediaPlayerApi = MediaPlayerApiImpl()
}
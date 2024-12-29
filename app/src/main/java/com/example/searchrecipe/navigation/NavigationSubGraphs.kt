package com.example.searchrecipe.navigation

import com.example.feature.search.ui.navigation.SearchFeatureApi
import com.example.mediaplayer.navigation.MediaPlayerApi

data class NavigationSubGraphs(
    val searchFeatureApi: SearchFeatureApi,
    val mediaPlayerApi: MediaPlayerApi
)

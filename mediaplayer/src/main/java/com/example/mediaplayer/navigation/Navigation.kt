package com.example.mediaplayer.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.example.common.navigation.FeatureApi
import com.example.common.navigation.NavigationRoute
import com.example.common.navigation.NavigationSubGraphRoute
import com.example.mediaplayer.screens.MediaPlayerScreen

interface MediaPlayerApi : FeatureApi

class MediaPlayerApiImpl : MediaPlayerApi {
    override fun registerGraph(
        navGraphBuilder: NavGraphBuilder,
        navHostController: NavHostController
    ) {
        navGraphBuilder.navigation(
            startDestination = NavigationRoute.MediaPlayer.route,
            route = NavigationSubGraphRoute.MediaPlayer.route
        ){
            composable(route = NavigationRoute.MediaPlayer.route){
                val videoId = it.arguments?.getString("video_id") ?: ""
                MediaPlayerScreen(videoId = videoId, navHostController = navHostController)
            }
        }
    }

}
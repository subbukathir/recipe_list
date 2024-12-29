package com.example.searchrecipe

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Surface
import com.example.searchrecipe.navigation.NavigationSubGraphs
import com.example.searchrecipe.navigation.RecipeNavigation
import com.example.searchrecipe.ui.theme.SearchrecipeTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var navigationSubGraphs: NavigationSubGraphs


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SearchrecipeTheme {
                Surface {
                    RecipeNavigation(
                        navigationSubGraphs = navigationSubGraphs
                    )
                }
            }
        }
    }
}
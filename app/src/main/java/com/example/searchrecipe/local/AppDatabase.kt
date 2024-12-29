package com.example.searchrecipe.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.search.data.local.RecipeDAO
import com.example.search.domain.model.Recipe

@Database(entities = [Recipe::class], version = 1, exportSchema = false)
abstract class AppDatabase: RoomDatabase() {

    companion object {
        fun getInstance(context: Context) = Room.databaseBuilder(
            context = context,
            klass = AppDatabase::class.java,
            name = "recipe_app_db"
        ).fallbackToDestructiveMigration()
            .build()
    }

    abstract fun getRecipeDao() : RecipeDAO
}
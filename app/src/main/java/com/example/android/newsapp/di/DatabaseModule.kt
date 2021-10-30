package com.example.android.newsapp.di

import android.app.Application
import androidx.room.Room
import com.example.android.newsapp.database.ArticleDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {
    companion object {
        @Provides
        @Singleton

        fun getdatabase(app: Application) =
            Room.databaseBuilder(app, ArticleDatabase::class.java, "articleDataBase").build()

        @Provides
        @Singleton
        fun getDao(database: ArticleDatabase) = database.getDao()
    }
}
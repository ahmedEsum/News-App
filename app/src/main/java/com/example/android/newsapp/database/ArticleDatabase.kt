package com.example.android.newsapp.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.android.newsapp.pojo.Article

@Database(entities = [Article::class],version = 5,exportSchema = false)
@TypeConverters(Converters::class)
abstract class ArticleDatabase : RoomDatabase() {
    abstract fun getDao(): ArticlesDao
}
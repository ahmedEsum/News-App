package com.example.android.newsapp.di

import com.example.android.newsapp.network.NewsAPi
import com.example.android.newsapp.utils.Constants.Companion.API_BASE
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {

    companion object {
        @Provides
        @Singleton

        fun getNewsApi(): NewsAPi =
            Retrofit.Builder().baseUrl(API_BASE).addConverterFactory(GsonConverterFactory.create())
                .build().create(NewsAPi::class.java)

    }
}
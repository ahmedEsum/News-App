package com.example.android.newsapp.network

import com.example.android.newsapp.pojo.NewsResponse
import com.example.android.newsapp.utils.Constants.Companion.API_KEY
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsAPi {
    @GET("v2/top-headlines")
    suspend fun getBreakingNews(
        @Query("country")
        countryCode: String = "eg", @Query("page")
        page: Int = 1, @Query("apikey")
        apiKey: String = API_KEY
    ): Response<NewsResponse>


    @GET("v2/everything")
    suspend fun getSearchQuery(
        @Query("q")
        qSearchWord: String, @Query("page")
        page: Int = 1, @Query("apikey")
        apiKey: String = API_KEY
    ): Response<NewsResponse>
}
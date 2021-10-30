package com.example.android.newsapp.viewmodels

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.ConnectivityManager.*
import android.net.NetworkCapabilities.*
import android.os.Build
import androidx.lifecycle.*
import com.example.android.newsapp.MainApplication
import com.example.android.newsapp.pojo.Article
import com.example.android.newsapp.pojo.NewsResponse
import com.example.android.newsapp.repository.Repository
import com.example.android.newsapp.utils.Resources
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class ArticleViewModel @Inject constructor(app: Application, private val repository: Repository) :
    AndroidViewModel(app) {
    val breakingNewsMTD: MutableLiveData<Resources<NewsResponse>> = MutableLiveData()
    var breakingNewsPage = 1
    private var breakingNewsList: NewsResponse? = null

    val searchNewsMTD: MutableLiveData<Resources<NewsResponse>> = MutableLiveData()
    var searchNewsPage = 1
    var searchNewsList: NewsResponse? = null

    val internetConnectivityMTD = MutableLiveData<Boolean>().apply {
        postValue(checkConnectivity())
    }


   init {
        getBreakingNews("eg")
    }

    fun saveArticle(article: Article) = viewModelScope.launch {
        repository.upsertArticle(article)
    }

    fun deleteArticle(article: Article) = viewModelScope.launch {
        repository.deleteArticle(article)
    }

    fun getSavedArticles() = repository.getSavedArticles()

    fun getBreakingNews(countryCode: String="eg") = viewModelScope.launch {
        safeBreakingNewsCall(countryCode)
    }

    fun getSearchNews(q: String) = viewModelScope.launch {
        safeSearchNewsCall(q)
    }


    private fun handlingApiResponse(response: Response<NewsResponse>): Resources<NewsResponse> {
        if (response.isSuccessful) {
            response.body()?.let {
                breakingNewsPage++
                if (breakingNewsList == null) {
                    breakingNewsList = it
                } else {
                    val oldArticles = breakingNewsList?.articles
                    val newArticle = it.articles
                    oldArticles?.addAll(newArticle)
                }
                return Resources.Success(breakingNewsList ?: it)
            }
        }
        return Resources.Failure(response.message())
    }


    private fun handlingSearchNews(response: Response<NewsResponse>): Resources<NewsResponse> {
        if (response.isSuccessful) {
            response.body()?.let {
                searchNewsPage++
                if (searchNewsList == null) {
                    searchNewsList = it
                } else {
                    val oldArticles = searchNewsList?.articles
                    val newArticle = it.articles
                    oldArticles?.addAll(newArticle)
                }
                return Resources.Success(searchNewsList ?: it)
            }
        }
        return Resources.Failure(response.message())
    }




    private suspend fun safeBreakingNewsCall(countryCode: String){
        breakingNewsMTD.postValue(Resources.Loading())
        try {
            if (checkConnectivity()){
                val response = repository.getBreakingNews(countryCode, breakingNewsPage)
                breakingNewsMTD.postValue(handlingApiResponse(response))
            }else {
                breakingNewsMTD.postValue(Resources.Failure("no internet connection"))
            }
        }catch (t:Throwable){
            when (t){
                is IOException -> breakingNewsMTD.postValue(Resources.Failure("network failure"))
                else -> breakingNewsMTD.postValue(Resources.Failure("Conversion error "))
            }
        }
    }

    private suspend fun safeSearchNewsCall(q: String){
        searchNewsMTD.postValue(Resources.Loading())
        try {
            if (checkConnectivity()){
                val response = repository.getSearchNews(q, searchNewsPage)
                searchNewsMTD.postValue(handlingSearchNews(response))
            }else {
                searchNewsMTD.postValue(Resources.Failure("no internet connection"))
            }
        }catch (t:Throwable){
            when (t){
                is IOException -> searchNewsMTD.postValue(Resources.Failure("network failure"))
                else -> searchNewsMTD.postValue(Resources.Failure("Conversion error "))
            }
        }
    }



    private fun checkConnectivity(): Boolean {
        val connectivityManager =
            getApplication<MainApplication>().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val activeNetwork = connectivityManager.activeNetwork ?: return false
            val capabilities =
                connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
            return when {
                capabilities.hasTransport(TRANSPORT_WIFI)-> true
                capabilities.hasTransport(TRANSPORT_ETHERNET)-> true
                capabilities.hasTransport(TRANSPORT_CELLULAR)-> true
                else -> false
            }
        }else {
            connectivityManager.activeNetworkInfo?.run {
                return  when(type){
                    TYPE_WIFI->true
                    TYPE_MOBILE->true
                    TYPE_ETHERNET->true
                    else->false
                }
            }
        }
        return false
    }

}
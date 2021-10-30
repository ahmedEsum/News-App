package com.example.android.newsapp.repository

import com.example.android.newsapp.database.ArticleDatabase
import com.example.android.newsapp.network.NewsAPi
import com.example.android.newsapp.pojo.Article
import javax.inject.Inject

class Repository @Inject constructor (private val articleDatabase: ArticleDatabase , private val newsAPi: NewsAPi) {

    suspend fun getBreakingNews(countryCode : String,pageNo:Int  )=newsAPi.getBreakingNews(countryCode,pageNo)
    suspend fun getSearchNews(searchQuery : String,pageNo:Int  )=newsAPi.getSearchQuery(searchQuery,pageNo)

    suspend fun upsertArticle (article : Article)=articleDatabase.getDao().insertNews(article)
    suspend fun deleteArticle (article: Article)=articleDatabase.getDao().deleteArticle(article)
    fun getSavedArticles ()=articleDatabase.getDao().getSavedArticles()
}
package com.example.android.newsapp.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.android.newsapp.R
import com.example.android.newsapp.adapters.ArticlesAdapter
import com.example.android.newsapp.databinding.SearchNewFragmentBinding
import com.example.android.newsapp.utils.Constants
import com.example.android.newsapp.utils.Constants.Companion.QUERY_RESULT_PER_PAGE
import com.example.android.newsapp.utils.Constants.Companion.TEXT_TYPING_TIME
import com.example.android.newsapp.utils.Resources
import com.example.android.newsapp.viewmodels.ArticleViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SearchNewsFragment : Fragment() {

    private lateinit var articlesAdapter: ArticlesAdapter
    val viewModel: ArticleViewModel by viewModels()
    private lateinit var binding: SearchNewFragmentBinding


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.search_new_fragment, container, false)
        setupViews()

        articlesAdapter.setOnItemListner {
            val bundle = Bundle().apply {
                putSerializable(Constants.ARTICLE_KEY, it)
            }
            findNavController().navigate(R.id.action_searchNewsFragment2_to_articleFragment, bundle)
        }
        var job: Job? = null

        binding.etSearch.addTextChangedListener { editable ->
            job?.cancel()
            job = MainScope().launch {
                delay(TEXT_TYPING_TIME)
                editable?.let {
                    if (editable.toString().isNotEmpty()) {
                        viewModel.searchNewsPage = 1
                        viewModel.searchNewsList = null
                        viewModel.getSearchNews(editable.toString())

                    }
                }
            }
        }

        viewModel.searchNewsMTD.observe(viewLifecycleOwner, {
            when (it) {
                is Resources.Success -> {
                    hideProgressBar()
                    it.data?.let { newsResponse ->
                        articlesAdapter.differ.submitList(newsResponse.articles)
                        val totalPages = newsResponse.totalResults / QUERY_RESULT_PER_PAGE + 2
                        isLastPage = totalPages == viewModel.searchNewsPage
                    }
                }

                is Resources.Failure -> {
                    hideProgressBar()
                    it.message?.let { message ->
                        Toast.makeText(binding.root.context, message, Toast.LENGTH_SHORT).show()
                    }
                }

                is Resources.Loading -> {
                    showProgressBar()
                }
            }
        })
        return binding.root
    }


    private fun hideProgressBar() {
        binding.paginationProgressBar.visibility = View.INVISIBLE
        isLoading = false
    }

    private fun showProgressBar() {
        binding.paginationProgressBar.visibility = View.VISIBLE
        isLoading = true
    }

    private fun setupViews() {
        articlesAdapter = ArticlesAdapter()
        binding.rvSearchNews.apply {
            adapter = articlesAdapter
            layoutManager = LinearLayoutManager(activity)
            addOnScrollListener(this@SearchNewsFragment.srcrollingListener)

        }
    }

    private var isLoading = false
    private var isScrolling = false
    private var isLastPage = false


    private val srcrollingListener = object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                isScrolling = true
            }
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            val layoutManager = recyclerView.layoutManager as LinearLayoutManager

            val firstItem = layoutManager.findFirstVisibleItemPosition()
            val currentVisibleItems = layoutManager.childCount
            val totalItems = layoutManager.itemCount

            val isNotLoadingAndIsNotLastPage = !isLoading && !isLastPage
            val isNotInBegining = firstItem >= 0
            val isLastItem = firstItem + currentVisibleItems >= totalItems
            val isMoreThanTheTotalList = totalItems >= QUERY_RESULT_PER_PAGE

            val shouldPaginate =
                isNotInBegining && isNotLoadingAndIsNotLastPage && isLastItem && isMoreThanTheTotalList

            if (shouldPaginate) {
                viewModel.getSearchNews(binding.etSearch.text.toString())
                isScrolling = false
            }
        }
    }
}
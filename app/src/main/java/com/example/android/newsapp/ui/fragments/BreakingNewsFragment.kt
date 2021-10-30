package com.example.android.newsapp.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.android.newsapp.R
import com.example.android.newsapp.adapters.ArticlesAdapter
import com.example.android.newsapp.databinding.BreakingNewFragmentBinding
import com.example.android.newsapp.utils.Constants.Companion.ARTICLE_KEY
import com.example.android.newsapp.utils.Constants.Companion.QUERY_RESULT_PER_PAGE
import com.example.android.newsapp.utils.Resources
import com.example.android.newsapp.viewmodels.ArticleViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BreakingNewsFragment : Fragment() {

    private lateinit var articlesAdapter: ArticlesAdapter
    val viewModel: ArticleViewModel by viewModels()
    private lateinit var binding: BreakingNewFragmentBinding



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.breaking_new_fragment, container, false)

        setupViews()
        articlesAdapter.setOnItemListner {
            val bundle = Bundle().apply {
                putSerializable(ARTICLE_KEY, it)
            }
            findNavController().navigate(
                R.id.action_breakingNewsFragment2_to_articleFragment,
                bundle
            )
        }

        viewModel.breakingNewsMTD.observe(requireActivity(), {
            when (it) {
                is Resources.Success -> {
                    hideProgressBar()
                    it.data?.let { newsResponse ->
                        articlesAdapter.differ.submitList(newsResponse.articles.toList())
                        val totalResponsePages =
                            newsResponse.totalResults / QUERY_RESULT_PER_PAGE + 2
                        isLastPage = totalResponsePages == viewModel.breakingNewsPage
                        if (isLastPage) {
                            binding.rvBreakingNews.setPadding(0, 0, 0, 0)
                        }
                    }
                }

                is Resources.Failure -> {
                    hideProgressBar()
                    it.message?.let { message ->
                        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
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
        binding.rvBreakingNews.apply {
            adapter = articlesAdapter
            layoutManager = LinearLayoutManager(activity)
            addOnScrollListener(this@BreakingNewsFragment.scrollingListener)

        }
    }


    var isLoading = false
    var isLastPage = false
    var isScrolling = false


    private val scrollingListener = object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                isScrolling = true
            }
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            val layout = recyclerView.layoutManager as LinearLayoutManager
            val firstVisibleItemPosition = layout.findFirstVisibleItemPosition()
            val currentVisibleItems = layout.childCount
            val totalItemsList = layout.itemCount

            val isNotLoadingIsNotLastPage = !isLoading && !isLastPage
            val isLastItem = firstVisibleItemPosition + currentVisibleItems >= totalItemsList
            val isNotInBeggining = firstVisibleItemPosition >= 0
            val isTotalMoreThanVisible = totalItemsList >= QUERY_RESULT_PER_PAGE

            val shouldPaginate =
                isLastItem && isNotInBeggining && isNotLoadingIsNotLastPage && isTotalMoreThanVisible

            if (shouldPaginate) {
                viewModel.getBreakingNews("eg")
                isScrolling = false
            }
        }
    }


}
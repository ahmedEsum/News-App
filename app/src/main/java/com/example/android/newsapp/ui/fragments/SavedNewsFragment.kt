package com.example.android.newsapp.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.android.newsapp.R
import com.example.android.newsapp.adapters.ArticlesAdapter
import com.example.android.newsapp.databinding.SavedNewFragmentBinding
import com.example.android.newsapp.utils.Constants
import com.example.android.newsapp.viewmodels.ArticleViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SavedNewsFragment : Fragment() {

    private lateinit var articlesAdapter: ArticlesAdapter
    private lateinit var binding: SavedNewFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = DataBindingUtil.inflate(inflater, R.layout.saved_new_fragment, container, false)
        var checkInternet = false
        val viewModel: ArticleViewModel by viewModels()
        setupViews()

        viewModel.getSavedArticles().observe(viewLifecycleOwner, {
            articlesAdapter.differ.submitList(it)
        })

        viewModel.internetConnectivityMTD.observe(viewLifecycleOwner, {
            checkInternet = it
        })


        articlesAdapter.setOnItemListner {
            if (checkInternet) {
                val bundle = Bundle().apply {
                    putSerializable(Constants.ARTICLE_KEY, it)
                }
                findNavController().navigate(
                    R.id.action_savedNewsFragment2_to_articleFragment,
                    bundle
                )
            }
        }


        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val articlePosition = viewHolder.adapterPosition
                val article = articlesAdapter.differ.currentList[articlePosition]
                viewModel.deleteArticle(article)
                Snackbar.make(requireView(), "Article deleted successfully", Snackbar.LENGTH_LONG)
                    .apply {
                        setAction("undo") {
                            viewModel.saveArticle(article)
                        }
                        show()
                    }
            }
        }


        ItemTouchHelper(itemTouchHelperCallback).apply {
            attachToRecyclerView(binding.rvSavedNews)
        }


        return binding.root
    }


    private fun setupViews() {
        articlesAdapter = ArticlesAdapter()
        binding.rvSavedNews.apply {
            adapter = articlesAdapter
            layoutManager = LinearLayoutManager(activity)

        }
    }


}
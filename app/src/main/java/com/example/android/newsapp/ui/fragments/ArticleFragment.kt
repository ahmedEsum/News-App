package com.example.android.newsapp.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.example.android.newsapp.R
import com.example.android.newsapp.databinding.ArticleFragmentBinding
import com.example.android.newsapp.pojo.Article
import com.example.android.newsapp.viewmodels.ArticleViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ArticleFragment : Fragment(){
private lateinit var binding :ArticleFragmentBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding=DataBindingUtil.inflate(inflater,R.layout.article_fragment,container,false)
        val args :ArticleFragmentArgs by navArgs()
        val viewModel : ArticleViewModel by viewModels()
        val article:Article = args.article
        binding.article=article
        binding.executePendingBindings()
        binding.fab.setOnClickListener{
            viewModel.saveArticle(article)
            Toast.makeText(requireContext(),"Article saved Successfully",Toast.LENGTH_SHORT).show()
        }
        return binding.root
    }

}
package com.example.android.newsapp.utils

import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.android.newsapp.R


@BindingAdapter("setImage")
fun setImage(imageView: ImageView, url: String?) {
    url?.let {
        Glide.with(imageView.context).load(url)
            .apply(RequestOptions().placeholder(R.drawable.ic_baseline_pending_24))
            .error(R.drawable.ic_baseline_error_outline_24).into(imageView)
    }
}

@BindingAdapter("loadUrl")
fun loadUrl ( webView: WebView, url: String?){
        webView.apply {
            webViewClient= WebViewClient()
            url?.let {
                loadUrl(it)
            }
        }
}


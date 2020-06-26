package com.example.test.librarysearch.common

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.test.librarysearch.R

class BindingAdapters {
    companion object {
        @JvmStatic
        @BindingAdapter("setImageLoader")
        fun setImageLoader(imageView: ImageView, url: String?) {
            url?.let {
                Glide.with(imageView.context).load(url).placeholder(R.drawable.placeholder_default).into(imageView)
            }
        }

        @BindingAdapter("setAdapter")
        fun setAdapter(view: RecyclerView, adapter: RecyclerView.Adapter<RecyclerView.ViewHolder>?) {
            adapter?.let {
                view.adapter = adapter
            }
        }
    }
}
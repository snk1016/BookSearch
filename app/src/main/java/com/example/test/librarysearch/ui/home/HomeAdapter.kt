package com.example.test.librarysearch.ui.home

import android.graphics.Paint
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.test.librarysearch.R
import com.example.test.librarysearch.databinding.AdapterHomeItemBinding
import com.example.test.librarysearch.model.response.Documents

class HomeAdapter: PagedListAdapter<Documents, HomeAdapter.HomeViewHolder>(diffCallback) {
    lateinit var onItemClickListener: OnItemClickListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        return HomeViewHolder(AdapterHomeItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
        getItem(position)?.let {
            if(it.price > it.salePrice) {
                holder.binding.txtBookItemPrice.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                holder.binding.txtBookItemSalePrice.visibility = View.VISIBLE

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                    holder.binding.txtBookItemPrice.setTextAppearance(R.style.txtPriceDisabled)
                else
                    holder.binding.txtBookItemPrice.setTextAppearance(holder.binding.txtBookItemPrice.context, R.style.txtPriceDisabled)
            }

            if(it.isLike)
                holder.binding.imgLike.setImageResource(R.drawable.icon_heart_on)
            else
                holder.binding.imgLike.setImageResource(R.drawable.icon_heart_off)

            holder.binding.item = it
            holder.binding.position = position
            holder.binding.listener = onItemClickListener
        }
    }

    interface OnItemClickListener {
        fun setOnItemClickListener(view: View, item: Documents, position: Int)
    }

    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<Documents>() {
            override fun areItemsTheSame(oldItem: Documents, newItem: Documents): Boolean =
                oldItem == newItem

            override fun areContentsTheSame(oldItem: Documents, newItem: Documents): Boolean =
                oldItem.isLike == newItem.isLike && oldItem == newItem
        }
    }

    class HomeViewHolder: RecyclerView.ViewHolder {
        var binding: AdapterHomeItemBinding

        constructor(binding: AdapterHomeItemBinding): super(binding.root) {
            this.binding = binding
        }
    }
}
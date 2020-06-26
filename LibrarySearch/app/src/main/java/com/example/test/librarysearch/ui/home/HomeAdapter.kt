package com.example.test.librarysearch.ui.home

import android.graphics.Paint
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.test.librarysearch.R
import com.example.test.librarysearch.databinding.AdapterHomeItemBinding
import com.example.test.librarysearch.model.response.Documents

class HomeAdapter(documents: MutableList<Documents>): RecyclerView.Adapter<HomeAdapter.HomeViewHolder>() {
    private var items: MutableList<Documents> = documents
    lateinit var onItemClickListener: OnItemClickListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        return HomeViewHolder(
            AdapterHomeItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
        if(items[position].price > items[position].salePrice ) {
            holder.binding.txtBookItemPrice.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
            holder.binding.txtBookItemSalePrice.visibility = View.VISIBLE

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                holder.binding.txtBookItemPrice.setTextAppearance(R.style.txtPriceDisabled)
            else
                holder.binding.txtBookItemPrice.setTextAppearance(holder.binding.txtBookItemPrice.context, R.style.txtPriceDisabled)
        }

        holder.binding.item = items[position]
        holder.binding.position = position
        holder.binding.listener = onItemClickListener
    }

    fun setOnItemClickListener(view: View, item: Documents, position: Int) {
        onItemClickListener.setOnItemClickListener(view, item, position)
    }

    fun addItem(item: MutableList<Documents>, position: Int) {
        items.addAll(item)
        notifyItemInserted(position)
    }

    interface OnItemClickListener {
        fun setOnItemClickListener(view: View, item: Documents, position: Int)
    }

    class HomeViewHolder: RecyclerView.ViewHolder {
        var binding: AdapterHomeItemBinding

        constructor(binding: AdapterHomeItemBinding): super(binding.root) {
            this.binding = binding
        }
    }
}
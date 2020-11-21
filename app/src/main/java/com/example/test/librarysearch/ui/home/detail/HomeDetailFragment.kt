package com.example.test.librarysearch.ui.home.detail

import android.content.Intent
import android.graphics.Paint
import android.graphics.drawable.StateListDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.*
import androidx.activity.OnBackPressedCallback
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.test.librarysearch.MainActivity
import com.example.test.librarysearch.R
import com.example.test.librarysearch.databinding.FragmentHomeDetailBinding
import com.example.test.librarysearch.model.response.Documents
import com.google.gson.Gson

class HomeDetailFragment : Fragment {

    private lateinit var binding: FragmentHomeDetailBinding

    private var item: String

    private lateinit var bookUrl: String

    private lateinit var documents: Documents

    private var isLike = false

    constructor(item: String) {
        this.item = item
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home_detail, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setHasOptionsMenu(true)

        documents = Gson().fromJson(item, Documents::class.java)

        if(documents.translators.size > 0)
            binding.layoutTranslator.visibility = View.VISIBLE

        if(documents.price > documents.salePrice ) {
            binding.txtBookItemPrice.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
            binding.txtBookItemSalePrice.visibility = View.VISIBLE

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                binding.txtBookItemPrice.setTextAppearance(R.style.txtPriceDisabled)
            else
                binding.txtBookItemPrice.setTextAppearance(binding.txtBookItemPrice.context, R.style.txtPriceDisabled)
        }

        bookUrl = documents.url
        isLike = documents.isLike

        binding.item = documents
        binding.lifecycleOwner = this

        (activity as MainActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)

        requireActivity().onBackPressedDispatcher.addCallback(this, onBackPressedCallback)

        binding.btnBookPurchase.setOnClickListener(mClickListener)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            android.R.id.home -> {
                (activity as MainActivity).setLike(isLike)
                activity?.onBackPressed()
            }

            R.id.action_like -> {
                isLike = !item.isChecked
                item.isChecked = isLike
                setMenuLikeSelector(item)
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.like, menu)

        var item = menu.findItem(R.id.action_like)
        item.isChecked = isLike
        setMenuLikeSelector(item)
    }

    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            (activity as MainActivity).setLike(isLike)
            this.remove()
            activity?.onBackPressed()
        }
    }

    private val mClickListener = View.OnClickListener { v ->
        when(v.id) {
            R.id.btnBookPurchase -> {
                val it = Intent(Intent.ACTION_VIEW, Uri.parse(bookUrl))
                startActivity(it)
            }
        }
    }

    private fun setMenuLikeSelector(item: MenuItem) {
        val stateListDrawable = resources.getDrawable(R.drawable.selector_like, null) as StateListDrawable
        val state = intArrayOf(if (item.isChecked) android.R.attr.state_checked else android.R.attr.state_empty)
        stateListDrawable.state = state
        item.icon = stateListDrawable.current
    }
}
package com.example.test.librarysearch.ui.home.detail

import android.content.Intent
import android.graphics.Paint
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.test.librarysearch.MainActivity
import com.example.test.librarysearch.R
import com.example.test.librarysearch.databinding.FragmentHomeBinding
import com.example.test.librarysearch.databinding.FragmentHomeDetailBinding
import com.example.test.librarysearch.model.response.Documents
import com.example.test.librarysearch.services.NetworkService
import com.example.test.librarysearch.viewModel.home.HomeViewModel
import com.example.test.librarysearch.viewModel.home.HomeViewModelFactory
import com.google.gson.Gson
import org.koin.android.ext.android.inject

class HomeDetailFragment : Fragment {

    private lateinit var homeViewModelFactory: HomeViewModelFactory

    private lateinit var homeViewModel: HomeViewModel

    private val api: NetworkService by inject()

    private lateinit var binding: FragmentHomeDetailBinding

    private var item: String

    private lateinit var bookUrl: String

    constructor(item: String) {
        this.item = item
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home_detail, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setHasOptionsMenu(true)

        val documents = Gson().fromJson(item, Documents::class.java)

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

        binding.item = documents
        binding.lifecycleOwner = this

        (activity as MainActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.btnBookPurchase.setOnClickListener(mClickListener)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            android.R.id.home -> {
                activity?.onBackPressed()
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private val mClickListener = View.OnClickListener { v ->
        when(v.id) {
            R.id.btnBookPurchase -> {
                val it = Intent(Intent.ACTION_VIEW, Uri.parse(bookUrl))
                startActivity(it)
            }
        }
    }
}
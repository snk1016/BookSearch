package com.example.test.librarysearch.ui.home

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
import com.example.test.librarysearch.model.response.Documents
import com.example.test.librarysearch.services.NetworkService
import com.example.test.librarysearch.viewModel.home.HomeViewModel
import com.example.test.librarysearch.viewModel.home.HomeViewModelFactory
import com.google.gson.Gson
import org.koin.android.ext.android.inject

class HomeFragment : Fragment() {

    private lateinit var homeViewModelFactory: HomeViewModelFactory

    private lateinit var homeViewModel: HomeViewModel

    private val api: NetworkService by inject()

    private lateinit var binding: FragmentHomeBinding

    private lateinit var bookName: String

    private var pageNo = 1

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setHasOptionsMenu(true)

        homeViewModelFactory = HomeViewModelFactory(context, api)
        homeViewModel = ViewModelProvider(this, homeViewModelFactory)
                        .get(HomeViewModel::class.java)

        binding.viewModel = homeViewModel
        binding.lifecycleOwner = this
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.listContainer.setHasFixedSize(true)
        binding.listContainer.setItemViewCacheSize(50)
    }

    override fun onResume() {
        super.onResume()
        (activity as MainActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)

        if(homeViewModel.homeAdapter == null || homeViewModel.homeAdapter?.itemCount == 0)
            binding.txtSearchNotFound.visibility = View.VISIBLE
        else
            binding.txtSearchNotFound.visibility = View.GONE

        (activity as MainActivity).getLike()?.let {
            homeViewModel.updateItem(it, (activity as MainActivity).getCurrentPos())
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        inflater.inflate(R.menu.search, menu)

        val actionSearch = menu.findItem(R.id.action_search).actionView as SearchView

        actionSearch.maxWidth = Int.MAX_VALUE
        actionSearch.queryHint = getString(R.string.hint_search)

        actionSearch.setOnQueryTextListener(mQueryTextListener)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.action_search) {
            val actionSearch = item as SearchView

            actionSearch.maxWidth = Int.MAX_VALUE
            actionSearch.queryHint = getString(R.string.hint_search)

            actionSearch.setOnQueryTextListener(mQueryTextListener)
        }

        return super.onOptionsItemSelected(item)
    }

    private val mQueryTextListener = object: SearchView.OnQueryTextListener {
        override fun onQueryTextSubmit(query: String?): Boolean {
            query?.let {
                bookName = it
                homeViewModel.clearAdapter()
                homeViewModel.callApiService(it, binding.txtSearchNotFound, mItemClickListener)
            }

            return false
        }

        override fun onQueryTextChange(newText: String?): Boolean {
            return false
        }
    }

    private var mItemClickListener = object: HomeAdapter.OnItemClickListener {
        override fun setOnItemClickListener(view: View, item: Documents, position: Int) {
            (activity as MainActivity).moveDetailFragment(Gson().toJson(item), position)
        }
    }
}
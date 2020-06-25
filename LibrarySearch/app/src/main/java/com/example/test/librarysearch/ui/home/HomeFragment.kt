package com.example.test.librarysearch.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.test.librarysearch.R
import com.example.test.librarysearch.databinding.FragmentHomeBinding
import com.example.test.librarysearch.services.NetworkService
import com.example.test.librarysearch.viewModel.home.HomeViewModel
import com.example.test.librarysearch.viewModel.home.HomeViewModelFactory
import org.koin.android.ext.android.inject
import javax.inject.Inject

class HomeFragment : Fragment() {

//    private lateinit var viewModelFactory: ViewModelProvider.Factory
//        @Inject set

    private lateinit var homeViewModelFactory: HomeViewModelFactory

    private lateinit var homeViewModel: HomeViewModel

    private val api: NetworkService by inject()

    private lateinit var binding: FragmentHomeBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        homeViewModelFactory = HomeViewModelFactory(api)
        homeViewModel = ViewModelProvider(requireParentFragment().viewModelStore, homeViewModelFactory)
                        .get(HomeViewModel::class.java)

        binding.viewModel = homeViewModel
        binding.lifecycleOwner = this

        Log.d("nh", "test")
    }
}
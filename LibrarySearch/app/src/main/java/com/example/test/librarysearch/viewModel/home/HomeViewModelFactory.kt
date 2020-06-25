package com.example.test.librarysearch.viewModel.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.test.librarysearch.services.NetworkService

class HomeViewModelFactory(private val api: NetworkService): ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return HomeViewModel(api) as T
    }

}
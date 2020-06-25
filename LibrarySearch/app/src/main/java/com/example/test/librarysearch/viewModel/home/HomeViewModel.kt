package com.example.test.librarysearch.viewModel.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.test.librarysearch.services.NetworkService
import com.example.test.librarysearch.viewModel.DisposableViewModel

class HomeViewModel(private val api: NetworkService) : DisposableViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is home Fragment"
    }
    val text: LiveData<String> = _text

//    value = "This is home Fragment"

    fun testMessage() {
//        _text.value = "test"
        _text.postValue("test")
    }
}
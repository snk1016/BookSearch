package com.example.test.librarysearch.viewModel.home

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.test.librarysearch.R
import com.example.test.librarysearch.model.response.Response
import com.example.test.librarysearch.services.NetworkService
import com.example.test.librarysearch.viewModel.DisposableViewModel
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class HomeViewModel(private val context: Context?, private val api: NetworkService) : DisposableViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is home Fragment"
    }
    val text: LiveData<String> = _text

//    value = "This is home Fragment"
    private val pageSize = 50

    fun callApiService(RESTApi: String) {
        var observable: Observable<Response>? = null

        when(RESTApi) {
            context?.getString(R.string.REST_API_FUNCTION_SEARCH_BOOK) -> {
                observable = api.searchBook("title", "java", pageSize, 1)
            }
        }

        observable?.let {
            loadData(observable)
        }
    }

    private fun loadData(observable: Observable<Response>) {
        addDisposable(observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe {
                    showProgress()
                }
                .doOnTerminate {
                    hideProgress()
                }
                .subscribe({ response ->
                    Log.d("nh", "response : $response")
                }, { error ->
                    Log.e("nh", error?.message!!)
                })
        )

    }

    private fun showProgress() {
//        _progressView.value = View.VISIBLE
    }

    private fun hideProgress() {
//        _progressView.value = View.INVISIBLE
    }
}
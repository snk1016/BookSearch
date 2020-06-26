package com.example.test.librarysearch.viewModel.home

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.test.librarysearch.R
import com.example.test.librarysearch.model.response.Documents
import com.example.test.librarysearch.model.response.Response
import com.example.test.librarysearch.services.NetworkService
import com.example.test.librarysearch.ui.home.HomeAdapter
import com.example.test.librarysearch.viewModel.DisposableViewModel
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class HomeViewModel(private val context: Context?, private val api: NetworkService) : DisposableViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is home Fragment"
    }
    val text: LiveData<String> = _text

    private val _adapter  = MutableLiveData<HomeAdapter>()

    val adapter : LiveData<HomeAdapter> get() = _adapter

    var homeAdapter: HomeAdapter? = null

    private val pageSize = 5

    fun callApiService(RESTApi: String, pageNo: Int, listener: HomeAdapter.OnItemClickListener) {
        var observable: Observable<Response>? = null

        when(RESTApi) {
            context?.getString(R.string.REST_API_FUNCTION_SEARCH_BOOK) -> {
                observable = api.searchBook("title", "자바", pageSize, pageNo)
            }
        }

        observable?.let {
            loadData(observable, listener)
        }
    }

    private fun loadData(observable: Observable<Response>, listener: HomeAdapter.OnItemClickListener) {
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
                    val items = response.documents
//                    response
                    items?.let {
                        if(homeAdapter == null || homeAdapter?.itemCount == 0) {
                            setBookAdapter(it, listener)
                        } else {
                            val size = homeAdapter?.itemCount
                            addBookAdapter(it, size!!)
                        }
                    }

                    Log.d("nh", "response : $response")
                }, { error ->
                    Log.e("nh", error?.message!!)
                })
        )
    }

    private fun setBookAdapter(items: MutableList<Documents>, listener: HomeAdapter.OnItemClickListener) {
        homeAdapter = HomeAdapter(items)
        homeAdapter?.onItemClickListener = listener
        homeAdapter?.notifyDataSetChanged()

        _adapter.value = homeAdapter
    }

    private fun addBookAdapter(items: MutableList<Documents>, insertPos: Int) {
        homeAdapter?.addItem(items, insertPos)
    }

    private fun showProgress() {
//        _progressView.value = View.VISIBLE
    }

    private fun hideProgress() {
//        _progressView.value = View.INVISIBLE
    }
}
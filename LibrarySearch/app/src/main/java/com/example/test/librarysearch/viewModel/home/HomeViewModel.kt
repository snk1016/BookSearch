package com.example.test.librarysearch.viewModel.home

import android.content.Context
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.test.librarysearch.R
import com.example.test.librarysearch.common.LoadingProgressBar
import com.example.test.librarysearch.model.response.Documents
import com.example.test.librarysearch.model.response.Response
import com.example.test.librarysearch.services.NetworkService
import com.example.test.librarysearch.ui.home.HomeAdapter
import com.example.test.librarysearch.viewModel.DisposableViewModel
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class HomeViewModel(private val context: Context?, private val api: NetworkService) : DisposableViewModel() {

    private val _adapter  = MutableLiveData<HomeAdapter>()

    val adapter : LiveData<HomeAdapter> get() = _adapter

    var homeAdapter: HomeAdapter? = null

    private val loadingProgressBar = LoadingProgressBar(context!!)

    private val pageSize = 50

    fun callApiService(RESTApi: String, bookName: String, pageNo: Int, txtSearchNotFound: TextView, listener: HomeAdapter.OnItemClickListener) {
        var observable: Observable<Response>? = null

        when(RESTApi) {
            context?.getString(R.string.REST_API_FUNCTION_SEARCH_BOOK) -> {
                observable = api.searchBook("title", bookName, pageNo, pageSize)
            }
        }

        observable?.let {
            loadData(observable, txtSearchNotFound, listener)
        }
    }

    private fun loadData(observable: Observable<Response>, txtSearchNotFound: TextView, listener: HomeAdapter.OnItemClickListener) {
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
                    items?.let {
                        if(homeAdapter == null || homeAdapter?.itemCount == 0) {
                            setBookAdapter(it, listener)
                        } else {
                            val size = homeAdapter?.itemCount
                            addBookAdapter(it, size!!)
                        }
                    }

                    if(homeAdapter == null || homeAdapter?.itemCount == 0)
                        txtSearchNotFound.visibility = View.VISIBLE
                    else
                        txtSearchNotFound.visibility = View.GONE
                }, { error ->
                    Toast.makeText(context, "ERROR => ${error?.message}", Toast.LENGTH_SHORT).show()
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

    fun removeAdapter() {
        homeAdapter?.removeItem()
    }

    private fun showProgress() {
        if(!loadingProgressBar.isShowing)
            loadingProgressBar.show()
    }

    private fun hideProgress() {
        if(loadingProgressBar.isShowing)
            loadingProgressBar.dismiss()
    }
}
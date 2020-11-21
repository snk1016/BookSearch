package com.example.test.librarysearch.viewModel.home

import android.content.Context
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.PagedList
import androidx.paging.RxPagedListBuilder
import com.example.test.librarysearch.model.response.Documents
import com.example.test.librarysearch.paging.BookPositionalDataSourceFactory
import com.example.test.librarysearch.services.NetworkService
import com.example.test.librarysearch.ui.home.HomeAdapter
import com.example.test.librarysearch.viewModel.DisposableViewModel
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class HomeViewModel(private val context: Context?, private val api: NetworkService) : DisposableViewModel() {

    var homeAdapter: HomeAdapter = HomeAdapter()

    private val _adapter = MutableLiveData<HomeAdapter>().apply {
        value = homeAdapter
    }

    val adapter : LiveData<HomeAdapter> get() = _adapter

    private val pageSize = 50
    private val prefetchDistance = 10

    private val pagedListConfig = PagedList.Config.Builder()
        .setPageSize(pageSize)
        .setInitialLoadSizeHint(pageSize)
        .setPrefetchDistance(prefetchDistance)
        .setEnablePlaceholders(false)
        .build()

    fun callApiService(bookName: String, txtSearchNotFound: TextView, listener: HomeAdapter.OnItemClickListener) {
        val dataSourceFactory = BookPositionalDataSourceFactory(api, bookName)

        val observable = RxPagedListBuilder(dataSourceFactory, pagedListConfig)
            .buildObservable()

        loadData(observable, txtSearchNotFound, listener)
    }

    private fun loadData(observable: Observable<PagedList<Documents>>, txtSearchNotFound: TextView, listener: HomeAdapter.OnItemClickListener) {
        addDisposable(observable
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ response ->
                response?.let {
                    if(homeAdapter.itemCount == 0)
                        setBookAdapter(it, listener)
                }

                if(homeAdapter.itemCount == 0)
                    txtSearchNotFound.visibility = View.VISIBLE
                else
                    txtSearchNotFound.visibility = View.GONE
            }, { error ->
                Toast.makeText(context, "ERROR => ${error?.message}", Toast.LENGTH_SHORT).show()
            })
        )
    }

    private fun setBookAdapter(items: PagedList<Documents>, listener: HomeAdapter.OnItemClickListener) {
        homeAdapter.submitList(items)
        homeAdapter.onItemClickListener = listener
        homeAdapter.notifyDataSetChanged()
    }

    fun clearAdapter() {
        homeAdapter.submitList(null)
        homeAdapter.notifyDataSetChanged()
    }

    fun updateItem(isLike: Boolean, position: Int) {
        homeAdapter.currentList?.get(position)?.isLike = isLike
        homeAdapter.currentList?.dataSource?.invalidate()

        homeAdapter.notifyItemChanged(position)
        homeAdapter.notifyItemRangeChanged(position, homeAdapter.itemCount)
    }
}
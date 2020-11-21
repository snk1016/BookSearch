package com.example.test.librarysearch.paging

import androidx.paging.PositionalDataSource
import com.example.test.librarysearch.model.response.Documents
import com.example.test.librarysearch.services.NetworkService

class BookPositionalDataSource(private val api: NetworkService, private val bookName: String) : PositionalDataSource<Documents>() {

    private var target = "title"

    private var pageSize = 0
    private var sumSize = 0

    private var isEnd = true

    override fun loadInitial(params: LoadInitialParams, callback: LoadInitialCallback<Documents>) {
        val firstLoadPosition = computeInitialLoadPosition(params, computeCount())
        val firstLoadSize = computeInitialLoadSize(params, firstLoadPosition, 50)

        api.searchBook(target, bookName, firstLoadPosition + 1, firstLoadSize)
            .subscribe { result ->
                result.meta?.let {
                    pageSize = it.pageableCount
                    isEnd = it.isEnd
                }

                result.documents?.let {
                    callback.onResult(it, firstLoadPosition, computeCount())
                    sumSize += it.size
                }
            }
    }

    override fun loadRange(params: LoadRangeParams, callback: LoadRangeCallback<Documents>) {
        if(pageSize > sumSize) {
            api.searchBook(target, bookName, (params.startPosition + params.loadSize)/params.loadSize, params.loadSize)
                .subscribe { result ->
                    result.meta?.let {
                        pageSize = it.pageableCount
                        isEnd = it.isEnd
                    }
                    result.documents?.let {
                        callback.onResult(it)
                        sumSize += it.size
                    }
                }
        }
    }

    private fun computeCount(): Int {
        return pageSize
    }
}
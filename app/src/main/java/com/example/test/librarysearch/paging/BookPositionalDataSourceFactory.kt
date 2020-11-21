package com.example.test.librarysearch.paging

import androidx.paging.DataSource
import com.example.test.librarysearch.model.response.Documents
import com.example.test.librarysearch.services.NetworkService

class BookPositionalDataSourceFactory(private val api: NetworkService, private val bookName: String) : DataSource.Factory<Int, Documents>() {

    private var bookPositionalDataSource: BookPositionalDataSource? = null

    override fun create(): DataSource<Int, Documents> {
        bookPositionalDataSource = BookPositionalDataSource(api, bookName)
        return bookPositionalDataSource!!
    }
}
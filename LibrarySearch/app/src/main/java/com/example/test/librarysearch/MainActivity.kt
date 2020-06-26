package com.example.test.librarysearch

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.View
import androidx.appcompat.widget.SearchView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
//        return super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.search, menu)
        val actionSearch = menu?.findItem(R.id.action_search)?.actionView as SearchView

        actionSearch.maxWidth = Int.MAX_VALUE
        actionSearch.queryHint = getString(R.string.hint_search)

//        actionSearch.setOnQueryTextFocusChangeListener()

        return true
    }

    private val mQueryTextListener = object: SearchView.OnQueryTextListener {
        override fun onQueryTextSubmit(query: String?): Boolean {
            return false
        }

        override fun onQueryTextChange(newText: String?): Boolean {
            return false
        }

    }
}
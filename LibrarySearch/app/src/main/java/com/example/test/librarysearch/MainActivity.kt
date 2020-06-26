package com.example.test.librarysearch

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.test.librarysearch.ui.home.HomeFragment
import com.example.test.librarysearch.ui.home.detail.HomeDetailFragment

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val transaction = supportFragmentManager.beginTransaction()
        transaction.apply {
            replace(R.id.fragmentContainer, HomeFragment())
            commit()
        }
    }

    fun moveDetailFragment(item: String) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.apply {
            replace(R.id.fragmentContainer, HomeDetailFragment(item))
            addToBackStack(null)
            commit()
        }
    }
}
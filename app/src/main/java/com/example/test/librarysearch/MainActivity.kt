package com.example.test.librarysearch

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.test.librarysearch.common.BackPressCloseHandler
import com.example.test.librarysearch.ui.home.HomeFragment
import com.example.test.librarysearch.ui.home.detail.HomeDetailFragment

class MainActivity : AppCompatActivity() {

    private lateinit var backPressCloseHandler: BackPressCloseHandler

    private var isLike: Boolean? = false

    private var position = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        backPressCloseHandler = BackPressCloseHandler(this)

        val transaction = supportFragmentManager.beginTransaction()
        transaction.apply {
            replace(R.id.fragmentContainer, HomeFragment())
            commit()
        }
    }

    override fun onBackPressed() {
        if (getVisibleFragment() == null || getVisibleFragment() is HomeFragment)
            backPressCloseHandler.onBackPressed()
        else
            super.onBackPressed()
    }

    private fun getVisibleFragment() : Fragment? {
        for (fragment in supportFragmentManager.fragments) {
            if (fragment.isVisible) {
                return fragment
            }
        }
        return null
    }

    fun moveDetailFragment(item: String, position: Int) {
        this.position = position
        val transaction = supportFragmentManager.beginTransaction()
        transaction.apply {
            replace(R.id.fragmentContainer, HomeDetailFragment(item))
            addToBackStack(null)
            commit()
        }
    }

    fun setLike(isLike: Boolean) {
        this.isLike = isLike
    }

    fun getLike(): Boolean? {
        return isLike
    }

    fun getCurrentPos(): Int {
        return position
    }

}
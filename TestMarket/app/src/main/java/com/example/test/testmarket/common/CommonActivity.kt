package com.example.test.testmarket.common

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.example.test.testmarket.R
import com.example.test.testmarket.ui.cart.CartFragment
import com.example.test.testmarket.ui.category.CategoryFragment
import com.example.test.testmarket.ui.order.OrderCompleteFragment
import com.example.test.testmarket.ui.order.OrderFragment
import com.example.test.testmarket.ui.product.ProductFragment
import com.example.test.testmarket.ui.product.detail.ProductFragmentDetail

class CommonActivity: AppCompatActivity() {

    private var pageTitle = "pageTitle"

    private var pageName = "pageName"

    private var pageCategory = "category"
    private var pageSubCategory = "subCategory"

    private var pageProductList = "productList"
    private var pageProductDetail = "productDetail"

    private var pageCart = "cart"
    private var pageOrder = "order"
    private var pageOrderComplete = "orderComplete"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_common)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        var it = intent

        if(it.hasExtra(pageName)) {
            var page = it.getStringExtra(pageName)
            var fragments : Fragment? = null

            when(page) {
                pageCategory,
                pageSubCategory -> {
                    fragments = CategoryFragment()
                    fragments.arguments = it.extras
                }

                pageProductList -> {
                    fragments = ProductFragment()
                    fragments.arguments = it.extras
                }

                pageProductDetail -> {
                    fragments = ProductFragmentDetail()
                    fragments.arguments = it.extras
                }

                pageCart -> {
                    fragments = CartFragment()
                    fragments.arguments = it.extras
                }

                pageOrder -> {
                    fragments = OrderFragment()
                    fragments.arguments = it.extras
                }
                pageOrderComplete -> {
                    fragments = OrderCompleteFragment()
                    fragments.arguments = it.extras
                }
            }

            fragments?.apply {
                supportFragmentManager.beginTransaction().add(R.id.layoutCommonBody, fragments, page).commit()
            }

        }

        if(it.hasExtra(pageTitle)) {
            title = it.getStringExtra(pageTitle)
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        var action = event?.action
        var x = event?.rawX
        var y = event?.rawY

        when(action) {
            MotionEvent.ACTION_DOWN -> {
                Log.d("nh", "ACTION_DOWN($x, $y)")
            }

            MotionEvent.ACTION_UP -> {
                Log.d("nh", "ACTION_UP($x, $y)")
            }

            MotionEvent.ACTION_MOVE -> {
                Log.d("nh", "ACTION_MOVE($x, $y)")
            }
        }

        return super.onTouchEvent(event)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        var it = intent

        if(it.hasExtra(pageName)) {
            if(it.getStringExtra(pageName) != pageCart && it.getStringExtra(pageName) != pageOrder && it.getStringExtra(pageName) != pageOrderComplete)
                menuInflater.inflate(R.menu.cart, menu)
        }

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.menuCart -> {
                var it = Intent(this, CommonActivity::class.java)
                it.putExtra("pageName", pageCart)
                startActivity(it)
            }
        }

        return super.onOptionsItemSelected(item)
    }
}
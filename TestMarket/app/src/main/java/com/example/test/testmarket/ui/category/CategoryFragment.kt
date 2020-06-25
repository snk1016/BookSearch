package com.example.test.testmarket.ui.category

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.example.test.testmarket.R
import com.example.test.testmarket.common.CommonActivity
import com.example.test.testmarket.databinding.FragmentCategoryBinding
import com.groobee.message.Groobee
import kotlin.properties.Delegates

class CategoryFragment : Fragment() {

    private lateinit var categoryViewModel: CategoryViewModel

//    private lateinit var mItemCli: AdapterCategory.OnItemClickListener

    private lateinit var binding: FragmentCategoryBinding

    private var categoryType by Delegates.notNull<Int>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_category, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        categoryViewModel = ViewModelProviders.of(this).get(CategoryViewModel::class.java)
        binding.viewModel = categoryViewModel
        binding.lifecycleOwner = this

        var extras = activity?.intent?.extras

        var categoryType = extras?.get("categoryType").toString()
        var categorySubType = extras?.get("categorySubType")
        this.categoryType = categoryType.toInt()

        if(categoryType.toInt() < 10)
            categoryType = "0${categoryType}"
        if(categorySubType != null && categorySubType.toString().toInt() < 10)
            categorySubType = "0${categorySubType}"

        var categoryArray = if(categorySubType == null) { resources.getStringArray(getResourceId("arrays_category_${categoryType}")) }
                            else { resources.getStringArray(getResourceId("arrays_sub_category_${categoryType}_${categorySubType}")) }

//        Groobee.getInstance().showDialog(activity, View.OnClickListener { Log.d("nh", "CLICK!!!") })

        categoryViewModel.setCategoryList(categoryArray, mItemClickListener)

        binding.listBody.adapter = categoryViewModel.adapterCategory
    }

    private var mItemClickListener = object: AdapterCategory.OnItemClickListener {
        override fun setOnItemClickListener(view: View, item: String, position: Int) {
            var extras = activity?.intent?.extras
            var categorySubType = extras?.get("categorySubType")
//            var categorySubEndType = extras?.get("categorySubEndType")

            if(categorySubType == null) {
                showActivity(item, position)
            } else {
                var it = Intent(activity, CommonActivity::class.java)
                var extras = activity?.intent?.extras
                extras?.putString("pageTitle", item)
                extras?.putInt("categorySubEndType", position)
                extras?.putString("pageName", "productList")
                it.putExtras(extras!!)
                startActivity(it)
                //상품리스트 이동
            }
        }
    }

    private fun showActivity(title: String, indexNo: Int) {
        var it = Intent(activity, CommonActivity::class.java)
        var extras = activity?.intent?.extras
        extras?.putString("pageTitle", title)
        extras?.putInt("categorySubType", indexNo)
        extras?.putString("pageName", "subCategory")
        it.putExtras(extras!!)
        startActivity(it)
    }

    private fun getResourceId(resourceName: String): Int {
        return try {
            resources.getIdentifier(resourceName, "array", activity?.packageName)
        } catch (e: Exception) {
            e.printStackTrace()
            -1
        }
    }
}

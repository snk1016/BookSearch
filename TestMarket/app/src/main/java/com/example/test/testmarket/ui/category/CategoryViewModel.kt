package com.example.test.testmarket.ui.category

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class CategoryViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is Category Fragment"
    }
    val text: LiveData<String> = _text

    var adapterCategory: AdapterCategory? = null

    private var _categoryItemTitle = MutableLiveData<String>()

    val categoryItemTitle: LiveData<String> = _categoryItemTitle

    fun setCategoryList(arrayCategory: Array<String>, listener: AdapterCategory.OnItemClickListener) {
        adapterCategory = AdapterCategory(arrayCategory)
        adapterCategory?.onItemClickListener = listener
        adapterCategory?.notifyDataSetChanged()
    }
}
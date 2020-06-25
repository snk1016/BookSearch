package com.example.test.librarysearch.model.response

import com.google.gson.annotations.SerializedName

data class Documents (
    @SerializedName("contents")
    val contents: String,

    @SerializedName("datetime")
    val datetime: String,

    @SerializedName("price")
    val price: Int,

    @SerializedName("publisher")
    val publisher: String,

    @SerializedName("sale_price")
    val salePrice: Int,

    @SerializedName("status")
    val status: String,

    @SerializedName("thumbnail")
    val thumbnail: String,

    @SerializedName("title")
    val title: String,

    @SerializedName("url")
    val url: String,

    @SerializedName("authors")
    val authors: MutableList<String>,

    @SerializedName("translators")
    val translators: MutableList<String>



/*
* "datetime": "2016-02-01T00:00:00.000+09:00",
            "isbn": "129201833X 9781292018331",
            "price": 40000,
            "publisher": "Pearson",
            "sale_price": 40000,
            "status": "정상판매",
            "thumbnail": "https://search1.kakaocdn.net/thumb/R120x174.q85/?fname=http%3A%2F%2Ft1.daumcdn.net%2Flbook%2Fimage%2F3383739%3Ftimestamp%3D20190220072908",
            "title": "Java",
            "translators": [],
            "url": "https://search.daum.net/search?w=bookpage&bookId=3383739&q=Java"
* */
)
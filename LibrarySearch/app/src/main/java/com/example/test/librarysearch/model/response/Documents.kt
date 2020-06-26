package com.example.test.librarysearch.model.response

import com.example.test.librarysearch.common.FormatUtils
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Documents (
    @SerializedName("contents")
    val contents: String,

    @SerializedName("datetime")
    val datetime: String,

    @SerializedName("isbn")
    val isbn: String,

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

): Serializable {
    val unitPrice: String get() = FormatUtils.toUnitWon(price)
    val unitSalePrice: String get() = "(${FormatUtils.toUnitWon(salePrice)})"

    val allAuthor: String get() = authors.toString().replace("[", "").replace("]", "")

}
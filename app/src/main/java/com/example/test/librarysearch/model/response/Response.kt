package com.example.test.librarysearch.model.response

import com.google.gson.annotations.SerializedName

data class Response (
    @SerializedName("documents")
    val documents: MutableList<Documents>? = null,

    @SerializedName("meta")
    val meta: Meta? = null
)

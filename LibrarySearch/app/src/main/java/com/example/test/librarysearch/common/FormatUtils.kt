package com.example.test.librarysearch.common

class FormatUtils {

    companion object {
        fun toUnitWon(num: Int): String {
            return String.format("%,d원", num)
        }
    }
}
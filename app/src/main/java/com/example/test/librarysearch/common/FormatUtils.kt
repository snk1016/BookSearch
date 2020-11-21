package com.example.test.librarysearch.common

import java.text.SimpleDateFormat

class FormatUtils {

    companion object {
        fun toUnitWon(num: Int): String {
            return String.format("%,d원", num)
        }

        fun toDateFormatter(date: String): String {
            val format = SimpleDateFormat("yyyy-MM-dd")
            return format.format(format.parse(date))
        }
    }
}
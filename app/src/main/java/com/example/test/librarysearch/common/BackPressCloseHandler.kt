package com.example.test.librarysearch.common

import android.app.Activity
import android.widget.Toast
import com.example.test.librarysearch.R

class BackPressCloseHandler {
    private var backKeyClickTime = 0L
    private var activity: Activity

    constructor(activity: Activity) {
        this.activity = activity
    }

    fun onBackPressed() {
        if (System.currentTimeMillis() > backKeyClickTime + 2000) {
            backKeyClickTime = System.currentTimeMillis();
            showToast();
            return;
        }

        if (System.currentTimeMillis() <= backKeyClickTime + 2000) {
            activity.finish();
        }
    }

    private fun showToast() {
        Toast.makeText(activity, activity.getString(R.string.message_back_press), Toast.LENGTH_SHORT).show();
    }
}
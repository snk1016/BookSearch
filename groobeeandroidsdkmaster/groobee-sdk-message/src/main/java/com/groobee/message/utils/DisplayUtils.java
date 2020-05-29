package com.groobee.message.utils;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

public class DisplayUtils {
    private static final String TAG = LoggerUtils.getClassLogTag(DisplayUtils.class);

    public static DisplayMetrics getDefaultScreenDisplayMetrics(Context context) {
        WindowManager windowManager = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics;
    }

    public static int getPixelsFromDensityAndDp(int dpi, int dp) {
        return Math.abs(dpi * dp / 160);
    }

    public static int getPixelsFromDp(Context context, int dp) {
        DisplayMetrics displayMetrics = getDefaultScreenDisplayMetrics(context);
        return Math.abs(displayMetrics.densityDpi * dp / 160);
    }
}

package com.groobee.message.utils;

import android.content.Context;

public class PackageUtils {
    private static final String TAG = LoggerUtils.getClassLogTag(PackageUtils.class);
    private static String packageName;

    public PackageUtils() {
    }

    public static void setResourcePackageName(String packageName) {
        if (!StringUtils.isNullOrBlank(packageName)) {
            PackageUtils.packageName = packageName;
        } else {
            LoggerUtils.e(TAG, "Package name may not be null or blank");
        }

    }

    public static String getResourcePackageName(Context context) {
        if (packageName != null) {
            return packageName;
        } else {
            packageName = context.getPackageName();
            return packageName;
        }
    }
}

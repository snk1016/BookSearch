package com.groobee.message.push.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;

import com.groobee.message.utils.LoggerUtils;

public class UriUtils {
    private static final String TAG = LoggerUtils.getClassLogTag(UriUtils.class);

    public static Intent getMainActivityIntent(Context context, Bundle extras) {
        // get main activity intent.
        Intent startActivityIntent = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
        startActivityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        if (extras != null) {
            startActivityIntent.putExtras(extras);
        }
        return startActivityIntent;
    }

    public static boolean isActivityRegisteredInManifest(Context context, String className) {
        try {
            ActivityInfo activityInfo = context.getPackageManager().getActivityInfo(new ComponentName(context, className), 0);
            return activityInfo != null;
        } catch (PackageManager.NameNotFoundException e) {
            LoggerUtils.w(TAG, "Could not find activity info for class with name: " + className, e);
            return false;
        }
    }
}

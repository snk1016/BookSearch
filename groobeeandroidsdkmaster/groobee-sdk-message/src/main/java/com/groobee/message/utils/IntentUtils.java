package com.groobee.message.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.os.Bundle;

import com.groobee.message.push.utils.UriUtils;

import java.util.Iterator;
import java.util.List;
import java.util.Random;

public final class IntentUtils {

    private static final String TAG = LoggerUtils.getClassLogTag(IntentUtils.class);

    private static final Random random = new Random();

    public IntentUtils() {}

    public static int getRequestCode() { return random.nextInt(); }

    public static void sendComponent(Context context, Intent intent) {
        List receivers = context.getPackageManager().queryBroadcastReceivers(intent, 0);

        if (receivers == null) {
            LoggerUtils.d(TAG, "No components found for the following intent: " + intent.getAction());
        } else {
            Iterator iterator = receivers.iterator();

            while(iterator.hasNext()) {
                ResolveInfo resolveInfo = (ResolveInfo)iterator.next();
                Intent it = new Intent(intent);
                ComponentName var6 = new ComponentName(resolveInfo.activityInfo.applicationInfo.packageName, resolveInfo.activityInfo.name);
                it.setComponent(var6);
                context.sendBroadcast(it);
            }

        }
    }

    public static Intent setActivity(Context context, String ActivityName, Bundle extras) {
        Intent it = null;

        if (UriUtils.isActivityRegisteredInManifest(context, ActivityName)) {
            it = new Intent()
                    .setClassName(context, ActivityName)
                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    .putExtras(extras);
        } else {
            LoggerUtils.i(TAG, "Not Found Activity: " + ActivityName);
        }

        return it;
    }
}

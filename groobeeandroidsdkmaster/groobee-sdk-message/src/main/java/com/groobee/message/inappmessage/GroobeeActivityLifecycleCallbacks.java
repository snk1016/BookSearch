package com.groobee.message.inappmessage;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.groobee.message.inappmessage.interfaces.GroobeeInAppMessagingDisplay;
import com.groobee.message.inappmessage.interfaces.GroobeeInAppMessagingDisplayCallbacks;
import com.groobee.message.inappmessage.model.InAppMessage;
import com.groobee.message.inappmessage.utils.RenewableTimer;
import com.groobee.message.providers.RuntimeConfigProvider;

public class GroobeeActivityLifecycleCallbacks implements GroobeeInAppMessagingDisplay, Application.ActivityLifecycleCallbacks {
    private final int SESSION_CLOSING_DURATION = (30 * 60) * 1000;
    private final int TIMER_INTERVAL_SECOND = 1000;

    private RenewableTimer sessionPushOpenTimer = null;
    private RenewableTimer sessionTimer = null;

    private RuntimeConfigProvider runtimeConfigProvider;

    private SharedPreferences sharedPreferences = null;

    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
        if(sharedPreferences == null)
            sharedPreferences = activity.getSharedPreferences(RuntimeConfigProvider.PREFERENCES_NAME, Context.MODE_PRIVATE);
    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) { }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {
        runtimeConfigProvider = new RuntimeConfigProvider(activity.getApplicationContext());
        Boolean session = runtimeConfigProvider.getData(RuntimeConfigProvider.PREFERENCES_SESSION, false);

        if(sessionPushOpenTimer != null) {
            sessionPushOpenTimer.cancel();
            sessionPushOpenTimer = null;
        }

        if(sessionTimer != null) {
            sessionTimer.cancel();
            sessionTimer = null;
        }

        if(!session) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            runtimeConfigProvider.addData(editor, RuntimeConfigProvider.PREFERENCES_SESSION, true);
            editor.apply();
        }
    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {  }

    @Override
    public void onActivityStopped(@NonNull Activity activity) {
        String sessionPushId = runtimeConfigProvider.getData(RuntimeConfigProvider.PREFERENCES_PUSH_UUID, "");
        Boolean session = runtimeConfigProvider.getData(RuntimeConfigProvider.PREFERENCES_SESSION, false);

        if(!sessionPushId.isEmpty()) {
            if (sessionPushOpenTimer == null) {
                sessionPushOpenTimer = new RenewableTimer();

                sessionPushOpenTimer.start(new RenewableTimer.Callback() {
                    @Override
                    public void onFinish() {
                        sessionPushOpenTimer = null;
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        runtimeConfigProvider.addData(editor, RuntimeConfigProvider.PREFERENCES_PUSH_UUID, "");
                        editor.apply();
                    }
                }, SESSION_CLOSING_DURATION, TIMER_INTERVAL_SECOND);
            }
        }

        if(session) {
            if (sessionTimer == null) {
                sessionTimer = new RenewableTimer();

                sessionTimer.start(new RenewableTimer.Callback() {
                    @Override
                    public void onFinish() {
                        sessionTimer = null;
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        runtimeConfigProvider.addData(editor, RuntimeConfigProvider.PREFERENCES_SESSION, false);
                        editor.apply();
                    }
                }, SESSION_CLOSING_DURATION, TIMER_INTERVAL_SECOND);
            }
        }
    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) { }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) { }

    @Override
    public void onDisplayMessage(InAppMessage inAppMessage, GroobeeInAppMessagingDisplayCallbacks callbacks) { }
}

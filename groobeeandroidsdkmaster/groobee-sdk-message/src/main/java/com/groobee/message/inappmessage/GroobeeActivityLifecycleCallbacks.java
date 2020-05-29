package com.groobee.message.inappmessage;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.groobee.message.Groobee;
import com.groobee.message.inappmessage.interfaces.GroobeeInAppMessagingDisplay;
import com.groobee.message.inappmessage.interfaces.GroobeeInAppMessagingDisplayCallbacks;
import com.groobee.message.inappmessage.model.InAppMessage;

public class GroobeeActivityLifecycleCallbacks implements GroobeeInAppMessagingDisplay, Application.ActivityLifecycleCallbacks {
    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {

    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {

    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {
//        Groobee.getInstance().onActivityResumed(activity);
    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {
//        Groobee.getInstance().onActivityPaused(activity);
    }

    @Override
    public void onActivityStopped(@NonNull Activity activity) {
    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {
//        Groobee.getInstance().onActivityDestroyed(activity);
    }

    @Override
    public void onDisplayMessage(InAppMessage inAppMessage, GroobeeInAppMessagingDisplayCallbacks callbacks) {

    }
}

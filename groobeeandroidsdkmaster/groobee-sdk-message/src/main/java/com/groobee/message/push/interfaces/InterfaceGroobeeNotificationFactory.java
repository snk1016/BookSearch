package com.groobee.message.push.interfaces;

import android.app.Notification;
import android.content.Context;
import android.os.Bundle;

import com.groobee.message.providers.GroobeeConfigProvider;

public interface InterfaceGroobeeNotificationFactory {
    Notification createNotification(GroobeeConfigProvider groobeeConfigProvider, Context context, Bundle notificationExtra, Bundle extras);
}

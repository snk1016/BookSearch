package com.groobee.message.push.factorys;

import android.app.Notification;
import android.content.Context;
import android.os.Bundle;

import androidx.core.app.NotificationCompat;

import com.groobee.message.providers.GroobeeConfigProvider;
import com.groobee.message.push.interfaces.InterfaceGroobeeNotificationFactory;
import com.groobee.message.push.utils.GroobeeNotificationActionUtils;
import com.groobee.message.push.utils.GroobeeNotificationUtils;

public class GroobeeNotificationFactory implements InterfaceGroobeeNotificationFactory {

    private static volatile GroobeeNotificationFactory instance = null;

    public static GroobeeNotificationFactory getInstance() {
        if (instance == null) {
            synchronized (GroobeeNotificationFactory.class) {
                if (instance == null) {
                    instance = new GroobeeNotificationFactory();
                }
            }
        }

        return instance;
    }

    @Override
    public Notification createNotification(GroobeeConfigProvider groobeeConfigProvider, Context context, Bundle notificationExtra, Bundle extras) {
        return setNotificationBuilder(groobeeConfigProvider, context, notificationExtra, extras).build();
    }

    private NotificationCompat.Builder setNotificationBuilder(GroobeeConfigProvider groobeeConfigProvider
            , Context context, Bundle notificationExtras, Bundle extras) {
//        GroobeeNotificationUtils.prefetchBitmapsIfNewlyReceivedStoryPush(context, notificationExtras, extras);

        String notificationChannelId = GroobeeNotificationUtils.getOrCreateNotificationChannelId(context, groobeeConfigProvider, notificationExtras);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, notificationChannelId)
                .setAutoCancel(true);

        GroobeeNotificationUtils.setContentTitle(notificationBuilder, notificationExtras);
        GroobeeNotificationUtils.setContentText(notificationBuilder, notificationExtras);
        GroobeeNotificationUtils.setTicker(notificationBuilder, notificationExtras);
//        GroobeeNotificationUtils.setSetShowWhen(notificationBuilder, notificationExtras);

        // Add intent to fire when the notification is opened or deleted.
        GroobeeNotificationUtils.setContentIntent(context, notificationBuilder, notificationExtras);
        GroobeeNotificationUtils.setDeleteIntent(context, notificationBuilder, notificationExtras);
        GroobeeNotificationUtils.setSmallIcon(groobeeConfigProvider, notificationBuilder);

        GroobeeNotificationUtils.setLargeIcon(context, groobeeConfigProvider, notificationBuilder, notificationExtras);
        GroobeeNotificationUtils.setSound(notificationBuilder, notificationExtras);

        // Subtext, priority, notification actions, and styles were added in JellyBean.
        GroobeeNotificationUtils.setSummaryText(notificationBuilder, notificationExtras);
//        GroobeeNotificationUtils.setPriorityIfPresentAndSupported(notificationBuilder, notificationExtras);
        GroobeeNotificationUtils.setStyle(context, notificationBuilder, notificationExtras);
        GroobeeNotificationActionUtils.addNotificationActions(context, notificationBuilder, notificationExtras);

        // Accent color, category, visibility, and public notification were added in Lollipop.
        GroobeeNotificationUtils.setAccentColor(groobeeConfigProvider, notificationBuilder, notificationExtras);
        GroobeeNotificationUtils.setCategory(notificationBuilder, notificationExtras);
        GroobeeNotificationUtils.setVisibility(notificationBuilder, notificationExtras);

        // Notification priority and sound were deprecated in Android O
        GroobeeNotificationUtils.setNotificationBadgeNumber(notificationBuilder, notificationExtras);

        return notificationBuilder;
    }
}

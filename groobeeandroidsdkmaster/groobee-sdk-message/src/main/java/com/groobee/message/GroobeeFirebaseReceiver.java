package com.groobee.message;

import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.core.app.NotificationManagerCompat;

import com.groobee.message.common.Constants;
import com.groobee.message.providers.GroobeeConfigProvider;
import com.groobee.message.push.interfaces.InterfaceGroobeeNotificationFactory;
import com.groobee.message.push.utils.GroobeeNotificationActionUtils;
import com.groobee.message.push.utils.GroobeeNotificationUtils;
import com.groobee.message.utils.LoggerUtils;

public class GroobeeFirebaseReceiver extends BroadcastReceiver {

    private static final String TAG = LoggerUtils.getClassLogTag(GroobeeConfig.class);

    private static final String FCM_RECEIVE_INTENT_ACTION = "com.google.android.c2dm.intent.RECEIVE";
    private static final String FCM_MESSAGE_TYPE_KEY = "message_type";
    private static final String FCM_DELETED_MESSAGES_KEY = "deleted_messages";
    private static final String FCM_NUMBER_OF_MESSAGES_DELETED_KEY = "total_deleted";

    protected static final String FIREBASE_MESSAGING_SERVICE_ROUTING_ACTION = "firebase_messaging_service_routing_action";

    @Override
    public void onReceive(Context context, Intent intent) {
        LoggerUtils.i(TAG, context.getString(R.string.GROOBEE_FIREBASE_RECEIVER_ON_RECEIVE, intent.toString()));

        String action = intent.getAction();
        if (FCM_RECEIVE_INTENT_ACTION.equals(action) || FIREBASE_MESSAGING_SERVICE_ROUTING_ACTION.equals(action)) {
            handleReceiveMessage(context, intent);
        } else if (Constants.PUSH_CANCEL_NOTIFICATION_ACTION.equals(action)) {
            GroobeeNotificationUtils.handleCancelNotificationAction(context, intent);
        } else if (Constants.PUSH_ACTION_CLICKED_ACTION.equals(action)) {
            GroobeeNotificationActionUtils.handleNotificationActionClicked(context, intent);
        } /*else if (Constants.APPBOY_STORY_TRAVERSE_CLICKED_ACTION.equals(action)) {
            handleAppboyFcmReceiveIntent(context, intent);
        } else if (Constants.APPBOY_STORY_CLICKED_ACTION.equals(action)) {
            GroobeeNotificationUtils.handlePushStoryPageClicked(context, intent);
        } */else if (Constants.PUSH_CLICKED_ACTION.equals(action)) {
            GroobeeNotificationUtils.handleNotificationOpened(context, intent);
        } else if (Constants.PUSH_DELETED_ACTION.equals(action)) {
            GroobeeNotificationUtils.handleNotificationDeleted(context, intent);
        } else {
            LoggerUtils.w(TAG, context.getString(R.string.GROOBEE_FIREBASE_RECEIVER_ON_RECEIVE_NOT_MATCHING_ACTION));
        }
    }

    public boolean handleReceiveMessage(Context context, Intent intent) {
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        String messageType = intent.getStringExtra(FCM_MESSAGE_TYPE_KEY);
        if (FCM_DELETED_MESSAGES_KEY.equals(messageType)) {
            int totalDeleted = intent.getIntExtra(FCM_NUMBER_OF_MESSAGES_DELETED_KEY, -1);
            if (totalDeleted == -1) {
                LoggerUtils.e(TAG, context.getString(R.string.GROOBEE_FIREBASE_RECEIVER_HANDLE_RECEIVE_MESSAGE_UNABLE_FCM, intent.toString()));
            } else {
                LoggerUtils.i(TAG, context.getString(R.string.GROOBEE_FIREBASE_RECEIVER_HANDLE_RECEIVE_MESSAGE_FCM_DELETE, String.valueOf(totalDeleted)));
            }
            return false;
        } else {
            Bundle fcmExtras = intent.getExtras();
            LoggerUtils.i(TAG, context.getString(R.string.GROOBEE_FIREBASE_RECEIVER_HANDLE_RECEIVE_MESSAGE, fcmExtras));

            Bundle extras = new Bundle();
            int notificationId = GroobeeNotificationUtils.getNotificationId(fcmExtras);

            GroobeeConfigProvider groobeeConfigProvider = new GroobeeConfigProvider(context);
            InterfaceGroobeeNotificationFactory interfaceGroobeeNotificationFactory = GroobeeNotificationUtils.getActiveNotificationFactory();
            Notification notification = interfaceGroobeeNotificationFactory.createNotification(groobeeConfigProvider, context, fcmExtras, extras);
            notificationManager.notify(Constants.PUSH_NOTIFICATION_TAG, notificationId, notification);

            return true;
        }
    }
}

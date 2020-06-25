package com.groobee.message;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.groobee.message.utils.LoggerUtils;

import java.util.Map;

public class GroobeeFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = LoggerUtils.getClassLogTag(GroobeeFirebaseMessagingService.class);
    private static final GroobeeFirebaseReceiver groobeeFirebaseReceiver = new GroobeeFirebaseReceiver();

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        handleRemoteMessage(this, remoteMessage);
    }

    public static boolean handleRemoteMessage(Context context, RemoteMessage remoteMessage) {
        if(remoteMessage == null) {
            LoggerUtils.w(TAG, context.getString(R.string.GROOBEE_FIREBASE_MESSAGING_SERVICE_HANDLE_REMOTE_MESSAGE_IS_NULL));
            return false;
        }

        if(remoteMessage.getData() == null) {
            LoggerUtils.w(TAG, context.getString(R.string.GROOBEE_FIREBASE_MESSAGING_SERVICE_HANDLE_REMOTE_MESSAGE_DATA_IS_NULL));
            return false;
        }

        Map<String, String> remoteMessageData = remoteMessage.getData();
        LoggerUtils.i(TAG, context.getString(R.string.GROOBEE_FIREBASE_MESSAGING_SERVICE_HANDLE_REMOTE_MESSAGE, String.valueOf(remoteMessageData)));

        RemoteMessage.Notification notification = null;

//        if(remoteMessageData.size() > 0) {
        Intent pushIntent = new Intent(GroobeeFirebaseReceiver.FIREBASE_MESSAGING_SERVICE_ROUTING_ACTION);
        Bundle bundle = new Bundle();
        for (Map.Entry<String, String> entry : remoteMessageData.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();

            LoggerUtils.v(TAG, context.getString(R.string.GROOBEE_FIREBASE_MESSAGING_SERVICE_HANDLE_REMOTE_MESSAGE_ITEM, key, value));
            bundle.putString(key, value);
        }
        pushIntent.putExtras(bundle);
        groobeeFirebaseReceiver.onReceive(context, pushIntent);
//        } else
//            notification = remoteMessage.getNotification();

//        if(notification != null) {
//            notification.get
//        }

        return true;
    }
}

package com.groobee.message;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;

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
            LoggerUtils.w(TAG, "Remote message from FCM was null.");
            return false;
        }

        if(remoteMessage.getData() == null) {
            LoggerUtils.w(TAG, "Remote message data from FCM was null.");
            return false;
        }

        Map<String, String> remoteMessageData = remoteMessage.getData();
        LoggerUtils.i(TAG, "Got remote message from FCM: " + remoteMessageData);

        RemoteMessage.Notification notification = null;

//        if(remoteMessageData.size() > 0) {
            Intent pushIntent = new Intent(GroobeeFirebaseReceiver.FIREBASE_MESSAGING_SERVICE_ROUTING_ACTION);
            Bundle bundle = new Bundle();
            for (Map.Entry<String, String> entry : remoteMessageData.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();

                LoggerUtils.v(TAG, "Adding bundle item from FCM remote data with key: " + key + " and value: " + value);
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

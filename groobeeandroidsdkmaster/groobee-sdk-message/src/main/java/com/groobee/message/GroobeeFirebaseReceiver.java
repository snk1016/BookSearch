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
        LoggerUtils.i(TAG, "Received broadcast message. Message: " + intent.toString());

        String action = intent.getAction();
        if (FCM_RECEIVE_INTENT_ACTION.equals(action) || FIREBASE_MESSAGING_SERVICE_ROUTING_ACTION.equals(action)) {
//            handleAppboyFcmReceiveIntent(context, intent);
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
            LoggerUtils.w(TAG, "The FCM receiver received a message not sent from sdk. Ignoring the message.");
        }
    }

    public boolean handleReceiveMessage(Context context, Intent intent) {
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        String messageType = intent.getStringExtra(FCM_MESSAGE_TYPE_KEY);
        if (FCM_DELETED_MESSAGES_KEY.equals(messageType)) {
            int totalDeleted = intent.getIntExtra(FCM_NUMBER_OF_MESSAGES_DELETED_KEY, -1);
            if (totalDeleted == -1) {
                LoggerUtils.e(TAG, "Unable to parse FCM message. Intent: " + intent.toString());
            } else {
                LoggerUtils.i(TAG, "FCM deleted " + totalDeleted + " messages.");
            }
            return false;
        } else {
            Bundle fcmExtras = intent.getExtras();
            LoggerUtils.i(TAG, "Push message payload received: " + fcmExtras);

            Bundle extras = new Bundle();
            int notificationId = GroobeeNotificationUtils.getNotificationId(fcmExtras);

            GroobeeConfigProvider groobeeConfigProvider = new GroobeeConfigProvider(context);
            InterfaceGroobeeNotificationFactory interfaceGroobeeNotificationFactory = GroobeeNotificationUtils.getActiveNotificationFactory();
            Notification notification = interfaceGroobeeNotificationFactory.createNotification(groobeeConfigProvider, context, fcmExtras, extras);
            notificationManager.notify(Constants.PUSH_NOTIFICATION_TAG, notificationId, notification);

            return true;

//            // Parsing the Appboy data extras (data push).
//            // We convert the JSON in the extras key into a Bundle.
//            Bundle appboyExtras = GroobeeNotificationUtils.getAppboyExtrasWithoutPreprocessing(fcmExtras);
//            fcmExtras.putBundle(Constants.APPBOY_PUSH_EXTRAS_KEY, appboyExtras);
//
//            if (!fcmExtras.containsKey(Constants.APPBOY_PUSH_RECEIVED_TIMESTAMP_MILLIS)) {
//                fcmExtras.putLong(Constants.APPBOY_PUSH_RECEIVED_TIMESTAMP_MILLIS, System.currentTimeMillis());
//            }
//
//            // This call must occur after the "extras" parsing above since we're expecting
//            // a bundle instead of a raw JSON string for the APPBOY_PUSH_EXTRAS_KEY key
//            if (GroobeeNotificationUtils.isUninstallTrackingPush(fcmExtras)) {
//                // Note that this re-implementation of this method does not forward the notification to receivers.
//                LoggerUtils.i(TAG, "Push message is uninstall tracking push. Doing nothing. Not forwarding this notification to broadcast receivers.");
//                return false;
//            }
//
//            GroobeeConfigProvider groobeeConfigProvider = new GroobeeConfigProvider(context);
//            if (groobeeConfigProvider.getIsInAppMessageTestPushEagerDisplayEnabled()
//                    && GroobeeNotificationUtils.isInAppMessageTestPush(intent)
//                    /*&& AppboyInAppMessageManager.getInstance().getActivity() != null*/) {
//                // Pass this test in-app message along for eager display and bypass displaying a push
//                LoggerUtils.d(TAG, "Bypassing push display due to test in-app message presence and "
//                        + "eager test in-app message display configuration setting.");
//                AppboyInternal.handleInAppMessageTestPush(context, intent);
//                return false;
//            }
//
//            // Parse the notification for any associated ContentCard
//            GroobeeNotificationUtils.handleContentCardsSerializedCardIfPresent(context, fcmExtras);
//
//            if (GroobeeNotificationUtils.isNotificationMessage(intent)) {
//                LoggerUtils.d(TAG, "Received notification push");
//                int notificationId = GroobeeNotificationUtils.getNotificationId(fcmExtras);
//                fcmExtras.putInt(Constants.APPBOY_PUSH_NOTIFICATION_ID, notificationId);
//                InterfaceGroobeeNotificationFactory interfaceGroobeeNotificationFactory = GroobeeNotificationUtils.getActiveNotificationFactory();
//
//                if (fcmExtras.containsKey(Constants.APPBOY_PUSH_STORY_KEY)) {
//                    if (!fcmExtras.containsKey(Constants.APPBOY_PUSH_STORY_IS_NEWLY_RECEIVED)) {
//                        LoggerUtils.d(TAG, "Received the initial push story notification.");
//                        fcmExtras.putBoolean(Constants.APPBOY_PUSH_STORY_IS_NEWLY_RECEIVED, true);
//                        // Log the push delivery event for the initial push story notification
//                        GroobeeNotificationUtils.logPushDeliveryEvent(context, fcmExtras);
//                    }
//                } else {
//                    // Log the push delivery event for regular foreground push
//                    GroobeeNotificationUtils.logPushDeliveryEvent(context, fcmExtras);
//                }
//
//                Notification notification = interfaceGroobeeNotificationFactory.createNotification(groobeeConfigProvider, context, fcmExtras, appboyExtras);
//
//                if (notification == null) {
//                    LoggerUtils.d(TAG, "Notification created by notification factory was null. Not displaying notification.");
//                    return false;
//                }
//
//                notificationManager.notify(Constants.APPBOY_PUSH_NOTIFICATION_TAG, notificationId, notification);
//                GroobeeNotificationUtils.sendPushMessageReceivedBroadcast(context, fcmExtras);
//                GroobeeNotificationUtils.wakeScreenIfAppropriate(context, appConfigurationProvider, fcmExtras);
//
//                // Set a custom duration for this notification.
//                if (fcmExtras != null && fcmExtras.containsKey(Constants.APPBOY_PUSH_NOTIFICATION_DURATION_KEY)) {
//                    int durationInMillis = Integer.parseInt(fcmExtras.getString(Constants.APPBOY_PUSH_NOTIFICATION_DURATION_KEY));
//                    GroobeeNotificationUtils.setNotificationDurationAlarm(context, this.getClass(), notificationId, durationInMillis);
//                }
//
//                return true;
//            } else {
//                LoggerUtils.d(TAG, "Received data push");
//                // Log the push delivery event
//                GroobeeNotificationUtils.logPushDeliveryEvent(context, fcmExtras);
//                GroobeeNotificationUtils.sendPushMessageReceivedBroadcast(context, fcmExtras);
//                GroobeeNotificationUtils.requestGeofenceRefreshIfAppropriate(context, fcmExtras);
//                return false;
//            }
        }
    }
}

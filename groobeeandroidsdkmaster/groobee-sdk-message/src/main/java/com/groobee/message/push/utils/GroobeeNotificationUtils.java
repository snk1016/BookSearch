
package com.groobee.message.push.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.core.app.NotificationCompat;

import com.groobee.message.Groobee;
import com.groobee.message.GroobeeFirebaseReceiver;
import com.groobee.message.R;
import com.groobee.message.common.Channel;
import com.groobee.message.common.Constants;
import com.groobee.message.providers.GroobeeConfigProvider;
import com.groobee.message.push.actions.ActionFactory;
import com.groobee.message.push.actions.UriAction;
import com.groobee.message.push.factorys.GroobeeNotificationFactory;
import com.groobee.message.push.factorys.GroobeeNotificationStyleFactory;
import com.groobee.message.push.interfaces.InterfaceGroobeeNotificationFactory;
import com.groobee.message.utils.IntentUtils;
import com.groobee.message.utils.LoggerUtils;
import com.groobee.message.utils.StringUtils;

public class GroobeeNotificationUtils {

    private static final String TAG = LoggerUtils.getClassLogTag(GroobeeNotificationUtils.class);

    public static final String PUSH_NOTIFICATION_OPENED_SUFFIX = ".intent.PUSH_NOTIFICATION_OPENED";
    public static final String PUSH_NOTIFICATION_RECEIVED_SUFFIX = ".intent.PUSH_NOTIFICATION_RECEIVED";
    public static final String PUSH_NOTIFICATION_DELETED_SUFFIX = ".intent.PUSH_NOTIFICATION_DELETED";

    public static void handleCancelNotificationAction(Context context, Intent intent) {
        try {
            if (intent.hasExtra(Constants.PUSH_NOTIFICATION_ID)) {
                int notificationId = intent.getIntExtra(Constants.PUSH_NOTIFICATION_ID, Constants.PUSH_DEFAULT_NOTIFICATION_ID);
                LoggerUtils.d(TAG, context.getString(R.string.GROOBEE_NOTIFICATION_UTILS_HANDLE_CANCEL_NOTIFICATION_ACTION, String.valueOf(notificationId)));
                NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.cancel(Constants.PUSH_NOTIFICATION_TAG, notificationId);
            }
        } catch (Exception e) {
            LoggerUtils.e(TAG, context.getString(R.string.GROOBEE_NOTIFICATION_UTILS_HANDLE_CANCEL_NOTIFICATION_ACTION_EXCEPTION), e);
        }
    }

    public static void handleNotificationOpened(Context context, Intent intent) {
        try {
            logNotificationOpened(context, intent);
            sendNotificationOpenedBroadcast(context, intent);
            GroobeeConfigProvider groobeeConfigProvider = new GroobeeConfigProvider(context);
            if (groobeeConfigProvider.getHandlePushDeepLinks() || groobeeConfigProvider.getPushMoveActivityEnabled()) {
                notificationOpenedIntent(context, intent, groobeeConfigProvider.getPushMoveActivityEnabled());
            }

        } catch (Exception e) {
            LoggerUtils.e(TAG, context.getString(R.string.GROOBEE_NOTIFICATION_UTILS_HANDLE_NOTIFICATION_OPENED_EXCEPTION), e);
        }
    }

    public static void handleNotificationDeleted(Context context, Intent intent) {
        try {
            LoggerUtils.d(TAG, context.getString(R.string.GROOBEE_NOTIFICATION_UTILS_HANDLE_NOTIFICATION_DELETED));
            sendPushActionIntent(context, GroobeeNotificationUtils.PUSH_NOTIFICATION_DELETED_SUFFIX, intent.getExtras());
        } catch (Exception e) {
            LoggerUtils.e(TAG, context.getString(R.string.GROOBEE_NOTIFICATION_UTILS_HANDLE_NOTIFICATION_DELETED_EXCEPTION), e);
        }
    }

    public static void notificationOpenedIntent(Context context, Intent intent, boolean isActMoveEnabled) {
        // get extras bundle.
        Bundle extras = intent.getBundleExtra(Constants.PUSH_EXTRAS_KEY);
        if (extras == null) {
            extras = new Bundle();
        }
        extras.putString(Constants.PUSH_CAMPAIGN_ID_KEY, intent.getStringExtra(Constants.PUSH_CAMPAIGN_ID_KEY));

        // If a deep link exists, start an ACTION_VIEW intent pointing at the deep link.
        // The intent returned from getStartActivityIntent() is placed on the back stack.
        // Otherwise, start the intent defined in getStartActivityIntent(). PUSH_URL_LINK_KEY
        String deepLink = intent.getStringExtra(Constants.PUSH_DEEP_LINK_KEY);
        String urlLink = intent.getStringExtra(Constants.PUSH_URL_LINK_KEY);
        if (!StringUtils.isNullOrBlank(deepLink)) {
            LoggerUtils.d(TAG, context.getString(R.string.GROOBEE_NOTIFICATION_UTILS_NOTIFICATION_OPENED_INTENT_FOUND_DEEP_LINK, deepLink));
//            boolean useWebView = "true".equalsIgnoreCase(intent.getStringExtra(Constants.PUSH_OPEN_URI_IN_WEBVIEW_KEY));
//            LoggerUtils.d(TAG, "Use webview set to: " + useWebView);

            // pass deep link and use webview values to target activity.
            extras.putString(Constants.PUSH_DEEP_LINK_KEY, deepLink);
//            extras.putBoolean(Constants.PUSH_OPEN_URI_IN_WEBVIEW_KEY, useWebView);

            UriAction uriAction = ActionFactory.createUriActionFromUrlString(deepLink, extras, Channel.PUSH);
            uriAction.execute(context);
            return;
        } else if (StringUtils.isNullOrBlank(deepLink) && !StringUtils.isNullOrBlank(urlLink)) {
            extras.putString(Constants.PUSH_URL_LINK_KEY, urlLink);
            UriAction uriAction = ActionFactory.createUriActionFromUrlString(urlLink, extras, Channel.PUSH);
            uriAction.execute(context);
        } else {
            if (isActMoveEnabled) {
                UriAction uriAction = ActionFactory.createUriActionFromActMove(intent, true, Channel.PUSH);
                uriAction.execute(context);
                return;
            }

            LoggerUtils.d(TAG, context.getString(R.string.GROOBEE_NOTIFICATION_UTILS_NOTIFICATION_OPENED_INTENT));
            context.startActivity(UriUtils.getMainActivityIntent(context, extras));
        }
    }

    public static void logPushDeliveryEvent(Context context, Bundle pushExtras) {
        if (pushExtras != null) {
            // Get the campaign ID
            String campaignId = pushExtras.getString(Constants.PUSH_CAMPAIGN_ID_KEY);
            if (!StringUtils.isNullOrBlank(campaignId)) {
                Groobee.getInstance().logPushDeliveryEvent(campaignId);
            } else {
                LoggerUtils.d(TAG, context.getString(R.string.GROOBEE_NOTIFICATION_UTILS_LOG_PUSH_DELIVERY_EVENT_CAMPAIGN_ID_IS_NULL, campaignId));
            }
        } else {
            LoggerUtils.d(TAG, context.getString(R.string.GROOBEE_NOTIFICATION_UTILS_LOG_PUSH_DELIVERY_EVENT_EXTRA_IS_NULL));
        }
    }

    public static String getOnCreateNotificationChannelId(Context context, GroobeeConfigProvider groobeeConfigProvider, Bundle notificationExtras) {
        String channelIdFromExtras = getNonBlankStringFromBundle(notificationExtras, Constants.PUSH_NOTIFICATION_CHANNEL_ID_KEY);
        String defaultChannelId = Constants.PUSH_DEFAULT_NOTIFICATION_CHANNEL_ID;

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            // If on Android < O, the channel does not really need to exist
            return channelIdFromExtras != null ? channelIdFromExtras : defaultChannelId;
        }

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        // First try to get the channel from the extras
        if (channelIdFromExtras != null) {
            if (notificationManager.getNotificationChannel(channelIdFromExtras) != null) {
                LoggerUtils.d(TAG, context.getString(R.string.GROOBEE_NOTIFICATION_UTILS_GET_ON_CREATE_NOTIFICATION_CHANNEL_ID, channelIdFromExtras));
                return channelIdFromExtras;
            } else {
                LoggerUtils.d(TAG, context.getString(R.string.GROOBEE_NOTIFICATION_UTILS_GET_ON_CREATE_NOTIFICATION_CHANNEL_ID_IS_NULL, channelIdFromExtras));
            }
        }

        // If we get here, we need to use the default channel
        if (notificationManager.getNotificationChannel(defaultChannelId) == null) {
            // If the default doesn't exist, create it now
            LoggerUtils.d(TAG, context.getString(R.string.GROOBEE_NOTIFICATION_UTILS_GET_ON_CREATE_NOTIFICATION_CHANNEL_ID_DEFAULT_NOT_EXIST));
            NotificationChannel channel = new NotificationChannel(defaultChannelId, groobeeConfigProvider.getDefaultNotificationChannelName(), NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(groobeeConfigProvider.getDefaultNotificationChannelDescription());
            notificationManager.createNotificationChannel(channel);
        }

        return defaultChannelId;
    }

    private static String getNonBlankStringFromBundle(Bundle bundle, String key) {
        if (bundle != null) {
            String stringValue = bundle.getString(key, null);
            if (!StringUtils.isNullOrBlank(stringValue)) {
                return stringValue;
            }
        }
        return null;
    }

    public static void cancelNotification(Context context, int notificationId) {
        try {
            LoggerUtils.d(TAG, context.getString(R.string.GROOBEE_NOTIFICATION_UTILS_CANCEL_NOTIFICATION, String.valueOf(notificationId)));
            Intent cancelNotificationIntent = new Intent(Constants.PUSH_CANCEL_NOTIFICATION_ACTION).setClass(context, GroobeeNotificationUtils.getNotificationReceiverClass());
            cancelNotificationIntent.putExtra(Constants.PUSH_NOTIFICATION_ID, notificationId);
            IntentUtils.sendComponent(context, cancelNotificationIntent);
        } catch (Exception e) {
            LoggerUtils.e(TAG, context.getString(R.string.GROOBEE_NOTIFICATION_UTILS_CANCEL_NOTIFICATION_EXCEPTION), e);
        }
    }

    static void sendNotificationOpenedBroadcast(Context context, Intent intent) {
        LoggerUtils.d(TAG, context.getString(R.string.GROOBEE_NOTIFICATION_UTILS_SEND_NOTIFICATION_OPENED_BROADCAST));
        sendPushActionIntent(context, GroobeeNotificationUtils.PUSH_NOTIFICATION_OPENED_SUFFIX, intent.getExtras());
    }

    private static void logNotificationOpened(Context context, Intent intent) {
        Groobee.getInstance().logPushNotificationOpened(intent);
    }

    public static int getNotificationId(Bundle notificationExtras) {
        if (notificationExtras != null) {
            if (notificationExtras.containsKey(Constants.PUSH_CUSTOM_NOTIFICATION_ID)) {
                try {
                    int notificationId = Integer.parseInt(notificationExtras.getString(Constants.PUSH_CUSTOM_NOTIFICATION_ID));
                    LoggerUtils.d(TAG, "Using notification id provided in the message's extras bundle: " + notificationId);
                    return notificationId;

                } catch (NumberFormatException e) {
                    LoggerUtils.e(TAG, "Unable to parse notification id provided in the "
                            + "message's extras bundle. Using default notification id instead: "
                            + Constants.PUSH_DEFAULT_NOTIFICATION_ID, e);
                    return Constants.PUSH_DEFAULT_NOTIFICATION_ID;
                }
            } else {
                String messageKey = notificationExtras.getString(Constants.PUSH_TITLE_KEY, "")
                        + notificationExtras.getString(Constants.PUSH_CONTENT_KEY, "");
                int notificationId = messageKey.hashCode();
                LoggerUtils.d(TAG, "Message without notification id provided in the extras bundle received. Using a hash of the message: " + notificationId);
                return notificationId;
            }
        } else {
            LoggerUtils.d(TAG, "Message without extras bundle received. Using default notification id: ");
            return Constants.PUSH_DEFAULT_NOTIFICATION_ID;
        }
    }

    public static InterfaceGroobeeNotificationFactory getActiveNotificationFactory() {
        InterfaceGroobeeNotificationFactory customGroobeeNotificationFactory = Groobee.getCustomGroobeeNotificationFactory();
        if (customGroobeeNotificationFactory == null) {
            return GroobeeNotificationFactory.getInstance();
        } else {
            return customGroobeeNotificationFactory;
        }
    }

    /**
     * Sets notification title if it exists in the notificationExtras.
     */
    public static void setContentTitle(NotificationCompat.Builder notificationBuilder, Bundle notificationExtras) {
        if (notificationExtras != null) {
            LoggerUtils.d(TAG, "Setting title for notification");
            String title = notificationExtras.getString(Constants.PUSH_TITLE_KEY);
            notificationBuilder.setContentTitle(title);
        }
    }

    /**
     * Sets notification content if it exists in the notificationExtras.
     */
    public static void setContentText(NotificationCompat.Builder notificationBuilder, Bundle notificationExtras) {
        if (notificationExtras != null) {
            LoggerUtils.d(TAG, "Setting content for notification");
            String content = notificationExtras.getString(Constants.PUSH_CONTENT_KEY);
            notificationBuilder.setContentText(content);
        }
    }

    /**
     * Sets notification ticker to the title if it exists in the notificationExtras.
     */
    public static void setTicker(NotificationCompat.Builder notificationBuilder, Bundle notificationExtras) {
        if (notificationExtras != null) {
            LoggerUtils.d(TAG, "Setting ticker for notification");
            notificationBuilder.setTicker(notificationExtras.getString(Constants.PUSH_TITLE_KEY));
        }
    }

    /**
     * Create broadcast intent that will fire when the notification has been opened. The FCM or ADM receiver will be notified,
     * log a click, then send a broadcast to the client receiver.
     *
     * @param context
     * @param notificationBuilder
     * @param notificationExtras
     */
    public static void setContentIntent(Context context, NotificationCompat.Builder notificationBuilder, Bundle notificationExtras) {
        try {
            PendingIntent pushOpenedPendingIntent = getPushActionPendingIntent(context, Constants.PUSH_CLICKED_ACTION, notificationExtras);
            notificationBuilder.setContentIntent(pushOpenedPendingIntent);
        } catch (Exception e) {
            LoggerUtils.e(TAG, context.getString(R.string.GROOBEE_NOTIFICATION_UTILS_SET_CONTENT_INTENT_EXCEPTION), e);
        }
    }

    public static void setDeleteIntent(Context context, NotificationCompat.Builder notificationBuilder, Bundle notificationExtras) {
        LoggerUtils.d(TAG, context.getString(R.string.GROOBEE_NOTIFICATION_UTILS_SET_DELETE_INTENT));
        try {
            PendingIntent pushDeletedPendingIntent = getPushActionPendingIntent(context, Constants.PUSH_DELETED_ACTION, notificationExtras);
            notificationBuilder.setDeleteIntent(pushDeletedPendingIntent);
        } catch (Exception e) {
            LoggerUtils.e(TAG, context.getString(R.string.GROOBEE_NOTIFICATION_UTILS_SET_DELETE_INTENT_EXCEPTION), e);
        }
    }

    public static int setSmallIcon(GroobeeConfigProvider groobeeConfigProvider, NotificationCompat.Builder notificationBuilder) {
        int smallNotificationIconResourceId = groobeeConfigProvider.getSmallNotificationIconResourceId();
        if (smallNotificationIconResourceId == 0) {
            LoggerUtils.d(TAG, "Small notification icon resource was not found. Will use the app icon when "
                    + "displaying notifications.");
            smallNotificationIconResourceId = groobeeConfigProvider.getApplicationIconResourceId();
        } else {
            LoggerUtils.d(TAG, "Setting small icon for notification via resource id");
        }
        notificationBuilder.setSmallIcon(smallNotificationIconResourceId);
        return smallNotificationIconResourceId;
    }

    public static boolean setLargeIcon(Context context, GroobeeConfigProvider groobeeConfigProvider,
                                                            NotificationCompat.Builder notificationBuilder, Bundle notificationExtras) {
        try {
            if (notificationExtras != null && notificationExtras.containsKey(Constants.PUSH_LARGE_ICON_KEY)) {
                LoggerUtils.d(TAG, context.getString(R.string.GROOBEE_NOTIFICATION_UTILS_SET_LARGE_ICON_URL));
                String bitmapUrl = notificationExtras.getString(Constants.PUSH_LARGE_ICON_KEY);
                Bitmap largeNotificationBitmap = Groobee.getInstance().getImageLoader().getBitmap(bitmapUrl);
                notificationBuilder.setLargeIcon(largeNotificationBitmap);
                return true;
            }
            LoggerUtils.d(TAG, context.getString(R.string.GROOBEE_NOTIFICATION_UTILS_SET_LARGE_ICON_RESOURCE));
            int largeNotificationIconResourceId = groobeeConfigProvider.getLargeNotificationIconResourceId();
            if (largeNotificationIconResourceId != 0) {
                Bitmap largeNotificationBitmap = BitmapFactory.decodeResource(context.getResources(), largeNotificationIconResourceId);
                notificationBuilder.setLargeIcon(largeNotificationBitmap);
                return true;
            } else {
                LoggerUtils.d(TAG, context.getString(R.string.GROOBEE_NOTIFICATION_UTILS_SET_LARGE_ICON_RESOURCE_NOT_FOUND));
            }
        } catch (Exception e) {
            LoggerUtils.e(TAG, context.getString(R.string.GROOBEE_NOTIFICATION_UTILS_SET_LARGE_ICON_EXCEPTION), e);
        }

        LoggerUtils.d(TAG, context.getString(R.string.GROOBEE_NOTIFICATION_UTILS_SET_LARGE_ICON_NO_SETTING));
        return false;
    }

    /**
     * Notifications can optionally include a sound to play when the notification is delivered.
     * <p/>
     * Starting with Android O, sound is set on a notification channel and not individually on notifications.
     */
    public static void setSound(NotificationCompat.Builder notificationBuilder, Bundle notificationExtras) {
        if (notificationExtras != null && notificationExtras.containsKey(Constants.PUSH_SOUND_KEY)) {
            // Retrieve sound uri if included in notificationExtras bundle.
            String soundUri = notificationExtras.getString(Constants.PUSH_SOUND_KEY);
            if (soundUri != null) {
                if (soundUri.equals(Constants.PUSH_SOUND_DEFAULT_VALUE)) {
                    LoggerUtils.d(TAG, "Setting default sound for notification.");
                    notificationBuilder.setDefaults(Notification.DEFAULT_SOUND);
                } else {
                    LoggerUtils.d(TAG, "Setting sound for notification via uri.");
                    notificationBuilder.setSound(Uri.parse(soundUri));
                }
            }
        } else {
            LoggerUtils.d(TAG, "Sound key not present in notification extras. Not setting sound for notification.");
        }
    }

    /**
     * Sets the subText of the notification if a summary is present in the notification extras.
     * <p/>
     * Supported on JellyBean+.
     */
    public static void setSummaryText(NotificationCompat.Builder notificationBuilder, Bundle notificationExtras) {
        if (notificationExtras != null && notificationExtras.containsKey(Constants.PUSH_SUB_TEXT_KEY)) {
            // Retrieve summary text if included in notificationExtras bundle.
            String summaryText = notificationExtras.getString(Constants.PUSH_SUB_TEXT_KEY);
            if (summaryText != null) {
                LoggerUtils.d(TAG, "Setting summary text for notification");
                notificationBuilder.setSubText(summaryText);
            }
        } else {
            LoggerUtils.d(TAG, "Summary text not present in notification extras. Not setting summary text for notification.");
        }
    }

    /**
     * Sets the style of the notification if supported.
     * <p/>
     * If there is an image url found in the extras payload and the image can be downloaded, then
     * use the android BigPictureStyle as the notification. Else, use the BigTextStyle instead.
     * <p/>
     * Supported JellyBean+.
     */
    public static void setStyle(Context context, NotificationCompat.Builder notificationBuilder, Bundle notificationExtras) {
        if (notificationExtras != null) {
            LoggerUtils.d(TAG, context.getString(R.string.GROOBEE_NOTIFICATION_UTILS_SET_STYLE));
            NotificationCompat.Style style = GroobeeNotificationStyleFactory.getBigNotificationStyle(context, notificationExtras, notificationBuilder);
            notificationBuilder.setStyle(style);
        }
    }

    public static void setAccentColor(GroobeeConfigProvider groobeeConfigProvider, NotificationCompat.Builder notificationBuilder, Bundle notificationExtras) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (notificationExtras != null && notificationExtras.containsKey(Constants.PUSH_ACCENT_KEY)) {
                // Color is an unsigned integer, so we first parse it as a long.
                LoggerUtils.d(TAG, "Using accent color for notification from extras bundle");
                notificationBuilder.setColor(Color.parseColor(notificationExtras.getString(Constants.PUSH_ACCENT_KEY)));
            } else {
                LoggerUtils.d(TAG, "Using default accent color for notification");
                notificationBuilder.setColor(groobeeConfigProvider.getDefaultNotificationAccentColor());
            }
        }
    }

    /**
     * Set category for devices on Lollipop and above. Category is one of the predefined notification categories (see the CATEGORY_* constants in Notification)
     * that best describes a Notification. May be used by the system for ranking and filtering.
     * <p/>
     * Supported Lollipop+.
     */
    public static void setCategory(NotificationCompat.Builder notificationBuilder, Bundle notificationExtras) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (notificationExtras != null && notificationExtras.containsKey(Constants.PUSH_CATEGORY_KEY)) {
                LoggerUtils.d(TAG, "Setting category for notification");
                String notificationCategory = notificationExtras.getString(Constants.PUSH_CATEGORY_KEY);
                notificationBuilder.setCategory(notificationCategory);
            } else {
                LoggerUtils.d(TAG, "Category not present in notification extras. Not setting category for notification.");
            }
        } else {
            LoggerUtils.d(TAG, "Notification category not supported on this android version. Not setting category for notification.");
        }
    }

    /**
     * Set visibility for devices on Lollipop and above.
     * <p/>
     * Sphere of visibility of this notification, which affects how and when the SystemUI reveals the notification's presence and
     * contents in untrusted situations (namely, on the secure lockscreen). The default level, VISIBILITY_PRIVATE, behaves exactly
     * as notifications have always done on Android: The notification's icon and tickerText (if available) are shown in all situations,
     * but the contents are only available if the device is unlocked for the appropriate user. A more permissive policy can be expressed
     * by VISIBILITY_PUBLIC; such a notification can be read even in an "insecure" context (that is, above a secure lockscreen).
     * To modify the public version of this notification—for example, to redact some portions—see setPublicVersion(Notification).
     * Finally, a notification can be made VISIBILITY_SECRET, which will suppress its icon and ticker until the user has bypassed the lockscreen.
     * <p/>
     * Supported Lollipop+.
     */
    public static void setVisibility(NotificationCompat.Builder notificationBuilder, Bundle notificationExtras) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (notificationExtras != null && notificationExtras.containsKey(Constants.PUSH_VISIBILITY_KEY)) {
                try {
                    int visibility = Integer.parseInt(notificationExtras.getString(Constants.PUSH_VISIBILITY_KEY));

                    if (isValidNotificationVisibility(visibility)) {
                        LoggerUtils.d(TAG, "Setting visibility for notification");
                        notificationBuilder.setVisibility(visibility);
                    } else {
                        LoggerUtils.e(TAG, "Received invalid notification visibility " + visibility);
                    }
                } catch (Exception e) {
                    LoggerUtils.e(TAG, "Failed to parse visibility from notificationExtras", e);
                }
            }
        } else {
            LoggerUtils.d(TAG, "Notification visibility not supported on this android version. Not setting visibility for notification.");
        }
    }

    /**
     * Sets the notification number, set via {@link NotificationCompat.Builder#setNumber(int)}. On Android O, this number is used with notification badges.
     */
    public static void setNotificationBadgeNumber(NotificationCompat.Builder notificationBuilder, Bundle notificationExtras) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            final String extrasBadgeCount = notificationExtras.getString(Constants.PUSH_NOTIFICATION_BADGE_COUNT_KEY, null);
            if (!StringUtils.isNullOrBlank(extrasBadgeCount)) {
                try {
                    int badgeCount = Integer.parseInt(extrasBadgeCount);
                    notificationBuilder.setNumber(badgeCount);
                } catch (NumberFormatException e) {
                    LoggerUtils.e(TAG, "Caught exception while setting number on notification.", e);
                }
            }
        }
    }

    /**
     * Creates a {@link PendingIntent} using the given action and extras specified.
     *
     * @param context            Application context
     * @param action             The action to set for the {@link PendingIntent}
     * @param notificationExtras The extras to set for the {@link PendingIntent}, if not null
     */
    private static PendingIntent getPushActionPendingIntent(Context context, String action, Bundle notificationExtras) {
        Intent pushActionIntent = new Intent(action).setClass(context, GroobeeNotificationUtils.getNotificationReceiverClass());
        if (notificationExtras != null) {
            pushActionIntent.putExtras(notificationExtras);
        }
        return PendingIntent.getBroadcast(context, IntentUtils.getRequestCode(), pushActionIntent, PendingIntent.FLAG_ONE_SHOT);
    }

    /**
     * @return the Class of the notification receiver used by this application.
     */
    public static Class<?> getNotificationReceiverClass() {
        return GroobeeFirebaseReceiver.class;
    }

    public static boolean isValidNotificationVisibility(int visibility) {
        return (visibility == Notification.VISIBILITY_SECRET || visibility == Notification.VISIBILITY_PRIVATE || visibility == Notification.VISIBILITY_PUBLIC);
    }

    /**
     * Broadcasts an intent with the given action suffix. Will copy the extras from the input intent.
     *
     * @param context            Application context.
     * @param notificationExtras The extras to attach to the intent.
     * @param actionSuffix       The action suffix. Will be appended to the host package name to create the full intent action.
     */
    private static void sendPushActionIntent(Context context, String actionSuffix, Bundle notificationExtras) {
        String pushAction = context.getPackageName() + actionSuffix;
        Intent pushIntent = new Intent(pushAction);
        if (notificationExtras != null) {
            pushIntent.putExtras(notificationExtras);
        }
        IntentUtils.sendComponent(context, pushIntent);
    }
}

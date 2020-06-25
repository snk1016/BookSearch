package com.groobee.message.push.factorys;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.DisplayMetrics;

import androidx.core.app.NotificationCompat;

import com.groobee.message.Groobee;
import com.groobee.message.R;
import com.groobee.message.common.Constants;
import com.groobee.message.utils.DisplayUtils;
import com.groobee.message.utils.LoggerUtils;
import com.groobee.message.utils.StringUtils;

public class GroobeeNotificationStyleFactory {
    private static final String TAG = LoggerUtils.getClassLogTag(GroobeeNotificationStyleFactory.class);

    public static final int BIG_PICTURE_STYLE_IMAGE_HEIGHT = 192;

    /**
     * Returns a big style NotificationCompat.Style. If an image is present, this will be a BigPictureStyle,
     * otherwise it will be a BigTextStyle.
     */
    public static NotificationCompat.Style getBigNotificationStyle(Context context, Bundle notificationExtras, NotificationCompat.Builder notificationBuilder) {
        NotificationCompat.Style style = null;

        if (notificationExtras != null && notificationExtras.containsKey(Constants.PUSH_BIG_IMAGE_URL_KEY)) {
            LoggerUtils.d(TAG, context.getString(R.string.GROOBEE_NOTIFICATION_STYLE_FACTORY_GET_BIG_NOTIFICATION_STYLE_PICTURE));
            style = getBigPictureNotificationStyle(context, notificationExtras);
        }

        if (notificationExtras != null && notificationExtras.containsKey(Constants.PUSH_BIG_LARGE_ICON_KEY) && !notificationExtras.containsKey(Constants.PUSH_BIG_IMAGE_URL_KEY)) {
            LoggerUtils.d(TAG, context.getString(R.string.GROOBEE_NOTIFICATION_STYLE_FACTORY_GET_BIG_NOTIFICATION_STYLE_PICTURE));
            style = getBigLargeIconNotificationStyle(context, notificationExtras);
        }

        // Default style is BigTextStyle.
        if (style == null) {
            LoggerUtils.d(TAG, context.getString(R.string.GROOBEE_NOTIFICATION_STYLE_FACTORY_GET_BIG_NOTIFICATION_STYLE_TEXT));
            style = getBigTextNotificationStyle(notificationExtras);
        }

        return style;
    }

    public static NotificationCompat.BigPictureStyle getBigLargeIconNotificationStyle(Context context, Bundle notificationExtras) {
        NotificationCompat.BigPictureStyle bigPictureNotificationStyle;

        if (notificationExtras == null || !notificationExtras.containsKey(Constants.PUSH_BIG_LARGE_ICON_KEY))
            return null;

        String imageUrl = notificationExtras.getString(Constants.PUSH_BIG_LARGE_ICON_KEY);
        if (StringUtils.isNullOrBlank(imageUrl))
            return null;

        Bitmap imageBitmap = Groobee.getInstance().getImageLoader().getBitmap(imageUrl);

        if (imageBitmap == null) {
            LoggerUtils.d(TAG, context.getString(R.string.GROOBEE_NOTIFICATION_STYLE_FACTORY_GET_BIG_LARGE_ICON_NOTIFICATION_STYLE_FAILED_IMAGE_URL, imageUrl));
            return null;
        }

        try {
            // Images get cropped differently across different screen sizes
            // Here we grab the current screen size and scale the image to fit correctly
            // Note: if the height is greater than the width it's going to look poor, so we might
            // as well let the system modify it and not complicate things by trying to smoosh it here.
            if (imageBitmap.getWidth() > imageBitmap.getHeight()) {
                DisplayMetrics displayMetrics = DisplayUtils.getDefaultScreenDisplayMetrics(context);
                int bigPictureHeightPixels = DisplayUtils.getPixelsFromDensityAndDp(displayMetrics.densityDpi, BIG_PICTURE_STYLE_IMAGE_HEIGHT);
                // 2:1 aspect ratio
                int bigPictureWidthPixels = 2 * bigPictureHeightPixels;
                if (bigPictureWidthPixels > displayMetrics.widthPixels) {
                    bigPictureWidthPixels = displayMetrics.widthPixels;
                }

                try {
                    imageBitmap = Bitmap.createScaledBitmap(imageBitmap, bigPictureWidthPixels, bigPictureHeightPixels, true);
                } catch (Exception e) {
                    LoggerUtils.e(TAG, context.getString(R.string.GROOBEE_NOTIFICATION_STYLE_FACTORY_GET_BIG_LARGE_ICON_NOTIFICATION_STYLE_FAILED_IMAGE_SCALE, e.toString()));
                }
            }
            if (imageBitmap == null) {
                LoggerUtils.i(TAG, context.getString(R.string.GROOBEE_NOTIFICATION_STYLE_FACTORY_GET_BIG_LARGE_ICON_NOTIFICATION_STYLE_FAILED_IMAGE_DOWNLOAD));
                return null;
            }

            bigPictureNotificationStyle = new NotificationCompat.BigPictureStyle();
            bigPictureNotificationStyle.bigLargeIcon(imageBitmap);
            setBigPictureSummaryAndTitle(bigPictureNotificationStyle, notificationExtras);

        } catch (Exception e) {
            LoggerUtils.e(TAG, context.getString(R.string.GROOBEE_NOTIFICATION_STYLE_FACTORY_GET_BIG_LARGE_ICON_NOTIFICATION_STYLE_EXCEPTION, e.toString()));
            bigPictureNotificationStyle = null;
        }

        return bigPictureNotificationStyle;
    }

    /**
     * Returns a BigPictureStyle notification style initialized with the bitmap, big title, and big summary
     * specified in the notificationExtras and extras bundles.
     * <p/>
     * If summary text exists, it will be shown in the expanded notification view.
     * If a title exists, it will override the default in expanded notification view.
     */
    public static NotificationCompat.BigPictureStyle getBigPictureNotificationStyle(Context context, Bundle notificationExtras) {
        NotificationCompat.BigPictureStyle bigPictureNotificationStyle;

        if (notificationExtras == null || !notificationExtras.containsKey(Constants.PUSH_BIG_IMAGE_URL_KEY))
            return null;

        String imageUrl = notificationExtras.getString(Constants.PUSH_BIG_IMAGE_URL_KEY);
        if (StringUtils.isNullOrBlank(imageUrl))
            return null;

        Bitmap imageBitmap = Groobee.getInstance().getImageLoader().getBitmap(imageUrl);

        if (imageBitmap == null) {
            LoggerUtils.d(TAG, context.getString(R.string.GROOBEE_NOTIFICATION_STYLE_FACTORY_GET_BIG_PICTURE_NOTIFICATION_STYLE_FAILED_IMAGE_URL, imageUrl));
            return null;
        }

        try {
            // Images get cropped differently across different screen sizes
            // Here we grab the current screen size and scale the image to fit correctly
            // Note: if the height is greater than the width it's going to look poor, so we might
            // as well let the system modify it and not complicate things by trying to smoosh it here.
            if (imageBitmap.getWidth() > imageBitmap.getHeight()) {
                DisplayMetrics displayMetrics = DisplayUtils.getDefaultScreenDisplayMetrics(context);
                int bigPictureHeightPixels = DisplayUtils.getPixelsFromDensityAndDp(displayMetrics.densityDpi, BIG_PICTURE_STYLE_IMAGE_HEIGHT);
                // 2:1 aspect ratio
                int bigPictureWidthPixels = 2 * bigPictureHeightPixels;
                if (bigPictureWidthPixels > displayMetrics.widthPixels) {
                    bigPictureWidthPixels = displayMetrics.widthPixels;
                }

                try {
                    imageBitmap = Bitmap.createScaledBitmap(imageBitmap, bigPictureWidthPixels, bigPictureHeightPixels, true);
                } catch (Exception e) {
                    LoggerUtils.e(TAG, context.getString(R.string.GROOBEE_NOTIFICATION_STYLE_FACTORY_GET_BIG_PICTURE_NOTIFICATION_STYLE_FAILED_IMAGE_SCALE, e.toString()));
                }
            }
            if (imageBitmap == null) {
                LoggerUtils.i(TAG, context.getString(R.string.GROOBEE_NOTIFICATION_STYLE_FACTORY_GET_BIG_PICTURE_NOTIFICATION_STYLE_FAILED_IMAGE_DOWNLOAD));
                return null;
            }

            bigPictureNotificationStyle = new NotificationCompat.BigPictureStyle();
            bigPictureNotificationStyle.bigPicture(imageBitmap);

            if (notificationExtras.containsKey(Constants.PUSH_BIG_LARGE_ICON_KEY))
                bigPictureNotificationStyle.bigLargeIcon(Groobee.getInstance().getImageLoader().getBitmap(notificationExtras.getString(Constants.PUSH_BIG_LARGE_ICON_KEY)));

            setBigPictureSummaryAndTitle(bigPictureNotificationStyle, notificationExtras);

        } catch (Exception e) {
            LoggerUtils.e(TAG, context.getString(R.string.GROOBEE_NOTIFICATION_STYLE_FACTORY_GET_BIG_PICTURE_NOTIFICATION_STYLE_EXCEPTION, e.toString()));
            bigPictureNotificationStyle = null;
        }

        return bigPictureNotificationStyle;
    }

    static void setBigPictureSummaryAndTitle(NotificationCompat.BigPictureStyle bigPictureNotificationStyle, Bundle notificationExtras) {
        String bigSummary = null;
        String bigTitle = null;

        if (notificationExtras.containsKey(Constants.PUSH_BIG_CONTENT_TEXT_KEY)) {
            bigSummary = notificationExtras.getString(Constants.PUSH_BIG_CONTENT_TEXT_KEY);
        }
        if (notificationExtras.containsKey(Constants.PUSH_BIG_TITLE_TEXT_KEY)) {
            bigTitle = notificationExtras.getString(Constants.PUSH_BIG_TITLE_TEXT_KEY);
        }

        if (bigSummary != null) {
            bigPictureNotificationStyle.setSummaryText(bigSummary);
        }
        if (bigTitle != null) {
            bigPictureNotificationStyle.setBigContentTitle(bigTitle);
        }

        String summaryText = notificationExtras.getString(Constants.PUSH_SUB_TEXT_KEY);
        if (summaryText == null && bigSummary == null) {
            String contentText = notificationExtras.getString(Constants.PUSH_CONTENT_KEY);
            bigPictureNotificationStyle.setSummaryText(contentText);
        }
    }

    /**
     * Returns a BigTextStyle notification style initialized with the content, big title, and big summary
     * specified in the notificationExtras bundles.
     * <p/>
     * If summary text exists, it will be shown in the expanded notification view.
     * If a title exists, it will override the default in expanded notification view.
     */
    public static NotificationCompat.BigTextStyle getBigTextNotificationStyle(Bundle notificationExtras) {
        if (notificationExtras != null) {
            NotificationCompat.BigTextStyle bigTextNotificationStyle = new NotificationCompat.BigTextStyle();
            String pushContent = notificationExtras.getString(Constants.PUSH_CONTENT_KEY);
            bigTextNotificationStyle.bigText(pushContent);

            String bigSummary = null;
            String bigTitle = null;

            if (notificationExtras.containsKey(Constants.PUSH_BIG_CONTENT_TEXT_KEY)) {
                bigSummary = notificationExtras.getString(Constants.PUSH_BIG_CONTENT_TEXT_KEY);
            }
            if (notificationExtras.containsKey(Constants.PUSH_BIG_TITLE_TEXT_KEY)) {
                bigTitle = notificationExtras.getString(Constants.PUSH_BIG_TITLE_TEXT_KEY);
            }
            if (bigSummary != null) {
                bigTextNotificationStyle.setSummaryText(bigSummary);
            }
            if (bigTitle != null) {
                bigTextNotificationStyle.setBigContentTitle(bigTitle);
            }

            return bigTextNotificationStyle;
        } else {
            return null;
        }
    }
}

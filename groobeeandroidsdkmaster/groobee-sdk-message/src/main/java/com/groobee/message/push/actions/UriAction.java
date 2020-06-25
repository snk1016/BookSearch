package com.groobee.message.push.actions;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.browser.customtabs.CustomTabsIntent;

import com.groobee.message.R;
import com.groobee.message.common.Channel;
import com.groobee.message.common.Constants;
import com.groobee.message.providers.GroobeeConfigProvider;
import com.groobee.message.push.utils.UriUtils;
import com.groobee.message.utils.FileUtils;
import com.groobee.message.utils.LoggerUtils;
import com.groobee.message.utils.StringUtils;

import java.util.List;

public class UriAction implements IAction {
    private static final String TAG = LoggerUtils.getClassLogTag(UriAction.class);

    private final Bundle mExtras;
    private final Channel mChannel;
    private Uri mUri;

    private boolean isActMoveEnabled = false;
    private Intent intent;

    /**
     * @param uri     The Uri.
     * @param extras  Any extras to be passed in the start intent.
     * @param channel The channel for the Uri. Must not be null.
     */
    public UriAction(Uri uri, Bundle extras, Channel channel) {
        mUri = uri;
        mExtras = extras;
        mChannel = channel;
    }

    public UriAction(boolean isActMoveEnabled, Intent intent, Channel channel) {
        this.isActMoveEnabled = isActMoveEnabled;
        this.intent = intent;
        mChannel = channel;
        mExtras = intent.getExtras();
    }

    /**
     * Constructor to copy an existing {@link UriAction}.
     *
     * @param originalUriAction A {@link UriAction} to copy parameters from.
     */
    public UriAction(UriAction originalUriAction) {
        this.mUri = originalUriAction.mUri;
        this.mExtras = originalUriAction.mExtras;
        this.mChannel = originalUriAction.mChannel;
    }

    @Override
    public Channel getChannel() {
        return mChannel;
    }

    @Override
    public void execute(Context context) {
        if (!isActMoveEnabled) {
            if (FileUtils.isLocalUri(mUri)) {
                LoggerUtils.d(TAG, context.getString(R.string.URI_ACTION_EXECUTE_SCHEME_IS_FILE, mUri));
                return;
            }

            LoggerUtils.d(TAG, context.getString(R.string.URI_ACTION_EXECUTE, String.valueOf(mChannel), String.valueOf(mUri), String.valueOf(mExtras)));

            String deepLink = mExtras.getString(Constants.PUSH_DEEP_LINK_KEY);
            String urlLink = mExtras.getString(Constants.PUSH_URL_LINK_KEY);

            if (deepLink != null && !deepLink.isEmpty()) {
                if (mChannel.equals(Channel.PUSH))
                    openUriWithActionViewFromPush(context, mUri, mExtras);
            } else if (deepLink == null && urlLink != null && !urlLink.isEmpty()) {
                if (mChannel.equals(Channel.PUSH))
                    openUriWithActionViewFromPushBrowser(context, mUri, mExtras);
            } else
                openUriWithActionView(context, mUri, mExtras);

        } else {
            if (mChannel.equals(Channel.PUSH))
                openActWithActionViewFromPush(context, intent, mExtras);
        }
    }

    public void setUri(Uri uri) {
        mUri = uri;
    }

    /**
     * @return the {@link Uri} that represents this {@link UriAction}.
     */
    public Uri getUri() {
        return mUri;
    }

    public Bundle getExtras() {
        return mExtras;
    }

    /**
     * Uses an Intent.ACTION_VIEW intent to open the Uri.
     */
    protected void openUriWithActionView(Context context, Uri uri, Bundle extras) {
        Intent intent = getActionViewIntent(context, uri, extras);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        if (intent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(intent);
        } else {
            LoggerUtils.w(TAG, context.getString(R.string.URI_ACTION_ACTIVITY_NOT_FOUND, String.valueOf(uri)));
        }
    }

    /**
     * Uses an {@link Intent#ACTION_VIEW} intent to open the {@link Uri} and places the main activity of the
     * activity on the back stack.
     *
     * @see UriAction#getIntentArrayWithConfiguredBackStack(Context, Bundle, Intent, GroobeeConfigProvider)
     */
    protected void openUriWithActionViewFromPush(Context context, Uri uri, Bundle extras) {
        GroobeeConfigProvider configurationProvider = new GroobeeConfigProvider(context);
        try {
            Intent uriIntent = getActionViewIntent(context, uri, extras);
            context.startActivities(getIntentArrayWithConfiguredBackStack(context, extras, uriIntent, configurationProvider));
        } catch (ActivityNotFoundException e) {
            LoggerUtils.w(TAG, context.getString(R.string.URI_ACTION_ACTIVITY_NOT_FOUND, String.valueOf(uri)), e);
        }
    }

    protected void openActWithActionViewFromPush(Context context, Intent intent, Bundle extras) {
        GroobeeConfigProvider configurationProvider = new GroobeeConfigProvider(context);
        try {
            context.startActivities(getIntentArrayWithConfiguredBackStack(context, extras, intent, configurationProvider));
        } catch (ActivityNotFoundException e) {
            LoggerUtils.w(TAG, context.getString(R.string.URI_ACTION_ACTIVITY_NOT_FOUND_2), e);
        }
    }

    protected void openUriWithActionViewFromPushBrowser(Context context, Uri uri, Bundle extras) {
        try {
            CustomTabsIntent customTabsIntent = new CustomTabsIntent.Builder().setShowTitle(true).build();
            customTabsIntent.intent.putExtras(extras);
            customTabsIntent.intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            customTabsIntent.launchUrl(context, uri);
        } catch (ActivityNotFoundException e) {
            LoggerUtils.w(TAG, context.getString(R.string.URI_ACTION_BROWSER_NOT_FOUND, uri), e);
        }
    }

    protected Intent getActionViewIntent(Context context, Uri uri, Bundle extras) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(uri);

        if (extras != null) {
            intent.putExtras(extras);
        }

        // If the current app can already handle the intent, default to using it
        List<ResolveInfo> resolveInfos = context.getPackageManager().queryIntentActivities(intent, 0);
        if (resolveInfos.size() > 1) {
            for (ResolveInfo resolveInfo : resolveInfos) {
                if (resolveInfo.activityInfo.packageName.equals(context.getPackageName())) {
                    LoggerUtils.d(TAG, context.getString(R.string.URI_ACTION_DEEP_LINK, resolveInfo.activityInfo.packageName));
                    intent.setPackage(resolveInfo.activityInfo.packageName);
                    break;
                }
            }
        }

        return intent;
    }

    /**
     * Gets an {@link Intent} array that has the configured back stack functionality.
     *
     * @param targetIntent The ultimate intent to be followed. For example, the main/launcher intent would be the penultimate {@link Intent}.
     * @see GroobeeConfigProvider#getPushMoveActivityEnabled()
     * @see GroobeeConfigProvider#getPushMoveActivityClassName()
     */
    protected Intent[] getIntentArrayWithConfiguredBackStack(Context context,
                                                             Bundle extras,
                                                             Intent targetIntent,
                                                             GroobeeConfigProvider configurationProvider) {
        // The root intent will either point to the launcher activity,
        // some custom activity, or nothing if the back-stack is disabled.
        Intent rootIntent = null;

        String deepLink = mExtras.getString(Constants.PUSH_DEEP_LINK_KEY);
        String urlLink = mExtras.getString(Constants.PUSH_URL_LINK_KEY);

        if (configurationProvider.getPushMoveActivityEnabled()) {
            // If a custom back stack class is defined, then set it
            final String pushDeepLinkBackStackActivityClassName = configurationProvider.getPushMoveActivityClassName();
            if (StringUtils.isNullOrBlank(pushDeepLinkBackStackActivityClassName)) {
                LoggerUtils.i(TAG, context.getString(R.string.URI_ACTION_START_MAIN_ACTIVITY));
                rootIntent = UriUtils.getMainActivityIntent(context, extras);
            } else {
                // Check if the activity is registered in the manifest. If not, then add nothing to the back stack
                if (UriUtils.isActivityRegisteredInManifest(context, pushDeepLinkBackStackActivityClassName)) {

                    if (TextUtils.isEmpty(deepLink) && TextUtils.isEmpty(urlLink)) {
                        LoggerUtils.i(TAG, context.getString(R.string.URI_ACTION_START_CUSTOM_ACTIVITY, pushDeepLinkBackStackActivityClassName));
                        rootIntent = new Intent()
                                .setClassName(context, pushDeepLinkBackStackActivityClassName)
                                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                .putExtras(extras);
                    }
                } else {
                    LoggerUtils.i(TAG, context.getString(R.string.URI_ACTION_UNREGISTERED_ACTIVITY, pushDeepLinkBackStackActivityClassName));
                }
            }
        } else {
            LoggerUtils.i(TAG, context.getString(R.string.URI_ACTION_SETTING_ACTIVITY_MOVE_DISABLE));
        }

        if (rootIntent == null) {
            // Calling startActivities() from outside of an Activity
            // context requires the FLAG_ACTIVITY_NEW_TASK flag on the first Intent
            targetIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            // Just return the target intent by itself
            return new Intent[]{targetIntent};
        } else {
            // Return the intents in their stack order
            return new Intent[]{rootIntent, targetIntent};
        }
    }
}

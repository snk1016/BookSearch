package com.groobee.message.providers;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import com.groobee.message.R;
import com.groobee.message.utils.LoggerUtils;
import com.groobee.message.utils.PackageUtils;

public class GroobeeConfigProvider extends TemporaryConfigProvider {

    private static final String TAG = LoggerUtils.getClassLogTag(GroobeeConfigProvider.class);

    private final Context context;

    public GroobeeConfigProvider(Context context) {
        super(context);
        this.context = context;
    }

    public String getGroobeeApiKey() {
        return this.getStringValue(context.getString(R.string.KEY_VARIABLE_GROOBEE_API_KEY), "");
    }

    public int getSmallNotificationIconResourceId() {
        return this.getNotificationIconResourceId(iconSize.small);
    }

    public int getLargeNotificationIconResourceId() {
        return this.getNotificationIconResourceId(iconSize.large);
    }

    public String getDefaultNotificationChannelName() {
        return this.getStringValue(context.getString(R.string.KEY_VARIABLE_DEFAULT_NOTIFICATION_CHANNEL_NAME), "General");
    }

    public String getDefaultNotificationChannelDescription() {
        return this.getStringValue(context.getString(R.string.KEY_VARIABLE_DEFAULT_NOTIFICATION_CHANNEL_DESCRIPTION), "");
    }

    public boolean getHandlePushDeepLinks() {
        return this.getBooleanValue(context.getString(R.string.KEY_VARIABLE_HANDLE_PUSH_DEEP_LINKS), false);
    }

    public boolean getPushMoveActivityEnabled() {
        return this.getBooleanValue(context.getString(R.string.KEY_VARIABLE_PUSH_MOVE_ACTIVITY_ENABLED), true);
    }

    public String getPushMoveActivityClassName() {
        return this.getStringValue(context.getString(R.string.KEY_VARIABLE_PUSH_MOVE_ACTIVITY_CLASS_NAME), "");
    }

    public int getApplicationIconResourceId() {
        int resourceId = 0;

        String applicationIcon = context.getString(R.string.KEY_VARIABLE_APPLICATION_ICON);

        if (this.mConfigTemp.containsKey(applicationIcon)) {
            resourceId = (Integer)this.mConfigTemp.get(applicationIcon);
        } else {
            String packageName = this.context.getPackageName();

            try {
                ApplicationInfo var3 = this.context.getPackageManager().getApplicationInfo(packageName, 0);
                resourceId = var3.icon;
            } catch (PackageManager.NameNotFoundException var6) {
                LoggerUtils.e(TAG, context.getString(R.string.GROOBEE_CONFIG_PROVIDER_GET_APPLICATION_ICON_RESOURCE_ID_EXCEPTION, packageName));
            }

            this.mConfigTemp.put(applicationIcon, resourceId);
        }

        return resourceId;
    }

    private int getNotificationIconResourceId(GroobeeConfigProvider.iconSize size) {
        String iconType = size.equals(iconSize.large) ? context.getString(R.string.KEY_VARIABLE_PUSH_LARGE_NOTIFICATION_ICON) : context.getString(R.string.KEY_VARIABLE_PUSH_SMALL_NOTIFICATION_ICON);
        int resourceId = 0;
        String defType = context.getString(R.string.DEF_TYPE_DRAWABLE);

        if (mConfigTemp.containsKey(iconType)) {
            resourceId = (Integer)mConfigTemp.get(iconType);
        } else if (runtimeConfigProvider.contains(iconType)) {
            String path = runtimeConfigProvider.getData(iconType, "");

            String[] detail_path = path.split(":");

            if(detail_path.length > 1)
                defType = detail_path[1].substring(0, detail_path[1].indexOf("/"));

            resourceId = context.getResources().getIdentifier(path, defType, PackageUtils.getResourcePackageName(context));
            mConfigTemp.put(iconType, resourceId);
            LoggerUtils.d(TAG, context.getString(R.string.GROOBEE_CONFIG_PROVIDER_GET_NOTIFICATION_ICON_RESOURCE_ID, iconType, path));
        } else {
            resourceId = context.getResources().getIdentifier(iconType, defType, PackageUtils.getResourcePackageName(context));
            mConfigTemp.put(iconType, resourceId);
        }

        return resourceId;
    }

    public int getDefaultNotificationAccentColor() {
        Integer var1 = this.getColorValue(context.getString(R.string.KEY_VARIABLE_DEFAULT_NOTIFICATION_ACCENT_COLOR));
        if (var1 != null) {
            LoggerUtils.d(TAG, context.getString(R.string.GROOBEE_CONFIG_PROVIDER_GET_DEFAULT_NOTIFICATION_ACCENT_COLOR));
            return var1;
        } else {
            return this.getIntValue(context.getString(R.string.KEY_VARIABLE_DEFAULT_NOTIFICATION_ACCENT_COLOR), 0);
        }
    }

    enum iconSize {
        large,
        small
    }
}

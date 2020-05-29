package com.groobee.message.providers;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

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
        return this.getStringValue("groobee_api_key", "");
    }

    public int getSmallNotificationIconResourceId() {
        return this.getNotificationIconResourceId(iconSize.small);
    }

    public int getLargeNotificationIconResourceId() {
        return this.getNotificationIconResourceId(iconSize.large);
    }

    public String getDefaultNotificationChannelName() {
        return this.getStringValue("default_notification_channel_name", "General");
    }

    public String getDefaultNotificationChannelDescription() {
        return this.getStringValue("default_notification_channel_description", "");
    }

    public boolean getHandlePushDeepLinks() {
        return this.getBooleanValue("handle_push_deep_links", false);
    }

    public boolean getPushMoveActivityEnabled() {
        return this.getBooleanValue("push_move_activity_enabled", true);
    }

    public String getPushMoveActivityClassName() {
        return this.getStringValue("push_move_activity_class_name", "");
    }

    public int getApplicationIconResourceId() {
        int resourceId = 0;

        if (this.mConfigTemp.containsKey("application_icon")) {
            resourceId = (Integer)this.mConfigTemp.get("application_icon");
        } else {
            String packageName = this.context.getPackageName();

            try {
                ApplicationInfo var3 = this.context.getPackageManager().getApplicationInfo(packageName, 0);
                resourceId = var3.icon;
            } catch (PackageManager.NameNotFoundException var6) {
                LoggerUtils.e(TAG, "Cannot find package named " + packageName);
            }

            this.mConfigTemp.put("application_icon", resourceId);
        }

        return resourceId;
    }

    private int getNotificationIconResourceId(GroobeeConfigProvider.iconSize size) {
        String iconType = size.equals(iconSize.large) ? "push_large_notification_icon" : "push_small_notification_icon";
        int resourceId = 0;
        if (mConfigTemp.containsKey(iconType)) {
            resourceId = (Integer)mConfigTemp.get(iconType);
        } else if (runtimeConfigProvider.contains(iconType)) {
            String path = runtimeConfigProvider.getData(iconType, "");

            String[] detail_path = path.split(":");
            String defType = "drawable";

            if(detail_path.length > 1)
                defType = detail_path[1].substring(0, detail_path[1].indexOf("/"));

            resourceId = context.getResources().getIdentifier(path, defType, PackageUtils.getResourcePackageName(context));
            mConfigTemp.put(iconType, resourceId);
            LoggerUtils.d(TAG, "Using runtime override value for key: " + iconType + " and value: " + path);
        } else {
            resourceId = context.getResources().getIdentifier(iconType, "drawable", PackageUtils.getResourcePackageName(context));
            mConfigTemp.put(iconType, resourceId);
        }

        return resourceId;
    }

    public int getDefaultNotificationAccentColor() {
        Integer var1 = this.getColorValue("default_notification_accent_color");
        if (var1 != null) {
            LoggerUtils.d(TAG, "Using default notification accent color found in resources");
            return var1;
        } else {
            return this.getIntValue("default_notification_accent_color", 0);
        }
    }

    enum iconSize {
        large,
        small
    }
}

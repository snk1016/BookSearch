package com.groobee.message.providers;

import android.content.Context;
import android.content.SharedPreferences;

import com.groobee.message.GroobeeConfig;
import com.groobee.message.utils.LoggerUtils;

import java.util.Set;

public class RuntimeConfigProvider {
    private static final String TAG = LoggerUtils.getClassLogTag(RuntimeConfigProvider.class);

    public static final String PREFERENCES_NAME = "rebuild_preference";

    private final SharedPreferences sharedPreferences;

    public RuntimeConfigProvider(Context context) {
        this.sharedPreferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
    }

    public void setRuntimeConfig(GroobeeConfig groobeeConfig) {
        LoggerUtils.i(TAG, "Setting configuration with config: " + groobeeConfig);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        addData(editor, "groobee_api_key", groobeeConfig.getApiKey());
        addData(editor, "handle_push_deep_links", groobeeConfig.getHandlePushDeepLinks());
        addData(editor, "push_move_activity_enabled", groobeeConfig.getPushMoveActivityEnabled());
        addData(editor, "push_move_activity_class_name", groobeeConfig.getPushMoveActivityClassName());
        addData(editor, "push_small_notification_icon", groobeeConfig.getSmallNotificationIcon());
        addData(editor, "push_large_notification_icon", groobeeConfig.getLargeNotificationIcon());
        editor.apply();
    }

    public void clean() {
        LoggerUtils.i(TAG, "Clearing configuration cache");
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }

    public String getData(String key, String defaultValue) { return this.sharedPreferences.getString(key, defaultValue); }

    public int getData(String key, int defaultValue) { return this.sharedPreferences.getInt(key, defaultValue); }

    public boolean getData(String key, boolean defaultValue) { return this.sharedPreferences.getBoolean(key, defaultValue); }

    public Set<String> getData(String key, Set<String> defaultValue) { return this.sharedPreferences.getStringSet(key, defaultValue); }

    public boolean contains(String key) { return this.sharedPreferences.contains(key); }

    private static void addData(SharedPreferences.Editor editor, String key, String value) { if (value != null) editor.putString(key, value); }

    private static void addData(SharedPreferences.Editor editor, String key, Integer value) { if (value != null) editor.putInt(key, value); }

    private static void addData(SharedPreferences.Editor editor, String key, Boolean value) { if (value != null) editor.putBoolean(key, value); }

    private static void addData(SharedPreferences.Editor editor, String key, Set<String> value) { if (value != null) editor.putStringSet(key, value); }
}

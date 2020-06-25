package com.groobee.message.providers;

import android.content.Context;
import android.content.SharedPreferences;

import com.groobee.message.GroobeeConfig;
import com.groobee.message.R;
import com.groobee.message.utils.LoggerUtils;

import java.util.Set;

public class RuntimeConfigProvider {
    private static final String TAG = LoggerUtils.getClassLogTag(RuntimeConfigProvider.class);

    public static final String PREFERENCES_NAME = "rebuild_preference";
    public static final String PREFERENCES_PUSH_UUID = "preference_push_uuid";
    public static final String PREFERENCES_SESSION = "preference_session";

    private final SharedPreferences sharedPreferences;

    private Context context;

    public RuntimeConfigProvider(Context context) {
        this.context = context;
        this.sharedPreferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
    }

    public void setRuntimeConfig(GroobeeConfig groobeeConfig) {
        LoggerUtils.i(TAG, context.getString(R.string.RUNTIME_CONFIG_PROVIDER_SET_RUNTIME_CONFIG, String.valueOf(groobeeConfig)));
        SharedPreferences.Editor editor = sharedPreferences.edit();
        addData(editor, context.getString(R.string.KEY_VARIABLE_GROOBEE_API_KEY), groobeeConfig.getApiKey());
        addData(editor, context.getString(R.string.KEY_VARIABLE_HANDLE_PUSH_DEEP_LINKS), groobeeConfig.getHandlePushDeepLinks());
        addData(editor, context.getString(R.string.KEY_VARIABLE_PUSH_MOVE_ACTIVITY_ENABLED), groobeeConfig.getPushMoveActivityEnabled());
        addData(editor, context.getString(R.string.KEY_VARIABLE_PUSH_MOVE_ACTIVITY_CLASS_NAME), groobeeConfig.getPushMoveActivityClassName());
        addData(editor, context.getString(R.string.KEY_VARIABLE_PUSH_SMALL_NOTIFICATION_ICON), groobeeConfig.getSmallNotificationIcon());
        addData(editor, context.getString(R.string.KEY_VARIABLE_PUSH_LARGE_NOTIFICATION_ICON), groobeeConfig.getLargeNotificationIcon());
        editor.apply();
    }

    public void clean() {
        LoggerUtils.i(TAG, context.getString(R.string.RUNTIME_CONFIG_PROVIDER_CLEAN));
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }

    public String getData(String key, String defaultValue) { return this.sharedPreferences.getString(key, defaultValue); }

    public int getData(String key, int defaultValue) { return this.sharedPreferences.getInt(key, defaultValue); }

    public boolean getData(String key, boolean defaultValue) { return this.sharedPreferences.getBoolean(key, defaultValue); }

    public Set<String> getData(String key, Set<String> defaultValue) { return this.sharedPreferences.getStringSet(key, defaultValue); }

    public boolean contains(String key) { return this.sharedPreferences.contains(key); }

    public static void addData(SharedPreferences.Editor editor, String key, String value) { if (value != null) editor.putString(key, value); }

    public static void addData(SharedPreferences.Editor editor, String key, Integer value) { if (value != null) editor.putInt(key, value); }

    public static void addData(SharedPreferences.Editor editor, String key, Boolean value) { if (value != null) editor.putBoolean(key, value); }

    public static void addData(SharedPreferences.Editor editor, String key, Set<String> value) { if (value != null) editor.putStringSet(key, value); }
}

package com.groobee.message.providers;

import android.content.Context;

import androidx.annotation.ColorInt;
import androidx.annotation.Nullable;

import com.groobee.message.utils.LoggerUtils;
import com.groobee.message.utils.PackageUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class TemporaryConfigProvider {
    private static final String TAG = LoggerUtils.getClassLogTag(TemporaryConfigProvider.class);

    private final Context context;

    protected final Map<String, Object> mConfigTemp;

    protected final RuntimeConfigProvider runtimeConfigProvider;

    public TemporaryConfigProvider(Context context) {
        this.context = context;
        this.mConfigTemp = Collections.synchronizedMap(new HashMap<String, Object>());
        this.runtimeConfigProvider = new RuntimeConfigProvider(context);
    }

    protected String getStringValue(String key, String defaultValue) {
        String result;

        if(mConfigTemp.containsKey(key))
            result = (String)mConfigTemp.get(key);
        else {
            if(runtimeConfigProvider.contains(key)) {
                result = runtimeConfigProvider.getData(key, defaultValue);
                mConfigTemp.put(key, result);
                LoggerUtils.d(TAG, "Using runtime override value for key: " + key + " and value: " + result);
            } else {
                result = readStringResourceValue(key, defaultValue);
                mConfigTemp.put(key, result);
                LoggerUtils.d(TAG, "Defaulting to using xml value for key: " + key + " and value: " + result);
            }
        }

        return result;
    }

    protected int getIntValue(String key, int defaultValue) {
        int result;

        if(mConfigTemp.containsKey(key))
            result = (Integer)mConfigTemp.get(key);
        else {
            if(this.runtimeConfigProvider.contains(key)) {
                result = runtimeConfigProvider.getData(key, defaultValue);
                mConfigTemp.put(key, result);
                LoggerUtils.d(TAG, "Using runtime override value for key: " + key + " and value: " + result);
            } else {
                result = this.readIntegerResourceValue(key, defaultValue);
                this.mConfigTemp.put(key, result);
                LoggerUtils.d(TAG, "Defaulting to using xml value for key: " + key + " and value: " + result);
            }
        }

        return result;
    }

    protected boolean getBooleanValue(String key, boolean defaultValue) {
        boolean result;

        if(mConfigTemp.containsKey(key))
            result = (Boolean)mConfigTemp.get(key);
        else {
            if(this.runtimeConfigProvider.contains(key)) {
                result = runtimeConfigProvider.getData(key, defaultValue);
                mConfigTemp.put(key, result);
                LoggerUtils.d(TAG, "Using runtime override value for key: " + key + " and value: " + result);
            } else {
                result = readBooleanResourceValue(key, defaultValue);
                mConfigTemp.put(key, result);
                LoggerUtils.d(TAG, "Defaulting to using xml value for key: " + key + " and value: " + result);
            }
        }

        return result;
    }

    protected Set<String> getStringSetValue(String key, Set<String> defaultValue) {
        Set<String> result;

        if(this.mConfigTemp.containsKey(key))
            result = (Set<String>)mConfigTemp.get(key);
        else {
            if(this.runtimeConfigProvider.contains(key)) {
                result = runtimeConfigProvider.getData(key, defaultValue);
                mConfigTemp.put(key, result);
                LoggerUtils.d(TAG, "Using runtime override value for key: " + key + " and value: " + result);
            } else {
                String[] array = readStringArrayResourceValue(key, new String[0]);

                if (array.length == 0)
                    result = defaultValue;
                else
                    result = new HashSet<>(Arrays.asList(array));
                mConfigTemp.put(key, result);
                LoggerUtils.d(TAG, "Defaulting to using xml value for key: " + key + " and value: " + result);
            }
        }

        return result;
    }

    @ColorInt
    @Nullable
    protected Integer getColorValue(String key) {
        Integer result;
        if (mConfigTemp.containsKey(key)) {
            result = (Integer)mConfigTemp.get(key);
        } else if (runtimeConfigProvider.contains(key)) {
            result = runtimeConfigProvider.getData(key, 0);
            mConfigTemp.put(key, result);
            LoggerUtils.d(TAG, "Using runtime override value for key: " + key + " and value: " + result);
        } else {
            result = readColorResourceValue(key);
            if (result != null) {
                mConfigTemp.put(key, result);
                LoggerUtils.d(TAG, "Defaulting to using xml value for key: " + key + " and value: " + result);
            }
        }

        return result;
    }

    protected int readIntegerResourceValue(String key, int defaultValue) {
        try {
            if (key == null) {
                return defaultValue;
            } else {
                int identifier = context.getResources().getIdentifier(key, "integer", PackageUtils.getResourcePackageName(context));
                if (identifier == 0) {
                    LoggerUtils.d(TAG, "Unable to find the xml integer configuration value with key " + key + ". Using default value '" + defaultValue + "'.");
                    return defaultValue;
                } else {
                    return context.getResources().getInteger(identifier);
                }
            }
        } catch (Exception e) {
            LoggerUtils.d(TAG, "Unexpected exception retrieving the xml integer configuration value with key " + key + ". Using default value " + defaultValue + "'.");
            return defaultValue;
        }
    }

    @ColorInt
    protected Integer readColorResourceValue(String key) {
        try {
            if (key == null) {
                return null;
            } else {
                int identifier = context.getResources().getIdentifier(key, "color", PackageUtils.getResourcePackageName(context));
                if (identifier == 0) {
                    LoggerUtils.d(TAG, "Unable to find the xml color configuration value with key " + key);
                    return null;
                } else {
                    return context.getResources().getColor(identifier);
                }
            }
        } catch (Exception e) {
            LoggerUtils.e(TAG, "Unexpected exception retrieving the xml color configuration value with key " + key + ".", e);
            return null;
        }
    }

    protected boolean readBooleanResourceValue(String key, boolean defaultValue) {
        try {
            if (key == null) {
                return defaultValue;
            } else {
                int identifier = context.getResources().getIdentifier(key, "bool", PackageUtils.getResourcePackageName(context));
                if (identifier == 0) {
                    LoggerUtils.d(TAG, "Unable to find the xml boolean configuration value with key " + key + ". Using default value '" + defaultValue + "'.");
                    return defaultValue;
                } else {
                    return context.getResources().getBoolean(identifier);
                }
            }
        } catch (Exception e) {
            LoggerUtils.d(TAG, "Unexpected exception retrieving the xml boolean configuration value with key " + key + ". Using default value " + defaultValue + "'.");
            return defaultValue;
        }
    }

    protected String readStringResourceValue(String key, String defaultValue) {
        try {
            if (key == null) {
                return defaultValue;
            } else {
                int identifier = context.getResources().getIdentifier(key, "string", PackageUtils.getResourcePackageName(context));
                if (identifier == 0) {
                    LoggerUtils.d(TAG, "Unable to find the xml string configuration value with key " + key + ". Using default value '" + defaultValue + "'.");
                    return defaultValue;
                } else {
                    return context.getResources().getString(identifier);
                }
            }
        } catch (Exception e) {
            LoggerUtils.d(TAG, "Unexpected exception retrieving the xml string configuration value with key " + key + ". Using default value " + defaultValue + "'.");
            return defaultValue;
        }
    }

    protected String[] readStringArrayResourceValue(String key, String[] defaultValue) {
        try {
            if (key == null) {
                return defaultValue;
            } else {
                int identifier = context.getResources().getIdentifier(key, "array", PackageUtils.getResourcePackageName(context));
                if (identifier == 0) {
                    LoggerUtils.d(TAG, "Unable to find the xml string array configuration value with key " + key + ". Using default value '" + Arrays.toString(defaultValue) + "'.");
                    return defaultValue;
                } else {
                    return context.getResources().getStringArray(identifier);
                }
            }
        } catch (Exception e) {
            LoggerUtils.d(TAG, "Unexpected exception retrieving the xml string array configuration value with key " + key + ". Using default value " + Arrays.toString(defaultValue) + "'.");
            return defaultValue;
        }
    }
}

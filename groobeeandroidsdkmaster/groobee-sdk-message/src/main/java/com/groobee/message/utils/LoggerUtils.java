package com.groobee.message.utils;

import android.util.Log;

public class LoggerUtils {

    private static final String beforeTagName = "Groobee-";
    private static final int maxTagLength = 80 - beforeTagName.length();

    private static int LOG_LEVEL = Log.INFO;

    public LoggerUtils() {}

    public static void setLogLevel(int logLevel) {
        LOG_LEVEL = logLevel;
    }

    public static int getLogLevel() { return LOG_LEVEL; }

    public static String getClassLogTag(Class classForTag) {
//        String className = classForTag.getName();
        String className = classForTag.getName().replace(classForTag.getPackage().getName() + ".", "");
        int tagLength = className.length();

        String tagName;

        if (tagLength <= maxTagLength)
            tagName = beforeTagName + className;
        else
            tagName = beforeTagName + className.substring(maxTagLength);

        return tagName;
    }

    public static int v (String tagName, String msg) { return getLogLevel() <= Log.VERBOSE ? Log.v(tagName, msg) : 0; }
    public static int v (String tagName, String msg, Throwable tr) { return getLogLevel() <= Log.VERBOSE ? Log.v(tagName, msg, tr) : 0; }

    public static int d (String tagName, String msg) { return getLogLevel() <= Log.DEBUG ? Log.d(tagName, msg) : 0; }
    public static int d (String tagName, String msg, Throwable tr) { return getLogLevel() <= Log.DEBUG ? Log.d(tagName, msg, tr) : 0; }

    public static int i (String tagName, String msg) { return getLogLevel() <= Log.INFO ? Log.i(tagName, msg) : 0; }
    public static int i (String tagName, String msg, Throwable tr) { return getLogLevel() <= Log.INFO ? Log.i(tagName, msg, tr) : 0; }

    public static int w (String tagName, String msg) { return getLogLevel() <= Log.WARN ? Log.w(tagName, msg) : 0; }
    public static int w (String tagName, String msg, Throwable tr) { return getLogLevel() <= Log.WARN ? Log.w(tagName, msg, tr) : 0; }

    public static int e (String tagName, String msg) { return getLogLevel() <= Log.ERROR ? Log.e(tagName, msg) : 0; }
    public static int e (String tagName, String msg, Throwable tr) { return getLogLevel() <= Log.ERROR ? Log.e(tagName, msg, tr) : 0; }
}

package com.groobee.message.utils;

public class StringUtils {
    public static boolean isNullOrEmpty(String reference) {
        return reference == null || reference.length() == 0;
    }

    public static boolean isNullOrBlank(String reference) {
        return reference == null || reference.trim().length() == 0;
    }
}

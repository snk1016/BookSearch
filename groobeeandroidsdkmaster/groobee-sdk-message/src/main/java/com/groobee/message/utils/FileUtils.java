package com.groobee.message.utils;

import android.net.Uri;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class FileUtils {

    private static final String TAG = LoggerUtils.getClassLogTag(FileUtils.class);

    public static final List<String> REMOTE_SCHEMES = Collections.unmodifiableList(Arrays.asList("http", "https", "ftp", "ftps", "about", "javascript"));

    public FileUtils() {}

    public static boolean isLocalUri(Uri uri) {
        if (uri == null) {
            LoggerUtils.i(TAG, "Null Uri received.");
            return false;
        } else {
            String var1 = uri.getScheme();
            return StringUtils.isNullOrBlank(var1) || var1.equals("file");
        }
    }
}

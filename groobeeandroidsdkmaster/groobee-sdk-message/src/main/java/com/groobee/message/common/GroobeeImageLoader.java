package com.groobee.message.common;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.util.LruCache;

import com.groobee.message.utils.LoggerUtils;
import com.groobee.message.utils.StringUtils;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class GroobeeImageLoader {
    private static final String TAG = LoggerUtils.getClassLogTag(GroobeeImageLoader.class);

    private final int maxMemory;

    private final int cacheSize;

    private GroobeeLruCache memoryCache;

    public static final List<String> REMOTE_SCHEMES = Collections.unmodifiableList(Arrays.asList("http", "https", "ftp", "ftps", "about", "javascript"));

    public GroobeeImageLoader(Context context) {
        maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        cacheSize = maxMemory / 8;

        memoryCache= new GroobeeLruCache(maxMemory);
    }

    private void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null) {
            memoryCache.put(key, bitmap);
        }
    }

    private Bitmap getBitmapFromMemCache(String key) {
        return memoryCache.get(key);
    }

    private boolean isRemoteUri(String url) {
        Uri uri = Uri.parse(url);
        boolean result;

        if (uri == null) {
            LoggerUtils.i(TAG, "Null Uri received.");
            result = false;
        } else {
            String scheme = uri.getScheme();
            if (StringUtils.isNullOrBlank(scheme)) {
                LoggerUtils.i(TAG, "Null or blank Uri scheme.");
                result = false;
            } else {
                result = REMOTE_SCHEMES.contains(scheme);
            }
        }

        return result;
    }

    private boolean isLocalUri(String url) {
        Uri uri = Uri.parse(url);
        boolean result;

        if (uri == null) {
            LoggerUtils.i(TAG, "Null Uri received.");
            result = false;
        } else {
            String scheme = uri.getScheme();
            result = StringUtils.isNullOrBlank(scheme) || scheme.equals("file");
        }

        return result;
    }

    public Bitmap getBitmap(String url) {
        Bitmap bitmap = null;

        try {
            if (url == null)
                LoggerUtils.i(TAG, "Null Uri received. Not getting image.");
            else if (isLocalUri(url)) {
                Bitmap local_bitmap = getLocalBitmapImage(url);
                if(local_bitmap != null)
                    addBitmapToMemoryCache(url, local_bitmap);
                bitmap = getBitmapFromMemCache(url);
            } else if (isRemoteUri(url))
                bitmap = new ImageTask().execute(url).get();
            else
                LoggerUtils.w(TAG, "Uri with unknown scheme received. Not getting image.");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return bitmap;
    }

    private static Bitmap getLocalBitmapImage(String path) {
        try {
            Uri uri = Uri.parse(path);
            File file = new File(uri.getPath());
            if (file.exists()) {
                LoggerUtils.i(TAG, "Retrieving image from path: " + file.getAbsolutePath());
                return BitmapFactory.decodeFile(file.getAbsolutePath());
            }
        } catch (OutOfMemoryError e) {
            LoggerUtils.e(TAG, "Out of Memory Error in local bitmap file retrieval for Path: " + path + ".", e);
        } catch (Exception e) {
            LoggerUtils.e(TAG, "Exception occurred when attempting to retrieve local bitmap.", e);
        } catch (Throwable t) {
            LoggerUtils.e(TAG, "Throwable caught in local bitmap file retrieval for Path: " + path, t);
        }

        return null;
    }

    private class GroobeeLruCache extends LruCache<String, Bitmap> {
        public GroobeeLruCache(int maxSize) {
            super(maxSize);
        }

        @Override
        protected int sizeOf(String key, Bitmap value) {
            return value.getByteCount() / 1024;
        }
    }

    private class ImageTask extends AsyncTask<String, Void, Bitmap> {
        @Override
        protected Bitmap doInBackground(String... params) {
            String url = params[0];
            try {
                Bitmap bitmap = HttpLoader.getBitmapImage(url);
                if(bitmap != null) {
                    addBitmapToMemoryCache(url, bitmap);
                    return getBitmapFromMemCache(url);
                }
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
            return null;
        }
    }
}

package com.groobee.message.common;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpLoader {
    public static Bitmap getBitmapImage(String url) {
        final Bitmap[] bitmap = new Bitmap[1];

        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
                    conn.setDoInput(true);
                    conn.connect();

                    InputStream is = conn.getInputStream();
                    bitmap[0] = BitmapFactory.decodeStream(is);
                } catch (Exception e) {
                    e.printStackTrace();
                    bitmap[0] = null;
                }
            }
        };
        thread.start();

        try { thread.join(); }
        catch (InterruptedException e) { e.printStackTrace(); }

        return bitmap[0];
    }
}

package com.groobee.message.utils;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ThreadPoolUtils {
    private static final int processSize = Runtime.getRuntime().availableProcessors();
    private static final int size;

    static {
        size = Math.max(getCorePoolSize(), processSize - 1);
    }

    public static int getCorePoolSize() {
        return 2;
    }

    public static int getMaxSize() {
        return size;
    }

    public static long getKeepAliveTime() {
        return 1L;
    }

    public static int getProcessSize() {
        return processSize;
    }

    public static BlockingQueue<Runnable> getQueue() {
        return new LinkedBlockingQueue<>();
    }
}

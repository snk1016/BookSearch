package com.groobee.message.common.thread;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class WorkingThreadFactory implements ThreadFactory {

    private final String workingThreadName;

    private Thread.UncaughtExceptionHandler uncaughtExceptionHandler;

    private final AtomicInteger atomicInteger = new AtomicInteger();

    public WorkingThreadFactory() {
        workingThreadName = WorkingThreadFactory.class.getSimpleName();
    }

    public WorkingThreadFactory(String workingThreadName) {
        this.workingThreadName = workingThreadName;
    }

    public void setUncaughtExceptionHandler(Thread.UncaughtExceptionHandler uncaughtExceptionHandler) {
        this.uncaughtExceptionHandler = uncaughtExceptionHandler;
    }

    @Override
    public Thread newThread(Runnable r) {
        if(uncaughtExceptionHandler == null) {
            throw new IllegalStateException("NullPointerException UncaughtExceptionHandler. You must call setUncaughtExceptionHandler before creating a new thread");
        } else {
            Thread thread = new Thread(r, workingThreadName + " #" + atomicInteger.getAndIncrement());
            thread.setUncaughtExceptionHandler(uncaughtExceptionHandler);
            return thread;
        }
    }
}

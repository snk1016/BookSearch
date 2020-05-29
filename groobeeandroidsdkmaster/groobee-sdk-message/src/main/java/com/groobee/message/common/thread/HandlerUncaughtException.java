package com.groobee.message.common.thread;

import androidx.annotation.NonNull;

import com.groobee.message.utils.LoggerUtils;

public class HandlerUncaughtException implements Thread.UncaughtExceptionHandler {
    private static final String TAG = LoggerUtils.getClassLogTag(HandlerUncaughtException.class);

    private InterfaceGenericT interfaceGenericT;

    public HandlerUncaughtException() {
    }

    public HandlerUncaughtException(InterfaceGenericT interfaceGenericT) {
        this.interfaceGenericT = interfaceGenericT;
    }

    @Override
    public void uncaughtException(@NonNull Thread t, @NonNull Throwable e) {
        try {
            if(interfaceGenericT != null) {
                LoggerUtils.w(TAG, "Uncaught exception from thread.", e);
                interfaceGenericT.GenericT(e, Throwable.class);
            }
        } catch (Exception ex) {
            LoggerUtils.w(TAG, "Failed to log throwable.", ex);
        }
    }
}

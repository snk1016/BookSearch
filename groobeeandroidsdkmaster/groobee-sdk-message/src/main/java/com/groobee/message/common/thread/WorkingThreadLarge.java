package com.groobee.message.common.thread;

import com.groobee.message.utils.ThreadPoolUtils;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

public class WorkingThreadLarge extends WorkingThreadPool {
    public WorkingThreadLarge(String id, ThreadFactory threadFactory) {
        super(id, ThreadPoolUtils.getCorePoolSize(), ThreadPoolUtils.getMaxSize(), ThreadPoolUtils.getKeepAliveTime(), TimeUnit.SECONDS, ThreadPoolUtils.getQueue(), threadFactory);
    }
}

package com.groobee.message.common.thread;

import com.groobee.message.utils.ThreadPoolUtils;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

public class WorkingThreadSmall extends WorkingThreadPool {
    public WorkingThreadSmall(String id, ThreadFactory threadFactory) {
        super(id, 1, 1, 0L, TimeUnit.MILLISECONDS, ThreadPoolUtils.getQueue(), threadFactory);
    }
}

package com.groobee.message.common.thread;

import com.groobee.message.utils.LoggerUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public abstract class WorkingThreadPool extends ThreadPoolExecutor {
    private static final String TAG = LoggerUtils.getClassLogTag(WorkingThreadPool.class);

    private List<Runnable> copyOnWriteArrayList = new CopyOnWriteArrayList<>();
    private Map<Runnable, Thread> map = new HashMap<>();

    private String id;

    public WorkingThreadPool(String id, int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory);
        this.id = id;
        this.setRejectedExecutionHandler(new HandlerRejectExecution());
    }

    @Override
    protected void beforeExecute(Thread t, Runnable r) {
        copyOnWriteArrayList.add(r);
        map.put(r, t);
        super.beforeExecute(t, r);
    }

    @Override
    protected void afterExecute(Runnable r, Throwable t) {
        copyOnWriteArrayList.remove(r);
        map.remove(r);
        super.afterExecute(r, t);
    }

    private String getWorkingState() {
        try {
            if(getActiveCount() != copyOnWriteArrayList.size()) {
                LoggerUtils.d(TAG, "Running task count does not match ThreadPoolExecutor active count. \nrunningTasks.size(): " + copyOnWriteArrayList.size() + " getActiveCount(): " + getActiveCount() + " ID: " + id);
                return null;
            } else {
                StringBuilder builder = new StringBuilder();
                builder.append("Running Tasks Size ");
                builder.append(copyOnWriteArrayList.size());
                builder.append(" Active thread dumps => \n");

                Iterator<Thread> it = map.values().iterator();

                while(it.hasNext()) {
                    Thread thread = it.next();

                    try {
                        StackTraceElement[] stackTraceElements = thread.getStackTrace();
                        builder.append(parseStackTrace(stackTraceElements) + "\n");
                    } catch (Exception ex) {
                        LoggerUtils.e(TAG, "Failed to create description for active thread: " + thread + " ID: " + id, ex);
                    }
                }

                return builder.toString();
            }
        } catch (Exception e) {
            LoggerUtils.e(TAG, "Failed to create running tasks description. ID: " + id, e);
            return null;
        }
    }

    private String parseStackTrace(StackTraceElement[] stackTraceElements) {
        if (stackTraceElements.length == 0) {
            return "";
        } else {
            StringBuilder builder = new StringBuilder();
            int size = stackTraceElements.length;

            for(int i = 0; i < size; i++) {
                builder.append("\n => ");
                builder.append(stackTraceElements[i]);
            }

            return builder.toString();
        }
    }

    class HandlerRejectExecution implements RejectedExecutionHandler {

        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
            LoggerUtils.d(TAG, "Rejected execution on runnable: " + r + " . ID: " + id);

            if (!executor.isShutdown() && !executor.isTerminating()) {
                String workingState = getWorkingState();

                try {
                    Runnable runnable;
                    if (!copyOnWriteArrayList.isEmpty()) {
                        runnable = copyOnWriteArrayList.get(0);
                        if (runnable instanceof Future) {
                            ((Future)runnable).cancel(true);
                        } else {
                            Thread var5 = map.get(runnable);
                            if (var5 != null) {
                                var5.interrupt();
                            }
                        }

                        copyOnWriteArrayList.remove(runnable);
                        map.remove(runnable);
                    }

                    runnable = executor.getQueue().poll();
                    if (runnable != null) {
                        LoggerUtils.v(TAG, "Running head of queue on caller thread: " + runnable + " . ID: " + id);
                        ExecutorService var7 = Executors.newSingleThreadExecutor();
                        var7.invokeAll(Collections.singletonList(Executors.callable(runnable)), 200L, TimeUnit.MILLISECONDS);
                    }

                    LoggerUtils.v(TAG, "Re-adding rejected task to queue: " + r + " . ID: " + id);
                    executor.execute(r);
                } catch (Exception var6) {
                    LoggerUtils.d(TAG, "Caught exception in rejected execution handler for incoming task: " + r + " . Running tasks description: " + workingState, var6);
                }

                if (workingState != null) {
                    LoggerUtils.w(TAG, "Handled rejected execution on incoming task: " + workingState);
                }

            } else {
                LoggerUtils.i(TAG, "ThreadPoolExecutor is shutdown. Dropping rejected task. ID: " + id);
            }
        }
    }
}

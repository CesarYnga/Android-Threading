package com.cesarynga.threading.threadpool;


import android.os.Handler;

import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class MyThreadPool implements Executor {

    private static final int POOL_SIZE = 3;
    private static final int MAX_POOL_SIZE = 3;
    private static final int TIMEOUT = 30;

    private final ThreadPoolExecutor threadPoolExecutor;

    // Handler that post task on the UI thread
    private final Handler uiHandler = new Handler();

    private final AtomicBoolean cancelled = new AtomicBoolean();

    public MyThreadPool() {
        this.threadPoolExecutor = new ThreadPoolExecutor(
                POOL_SIZE,
                MAX_POOL_SIZE,
                TIMEOUT,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>());
    }

    @Override
    public void execute(Runnable command) {
        threadPoolExecutor.execute(command);
    }

    public void shutdown() {
        threadPoolExecutor.shutdownNow();
    }

    public void cancel(boolean mayInterruptIfRunning) {
        this.cancelled.set(mayInterruptIfRunning);
    }

    public void notifyTaskComplete(Runnable runnable) {
        if (!cancelled.get()) {
            uiHandler.post(runnable);
        }
    }
}

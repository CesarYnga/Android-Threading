package com.cesarynga.threading;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import java.util.concurrent.TimeUnit;

// Service that performs its job in a worker thread
public class MyIntentService extends IntentService {

    public static final String ACTION_TASK_STARTED = "action_task_started";
    public static final String ACTION_TASK_FINISHED = "action_task_finished";

    private static final int TASK_DURATION_IN_SECONDS = 5;

    public MyIntentService() {
        super("MyIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Intent startedIntent = new Intent(ACTION_TASK_STARTED);
        LocalBroadcastManager.getInstance(this).sendBroadcast(startedIntent);

        try {
            TimeUnit.SECONDS.sleep(TASK_DURATION_IN_SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Intent finishedIntent = new Intent(ACTION_TASK_FINISHED);
        LocalBroadcastManager.getInstance(this).sendBroadcast(finishedIntent);
    }
}

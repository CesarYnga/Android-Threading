package com.cesarynga.threading.intentservice;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.util.concurrent.TimeUnit;

// Service that performs its job in a worker thread
public class MyIntentService extends IntentService {

    private static final String TAG = "MyIntentService";

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

        Log.d(TAG, "Task started");
        try {
            TimeUnit.SECONDS.sleep(TASK_DURATION_IN_SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "Task finished");

        Intent finishedIntent = new Intent(ACTION_TASK_FINISHED);
        LocalBroadcastManager.getInstance(this).sendBroadcast(finishedIntent);
    }
}

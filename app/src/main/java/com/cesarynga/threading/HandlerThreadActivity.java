package com.cesarynga.threading;

import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class HandlerThreadActivity extends AppCompatActivity {

    private static final int TASK_DURATION_IN_SECONDS = 5;

    @BindView(R.id.btn_start_task)
    Button btnStartTask;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    // Handler that post task on the UI thread
    private final Handler uiHandler = new Handler();

    private final MyWorkerThread myWorkerThread = new MyWorkerThread("myWorkerThread");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_async_task);
        ButterKnife.bind(this);

        myWorkerThread.start();
        myWorkerThread.prepareHandler();
    }

    @OnClick(R.id.btn_start_task)
    public void onClick() {
        myWorkerThread.postTask(new BackgroundTask(this));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        myWorkerThread.quit();
    }

    // HandlerThread that handles operations in background
    // Declare as static in order to prevent inner class to keep a reference to the activity
    private static class MyWorkerThread extends HandlerThread {

        // Handler that post task on the worker thread
        private Handler workerHandler;

        MyWorkerThread(String name) {
            super(name);
        }

        void postTask(Runnable task) {
            workerHandler.post(task);
        }

        void prepareHandler() {
            workerHandler = new Handler(getLooper());
        }
    }

    // Operation to be executed in a background thread
    // Declare as static in order to prevent inner class to keep a reference to the activity
    private static class BackgroundTask implements Runnable {

        private WeakReference<HandlerThreadActivity> activityRef;

        // Pass the activity in the constructor and set it in a WeakReference,
        // so the reference to the context will be eligible for GC
        public BackgroundTask(HandlerThreadActivity activity) {
            this.activityRef = new WeakReference<>(activity);
        }

        @Override
        public void run() {
            HandlerThreadActivity activity = activityRef.get();
            activity.uiHandler.post(new OnStartTask(activity));
            try {
                TimeUnit.SECONDS.sleep(TASK_DURATION_IN_SECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            activity.uiHandler.post(new OnFinishTask(activity));
        }
    }

    // Task to be executed when background task starts
    // Declare as static in order to prevent inner class to keep a reference to the activity
    private static class OnStartTask implements Runnable {

        private WeakReference<HandlerThreadActivity> activityRef;

        // Pass the activity in the constructor and set it in a WeakReference,
        // so the reference to the context will be eligible for GC
        public OnStartTask(HandlerThreadActivity activity) {
            this.activityRef = new WeakReference<>(activity);
        }

        @Override
        public void run() {
            HandlerThreadActivity activity = activityRef.get();
            activity.btnStartTask.setEnabled(false);
            activity.btnStartTask.setText(R.string.text_in_progress);
            activity.progressBar.setVisibility(View.VISIBLE);
        }
    }

    // Task to be executed when background task finishes
    // Declare as static in order to prevent inner class to keep a reference to the activity
    private static class OnFinishTask implements Runnable {

        private WeakReference<HandlerThreadActivity> activityRef;

        // Pass the activity in the constructor and set it in a WeakReference,
        // so the reference to the context will be eligible for GC
        public OnFinishTask(HandlerThreadActivity activity) {
            this.activityRef = new WeakReference<>(activity);
        }

        @Override
        public void run() {
            HandlerThreadActivity activity = activityRef.get();
            activity.btnStartTask.setEnabled(true);
            activity.btnStartTask.setText(R.string.text_start);
            activity.progressBar.setVisibility(View.GONE);
            Toast.makeText(activity, R.string.text_finished, Toast.LENGTH_SHORT).show();
        }
    }

}

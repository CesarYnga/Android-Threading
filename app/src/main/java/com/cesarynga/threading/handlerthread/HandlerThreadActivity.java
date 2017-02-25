package com.cesarynga.threading.handlerthread;

import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.cesarynga.threading.R;

import java.lang.ref.WeakReference;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class HandlerThreadActivity extends AppCompatActivity {

    private static final String TAG = "HandlerThreadActivity";

    private static final int TASK_DURATION_IN_SECONDS = 7;

    private static final int TASK_INIT = 0;
    private static final int TASK_COMPLETE = 1;

    @BindView(R.id.btn_start_task)
    Button btnStartTask;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    private UiHandler uiHandler;

    private final MyWorkerThread myWorkerThread = new MyWorkerThread("myWorkerThread");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_async_task);
        ButterKnife.bind(this);

        myWorkerThread.start();
        myWorkerThread.prepareHandler();

        uiHandler = new UiHandler(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        myWorkerThread.cancel(false);
    }

    @Override
    protected void onStop() {
        super.onStop();
        myWorkerThread.cancel(true);
    }

    @OnClick(R.id.btn_start_task)
    public void onClick() {
        myWorkerThread.postTask(new Runnable() {
            @Override
            public void run() {
                uiHandler.sendEmptyMessage(TASK_INIT);

                Log.d(TAG, "Task started");
                try {
                    TimeUnit.SECONDS.sleep(TASK_DURATION_IN_SECONDS);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Log.d(TAG, "Task finished");

                if (!myWorkerThread.isCancelled()) {
                    Log.d(TAG, "Update UI");
                    uiHandler.sendEmptyMessage(TASK_COMPLETE);
                }
            }
        });
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

        private final AtomicBoolean cancelled = new AtomicBoolean();

        MyWorkerThread(String name) {
            super(name);
        }

        void postTask(Runnable task) {
            workerHandler.post(task);
        }

        void prepareHandler() {
            workerHandler = new Handler(getLooper());
        }

        public void cancel(boolean mayInterruptIfRunning) {
            this.cancelled.set(mayInterruptIfRunning);
        }

        public boolean isCancelled() {
            return this.cancelled.get();
        }
    }

    // Handler that post task on the UI thread
    private static class UiHandler extends Handler {

        private final WeakReference<HandlerThreadActivity> activityRef;

        public UiHandler(HandlerThreadActivity activity) {
            activityRef = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case TASK_INIT:
                    activityRef.get().btnStartTask.setEnabled(false);
                    activityRef.get().btnStartTask.setText(R.string.text_in_progress);
                    activityRef.get().progressBar.setVisibility(View.VISIBLE);
                    break;
                case TASK_COMPLETE:
                    activityRef.get().btnStartTask.setEnabled(true);
                    activityRef.get().btnStartTask.setText(R.string.text_start);
                    activityRef.get().progressBar.setVisibility(View.GONE);
                    Toast.makeText(activityRef.get(), R.string.text_finished, Toast.LENGTH_SHORT).show();
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }
}

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

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class HandlerThreadActivity extends AppCompatActivity {

    private static final String TAG = "HandlerThreadActivity";

    private static final int TASK_DURATION_IN_SECONDS = 5;

    private static final int TASK_INIT = 0;
    private static final int TASK_COMPLETE = 1;

    @BindView(R.id.btn_start_task)
    Button btnStartTask;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    // Handler that post task on the UI thread
    private Handler uiHandler;

    private final MyWorkerThread myWorkerThread = new MyWorkerThread("myWorkerThread");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_async_task);
        ButterKnife.bind(this);

        myWorkerThread.start();
        myWorkerThread.prepareHandler();

        uiHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case TASK_INIT:
                        btnStartTask.setEnabled(false);
                        btnStartTask.setText(R.string.text_in_progress);
                        progressBar.setVisibility(View.VISIBLE);
                        break;
                    case TASK_COMPLETE:
                        btnStartTask.setEnabled(true);
                        btnStartTask.setText(R.string.text_start);
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(HandlerThreadActivity.this, R.string.text_finished, Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        super.handleMessage(msg);
                }
            }
        };
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

        private boolean cancelled = false;

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
            this.cancelled = mayInterruptIfRunning;
        }

        public boolean isCancelled() {
            return this.cancelled;
        }
    }
}

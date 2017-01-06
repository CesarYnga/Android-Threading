package com.cesarynga.threading;

import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

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
        myWorkerThread.postTask(task);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        myWorkerThread.quit();
    }

    private final Runnable task = new Runnable() {
        @Override
        public void run() {
            uiHandler.post(onStartTask);
            try {
                TimeUnit.SECONDS.sleep(TASK_DURATION_IN_SECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            uiHandler.post(onFinishedTask);
        }
    };

    private static class MyWorkerThread extends HandlerThread {

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

    private final Runnable onStartTask = new Runnable() {
        @Override
        public void run() {
            btnStartTask.setEnabled(false);
            btnStartTask.setText(R.string.text_in_progress);
            progressBar.setVisibility(View.VISIBLE);
        }
    };

    private final Runnable onFinishedTask = new Runnable() {
        @Override
        public void run() {
            btnStartTask.setEnabled(true);
            btnStartTask.setText(R.string.text_start);
            progressBar.setVisibility(View.GONE);
            Toast.makeText(HandlerThreadActivity.this, R.string.text_finished, Toast.LENGTH_SHORT).show();
        }
    };
}

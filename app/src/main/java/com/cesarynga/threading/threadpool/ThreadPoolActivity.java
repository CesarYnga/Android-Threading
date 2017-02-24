package com.cesarynga.threading.threadpool;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.cesarynga.threading.R;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class ThreadPoolActivity extends AppCompatActivity {

    private static final String TAG = "ThreadPoolActivity";

    private static final int TASK_1_DURATION_IN_SECONDS = 5;
    private static final int TASK_2_DURATION_IN_SECONDS = 3;
    private static final int TASK_3_DURATION_IN_SECONDS = 7;

    private static final int POOL_SIZE = 3;
    private static final int MAX_POOL_SIZE = 3;
    private static final int TIMEOUT = 30;

    @BindView(R.id.btn_start_task_1)
    Button btnStartTask1;
    @BindView(R.id.btn_start_task_2)
    Button btnStartTask2;
    @BindView(R.id.btn_start_task_3)
    Button btnStartTask3;
    @BindView(R.id.progress_bar_1)
    ProgressBar progressBar1;
    @BindView(R.id.progress_bar_2)
    ProgressBar progressBar2;
    @BindView(R.id.progress_bar_3)
    ProgressBar progressBar3;

    private ThreadPoolExecutor threadPoolExecutor;

    // Handler that post task on the UI thread
    private final Handler uiHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thread_pool);
        ButterKnife.bind(this);

        this.threadPoolExecutor = new ThreadPoolExecutor(
                POOL_SIZE,
                MAX_POOL_SIZE,
                TIMEOUT,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>());
    }

    @OnClick(R.id.btn_start_task_1)
    public void onClick1() {
        showProgress(progressBar1);
        threadPoolExecutor.execute(new Runnable() {
            @Override
            public void run() {
                executeDummyTask(TASK_1_DURATION_IN_SECONDS);
                uiHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Log.d(TAG, "Update UI 1");
                        showToast("Task 1 finished");
                        hideProgress(progressBar1);
                    }
                });
            }
        });
    }

    @OnClick(R.id.btn_start_task_2)
    public void onClick2() {
        showProgress(progressBar2);
        threadPoolExecutor.execute(new Runnable() {
            @Override
            public void run() {
                executeDummyTask(TASK_2_DURATION_IN_SECONDS);
                uiHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Log.d(TAG, "Update UI 2");
                        showToast("Task 2 finished");
                        hideProgress(progressBar2);
                    }
                });
            }
        });
    }

    @OnClick(R.id.btn_start_task_3)
    public void onClick3() {
        showProgress(progressBar3);
        threadPoolExecutor.execute(new Runnable() {
            @Override
            public void run() {
                executeDummyTask(TASK_3_DURATION_IN_SECONDS);
                uiHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Log.d(TAG, "Update UI 3");
                        showToast("Task 3 finished");
                        hideProgress(progressBar3);
                    }
                });
            }
        });
    }

    private void executeDummyTask(long timeout) {
        try {
            TimeUnit.SECONDS.sleep(timeout);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void showProgress(ProgressBar progressBar) {
        progressBar.setVisibility(View.VISIBLE);
    }

    private void hideProgress(ProgressBar progressBar) {
        progressBar.setVisibility(View.GONE);
    }
    
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}

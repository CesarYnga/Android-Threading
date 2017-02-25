package com.cesarynga.threading.threadpool;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.cesarynga.threading.R;
import com.cesarynga.threading.handlerthread.HandlerThreadActivity;

import java.lang.ref.WeakReference;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class ThreadPoolActivity extends AppCompatActivity {

    private static final String TAG = "ThreadPoolActivity";

    private static final int TASK_1_DURATION_IN_SECONDS = 5;
    private static final int TASK_2_DURATION_IN_SECONDS = 3;
    private static final int TASK_3_DURATION_IN_SECONDS = 7;


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

    private MyThreadPool myThreadPool;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thread_pool);
        ButterKnife.bind(this);

        myThreadPool = new MyThreadPool();
    }

    @Override
    protected void onStart() {
        super.onStart();
        myThreadPool.cancel(false);
    }

    @Override
    protected void onStop() {
        super.onStop();
        myThreadPool.cancel(true);
    }

    @OnClick(R.id.btn_start_task_1)
    public void onClick1() {
        showProgress(progressBar1);
        myThreadPool.execute(new MyTask(this, MyTask.TASK_TYPE_1));
    }

    @OnClick(R.id.btn_start_task_2)
    public void onClick2() {
        showProgress(progressBar2);
        myThreadPool.execute(new MyTask(this, MyTask.TASK_TYPE_2));
    }

    @OnClick(R.id.btn_start_task_3)
    public void onClick3() {
        showProgress(progressBar3);
        myThreadPool.execute(new MyTask(this, MyTask.TASK_TYPE_3));
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

    // Task to be executed on the thread pool
    private static class MyTask implements Runnable {

        public static final int TASK_TYPE_1 = 1;
        public static final int TASK_TYPE_2 = 2;
        public static final int TASK_TYPE_3 = 3;

        private final WeakReference<ThreadPoolActivity> activityRef;
        private final int taskType;

        public MyTask(ThreadPoolActivity activity, int taskType) {
            this.activityRef = new WeakReference<>(activity);
            this.taskType = taskType;
        }

        @Override
        public void run() {
            int duration;
            switch (taskType) {
                case TASK_TYPE_1:
                    duration = TASK_1_DURATION_IN_SECONDS;
                    break;
                case TASK_TYPE_2:
                    duration = TASK_2_DURATION_IN_SECONDS;
                    break;
                case TASK_TYPE_3:
                    duration = TASK_3_DURATION_IN_SECONDS;
                    break;
                default:
                    duration = TASK_1_DURATION_IN_SECONDS;
            }
            activityRef.get().executeDummyTask(duration);
            activityRef.get().myThreadPool.notifyTaskComplete(new Runnable() {
                @Override
                public void run() {
                    switch (taskType) {
                        case TASK_TYPE_1:
                            Log.d(TAG, "Update UI 1");
                            activityRef.get().showToast("Task 1 finished");
                            activityRef.get().hideProgress(activityRef.get().progressBar1);
                            break;
                        case TASK_TYPE_2:
                            Log.d(TAG, "Update UI 2");
                            activityRef.get().showToast("Task 2 finished");
                            activityRef.get().hideProgress(activityRef.get().progressBar2);
                            break;
                        case TASK_TYPE_3:
                            Log.d(TAG, "Update UI 3");
                            activityRef.get().showToast("Task 3 finished");
                            activityRef.get().hideProgress(activityRef.get().progressBar3);
                            break;
                    }
                }
            });
        }
    }
}

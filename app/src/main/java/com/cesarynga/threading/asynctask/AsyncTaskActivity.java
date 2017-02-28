package com.cesarynga.threading.asynctask;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.cesarynga.threading.R;

import java.lang.ref.WeakReference;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AsyncTaskActivity extends AppCompatActivity {

    private static final String TAG = "AsyncTaskActivity";

    private static final int TASK_DURATION_IN_SECONDS = 7;

    @BindView(R.id.btn_start_task)
    Button btnStartTask;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    private MyTask myTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_async_task);
        ButterKnife.bind(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (myTask != null) {
            myTask.cancel(true);
        }
    }

    @OnClick(R.id.btn_start_task)
    public void onClick() {
        // Need to execute a new AsyncTask each time
        myTask = new MyTask(this);
        myTask.execute();
    }


    // Task to be executed in background
    // Declare as static in order to prevent inner class to keep a reference to the activity
    private static class MyTask extends AsyncTask<Void, Void, Void> {

        private final WeakReference<AsyncTaskActivity> activityRef;

        // Pass the activity in the constructor and set it in a WeakReference,
        // so the reference to the context will be eligible for GC
        private MyTask(AsyncTaskActivity activity) {
            activityRef = new WeakReference<>(activity);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            activityRef.get().onTaskStart();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            Log.d(TAG, "Task started");
            try {
                TimeUnit.SECONDS.sleep(TASK_DURATION_IN_SECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Log.d(TAG, "Task finished");
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (!isCancelled()) {
                Log.d(TAG, "Update UI");
                activityRef.get().onTaskFinished();
            }
        }
    }

    private void onTaskStart() {
        btnStartTask.setEnabled(false);
        btnStartTask.setText(R.string.text_in_progress);
        progressBar.setVisibility(View.VISIBLE);
    }

    private void onTaskFinished() {
        btnStartTask.setEnabled(true);
        btnStartTask.setText(R.string.text_start);
        progressBar.setVisibility(View.GONE);
        Toast.makeText(this, R.string.text_finished, Toast.LENGTH_SHORT).show();
    }
}

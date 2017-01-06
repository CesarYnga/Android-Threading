package com.cesarynga.threading;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.lang.ref.WeakReference;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AsyncTaskActivity extends AppCompatActivity {

    private static final int TASK_DELAY = 5000;

    @BindView(R.id.btn_start_task)
    Button btnStartTask;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_async_task);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.btn_start_task)
    public void onClick() {
        new MyTask(this).execute();
    }

    private static class MyTask extends AsyncTask<Void, Void, Void> {

        private final WeakReference<AsyncTaskActivity> activityRef;

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
            try {
                Thread.sleep(TASK_DELAY);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            activityRef.get().onTaskFinished();
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

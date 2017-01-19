package com.cesarynga.threading;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.lang.ref.WeakReference;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class IntentServiceActivity extends AppCompatActivity {

    @BindView(R.id.btn_start_task)
    Button btnStartTask;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    private MyReceiver myReceiver;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_async_task);
        ButterKnife.bind(this);
        myReceiver = new MyReceiver(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter filter = new IntentFilter();
        filter.addAction(MyIntentService.ACTION_TASK_STARTED);
        filter.addAction(MyIntentService.ACTION_TASK_FINISHED);
        LocalBroadcastManager.getInstance(this).registerReceiver(myReceiver, filter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(myReceiver);
    }

    @OnClick(R.id.btn_start_task)
    public void onClick() {
        Intent intent = new Intent(this, MyIntentService.class);
        startService(intent);
    }

    private static class MyReceiver extends BroadcastReceiver {

        private final WeakReference<IntentServiceActivity> activityRef;

        public MyReceiver(IntentServiceActivity activity) {
            activityRef = new WeakReference<>(activity);
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (TextUtils.equals(action, MyIntentService.ACTION_TASK_STARTED)) {
                activityRef.get().onTaskStart();
            } else if (TextUtils.equals(action, MyIntentService.ACTION_TASK_FINISHED)) {
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

package com.cesarynga.threading;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import butterknife.ButterKnife;
import butterknife.OnItemClick;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @OnItemClick(R.id.list_view)
    public void onOptionClick(int position) {
        switch (position) {
            case 0:
                startActivity(AsyncTaskActivity.class);
                break;
            case 1:
                startActivity(HandlerThreadActivity.class);
        }
    }

    private void startActivity(Class activity) {
        Intent intent = new Intent(getApplicationContext(), activity);
        startActivity(intent);
    }
}

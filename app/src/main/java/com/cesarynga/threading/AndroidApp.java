package com.cesarynga.threading;

import android.app.Application;

import com.squareup.leakcanary.LeakCanary;

/**
 * Created by cynga on 23/02/2017.
 */

public class AndroidApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        if (LeakCanary.isInAnalyzerProcess(this)) {
            return;
        }
        LeakCanary.install(this);
    }
}

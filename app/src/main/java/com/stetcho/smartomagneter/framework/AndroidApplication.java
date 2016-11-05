package com.stetcho.smartomagneter.framework;

import android.app.Application;

import com.squareup.leakcanary.LeakCanary;
import com.stetcho.smartomagneter.BuildConfig;

public class AndroidApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        if (BuildConfig.DEBUG) {
            LeakCanary.install(this);
        }
    }
}
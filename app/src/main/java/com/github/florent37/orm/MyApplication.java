package com.github.florent37.orm;

import android.app.Application;

import fr.xebia.android.freezer.Freezer;

/**
 * Created by florentchampigny on 07/01/2016.
 */
public class MyApplication extends Application {

    @Override public void onCreate() {
        super.onCreate();
        Freezer.onCreate(this);
    }

    @Override public void onTerminate() {
        super.onTerminate();
        Freezer.onDestroy();
    }
}
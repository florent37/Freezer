package com.github.florent37.dao;

import android.app.Application;

import com.github.florent37.orm.ORM;

/**
 * Created by florentchampigny on 07/01/2016.
 */
public class MyApplication extends Application {

    @Override public void onCreate() {
        super.onCreate();
        ORM.onCreate(this);
    }

    @Override public void onTerminate() {
        super.onTerminate();
        ORM.onDestroy();
    }
}
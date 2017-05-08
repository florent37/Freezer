package com.github.florent37.rxandroidorm.sample;

import android.app.Application;

import com.facebook.stetho.Stetho;

import com.github.florent37.rxandroidorm.RxAndroidOrm;

/**
 * Created by florentchampigny on 07/01/2016.
 */
public class MyApplication extends Application {

    @Override public void onCreate() {
        super.onCreate();
        RxAndroidOrm.onCreate(this);

        Stetho.initializeWithDefaults(this);
    }

}
package com.github.florent37.orm;

import android.app.Application;

import com.facebook.stetho.Stetho;

import fr.xebia.android.freezer.Freezer;

/**
 * Created by florentchampigny on 07/01/2016.
 */
public class MyApplication extends Application {

    @Override public void onCreate() {
        super.onCreate();
        Freezer.onCreate(this);

        Stetho.initializeWithDefaults(this);
    }

}
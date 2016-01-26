package com.github.florent37.orm;

import android.app.Application;

import com.xebia.android.orm.AndroidORM;

/**
 * Created by florentchampigny on 07/01/2016.
 */
public class MyApplication extends Application {

   @Override public void onCreate() {
       super.onCreate();
       AndroidORM.onCreate(this);
   }

   @Override public void onTerminate() {
       super.onTerminate();
       AndroidORM.onDestroy();
   }
}
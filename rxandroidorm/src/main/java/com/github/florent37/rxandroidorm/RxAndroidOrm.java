package com.github.florent37.rxandroidorm;

import android.app.Application;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.lang.reflect.Constructor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;

/**
 * Created by florentchampigny on 18/05/2016.
 */
public final class RxAndroidOrm {
    
    private static final String TAG = "RxAndroidOrm";
    private static RxAndroidOrm INSTANCE;

    private SQLiteDatabase database;

    private SQLiteOpenHelper helper;
    private AtomicInteger usages = new AtomicInteger(0);

    private RxAndroidOrm() {
    }

    public static RxAndroidOrm getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new RxAndroidOrm();
        }
        return INSTANCE;
    }

    public static RxAndroidOrm onDestroy() {
        RxAndroidOrm rxAndroidOrm = getInstance();
        rxAndroidOrm.close();
        return rxAndroidOrm;
    }

    public static RxAndroidOrm onCreate(Application application) {
        RxAndroidOrm rxAndroidOrm = getInstance();
        rxAndroidOrm.helper = rxAndroidOrm.findDatabaseHelper(application);
        return rxAndroidOrm;
    }

    public SQLiteDatabase getDatabase() {
        return database;
    }

    public RxAndroidOrm open() throws SQLException {
        if (helper != null) {
            database = helper.getWritableDatabase();
        }
        return this;
    }

    public RxAndroidOrm close() {
        if (helper != null) {
            helper.close();
        }
        return this;
    }

    private SQLiteOpenHelper findDatabaseHelper(Application application) {
        final String className = "com.github.florent37.rxandroidorm.DatabaseHelper";
        try {
            Class<?> clazz = Class.forName(className);
            Constructor<?> constructor = clazz.getConstructor(Context.class);
            return (SQLiteOpenHelper) constructor.newInstance(application);
        } catch (Exception e) {
            Log.e(TAG, "cannot construct RxAndroidOrm", e);
        }
        return null;
    }

    public Observable<SQLiteDatabase> database() {
        return Observable
                .create(new ObservableOnSubscribe<SQLiteDatabase>() {
                    @Override
                    public void subscribe(ObservableEmitter<SQLiteDatabase> e) throws Exception {
                        usages.incrementAndGet();
                        SQLiteDatabase database = RxAndroidOrm.getInstance().open().getDatabase();

                        e.onNext(database);
                        e.onComplete();
                    }
                })
                .doOnTerminate(new Action() {
                    @Override
                    public void run() throws Exception {
                        Observable.timer(3, TimeUnit.SECONDS)
                                .subscribe(new Consumer<Long>() {
                                    @Override
                                    public void accept(@NonNull Long aLong) throws Exception {
                                        final int count = usages.decrementAndGet();
                                        if(count == 0) {
                                            RxAndroidOrm.getInstance().close();
                                        }
                                    }
                                });
                    }
                });
    }

}
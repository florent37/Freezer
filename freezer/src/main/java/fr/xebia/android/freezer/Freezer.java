package fr.xebia.android.freezer;

import android.app.Application;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.lang.reflect.Constructor;

/**
 * Created by florentchampigny on 18/05/2016.
 */
public final class Freezer {
    private static final String TAG = "Freezer";
    private static Freezer INSTANCE;

    private SQLiteDatabase database;

    private SQLiteOpenHelper helper;

    private Freezer() {
    }

    public static Freezer getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Freezer();
        }
        return INSTANCE;
    }

    public static Freezer onDestroy() {
        Freezer freezer = getInstance();
        freezer.close();
        return freezer;
    }

    public static Freezer onCreate(Application application) {
        Freezer freezer = getInstance();
        freezer.helper = freezer.findDatabaseHelper(application);
        return freezer;
    }

    public SQLiteDatabase getDatabase() {
        return database;
    }

    public Freezer open() throws SQLException {
        if (helper != null) {
            database = helper.getWritableDatabase();
        }
        return this;
    }

    public Freezer close() {
        if (helper != null) {
            helper.close();
        }
        return this;
    }

    private SQLiteOpenHelper findDatabaseHelper(Application application) {
        final String className = "fr.xebia.android.freezer.DatabaseHelper";
        try {
            Class<?> clazz = Class.forName(className);
            Constructor<?> constructor = clazz.getConstructor(Context.class);
            return (SQLiteOpenHelper) constructor.newInstance(application);
        } catch (Exception e) {
            Log.e(TAG, "cannot construct Freezer", e);
        }
        return null;
    }
}
package com.github.florent37.dao;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;

/**
 * Created by florentchampigny on 18/01/2016.
 */
public class Constants {
    public static final String DAO_PACKAGE = "com.github.florent37.dao";

    public static final String DAO_CLASS_NAME = "DAO";
    public static final String DATABASE_HELPER_CLASS_NAME = "DatabaseHelper";

    public static final String DAO_SUFFIX = "DAO";
    public static final String CURSOR_HELPER_SUFFIX = "CursorHelper";
    public static final String QUERY_BUILDER_SUFFIX = "DAOQueryBuilder";

    public static final TypeName daoClassName = ClassName.get(Constants.DAO_PACKAGE, "DAO");
    public static final TypeName dbHelperClassName = ClassName.get(Constants.DAO_PACKAGE, "DatabaseHelper");

    public static final TypeName applicationClassName = ClassName.get("android.app", "Application");
    public static final TypeName databaseClassName = ClassName.get("android.database.sqlite", "SQLiteDatabase");
    public static final TypeName sqliteOpenHelperClassName = ClassName.get("android.database.sqlite", "SQLiteOpenHelper");
    public static final TypeName contextClassName = ClassName.get("android.content", "Context");
    public static final TypeName cursorClassName = ClassName.get("android.database", "Cursor");
    public static final TypeName contentValuesClassName = ClassName.get("android.content", "ContentValues");

    public static final String FIELD_ID = "_id";
    public static final String FIELD_NAME = "_field_name";
}

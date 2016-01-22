package com.github.florent37.dao;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;

/**
 * Created by florentchampigny on 18/01/2016.
 */
public class Constants {
    public static final String DAO_PACKAGE = "com.github.florent37.orm";

    public static final String DAO_CLASS_NAME = "ORM";
    public static final String DATABASE_HELPER_CLASS_NAME = "DatabaseHelper";

    public static final String DAO_SUFFIX = "ORM";
    public static final String CURSOR_HELPER_SUFFIX = "CursorHelper";
    public static final String QUERY_BUILDER_SUFFIX = "QueryBuilder";
    public static final String ENUM_COLUMN_SUFFIX = "Columns";

    public static final TypeName daoClassName = ClassName.get(Constants.DAO_PACKAGE, DAO_SUFFIX);
    public static final TypeName dbHelperClassName = ClassName.get(Constants.DAO_PACKAGE, DATABASE_HELPER_CLASS_NAME);

    public static final TypeName applicationClassName = ClassName.get("android.app", "Application");
    public static final TypeName databaseClassName = ClassName.get("android.database.sqlite", "SQLiteDatabase");
    public static final TypeName sqliteOpenHelperClassName = ClassName.get("android.database.sqlite", "SQLiteOpenHelper");
    public static final TypeName contextClassName = ClassName.get("android.content", "Context");
    public static final TypeName cursorClassName = ClassName.get("android.database", "Cursor");
    public static final TypeName contentValuesClassName = ClassName.get("android.content", "ContentValues");

    public static final String ENUM_COLUMN_ELEMENT_NAME = "column_name";

    public static final String FIELD_ID = "_id";
    public static final String FIELD_NAME = "_field_name";
    public static final String QUERY_TABLE_VARIABLE = "t";
    public static final String QUERY_NAMED = "NAMED";
}

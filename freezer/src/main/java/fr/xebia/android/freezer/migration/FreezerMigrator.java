package fr.xebia.android.freezer.migration;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by florentchampigny on 28/01/2016.
 */
public class FreezerMigrator {

    SQLiteDatabase database;

    public FreezerMigrator(SQLiteDatabase database) {
        this.database = database;
    }

    public TableCreator createObject(String objectName) {
        return new TableCreator(database,objectName);
    }

    protected static String getTableName(String objectName) {
        return objectName.toUpperCase();
    }

    public TableTransformer update(String objectName) {
        return new TableTransformer(database,objectName);
    }

    @Deprecated
    public FreezerMigrator addTable(TableCreator tableCreator) {
        //TODO
        return this;
    }

    public static class TableTransformer {
        SQLiteDatabase database;
        String objectName;

        public TableTransformer(SQLiteDatabase database, String objectName) {
            this.database = database;
            this.objectName = objectName;
        }

        public ColumnTransformer transform(String column) {
            return new ColumnTransformer(database,this, objectName, column);
        }

        public TableTransformer name(String newName) {
            database.execSQL(String.format("ALTER TABLE %s RENAME TO %s", getTableName(objectName), getTableName(newName)));
            return this;
        }

        public TableTransformer removeField(String column) {
            database.execSQL(String.format("ALTER TABLE %s REMOVE COLUMN %s", getTableName(objectName), column));
            return this;
        }

        @Deprecated
        public TableTransformer addField(String column, ColumnType type) {
            //primitive
            if (type.isPrimitive()) {
                database.execSQL(String.format("ALTER TABLE %s ADD COLUMN %s %s", getTableName(objectName), column, type.getSqlName()));
            }

            //else TODO
            return this;
        }

        public void apply() {
            //TODO
        }

    }

    public static class ColumnTransformer {
        SQLiteDatabase database;
        TableTransformer tableTransformer;
        String columnName;
        String objectName;

        public ColumnTransformer(SQLiteDatabase database, TableTransformer tableTransformer, String columnName, String objectName) {
            this.database = database;
            this.tableTransformer = tableTransformer;
            this.columnName = columnName;
            this.objectName = objectName;
        }

        public TableTransformer name(String newName) {
            //primitive
            database.execSQL(String.format("ALTER TABLE %s RENAME COLUMN %s TO %s", getTableName(objectName), columnName, newName));

            //else TODO
            return tableTransformer;
        }

        @Deprecated
        public TableTransformer type(ColumnType fromType, ColumnType newType) {
            //primitive
            //{

            //    //TODO
            //    //rename the column to old_column
            //    String tmp_column = columnName + "_tmp";
            //    database.execSQL(String.format("ALTER TABLE %s RENAME COLUMN %s TO %s", getTableName(objectName), columnName, tmp_column));
            //    //create the new column
            //    database.execSQL(String.format("ALTER TABLE %s ADD COLUMN %s %s", getTableName(objectName), columnName, newType));

            //    //get the old values
            //    //transform the values
            //    //insert them into newType

            //    Cursor cursor = db.rawQuery("SELECT _id, .${old_column} FROM ${tableName}", null);
            //    while (!cursor.isAfterLast()) {
            //        long id = cursor.getLong(0);
            //        String newValue = ""; //TRANSFORM(cursor.get**(1));

            //        database.execSQL(String.format("UPDATE %s SET %s = '%s' WHERE _id = %d", getTableName(objectName), newValue, id));

            //        cursor.moveToNext();
            //    }
            //    cursor.close();

            //}
            return tableTransformer;
        }
    }

    public static class TableCreator {
        SQLiteDatabase database;
        String objectName;

        public TableCreator(SQLiteDatabase database, String objectName) {
            this.database = database;
            this.objectName = objectName;
        }

        public TableCreator field(String name, ColumnType type) {
            //TODO
            return this;
        }
    }
}

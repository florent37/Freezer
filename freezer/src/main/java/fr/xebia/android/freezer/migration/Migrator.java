package fr.xebia.android.freezer.migration;

import android.database.sqlite.SQLiteDatabase;

/**
 * Created by florentchampigny on 28/01/2016.
 */
public class Migrator {

    DatabaseHelper databaseHelper;

    public Migrator(SQLiteDatabase database) {
        this.databaseHelper = new DatabaseHelper(database);
    }

    protected static String getTableName(String objectName) {
        return objectName.toUpperCase();
    }

    public TableCreator createObject(String objectName) {
        return new TableCreator(databaseHelper, objectName);
    }

    public TableTransformer update(String objectName) {
        return new TableTransformer(databaseHelper, objectName);
    }

    public Migrator remove(String objectName) {
        databaseHelper.dropTable(getTableName(objectName));
        return this;
    }

    @Deprecated
    public Migrator addTable(TableCreator tableCreator) {
        //TODO
        return this;
    }

    public static class TableTransformer {
        DatabaseHelper database;
        String objectName;

        public TableTransformer(DatabaseHelper database, String objectName) {
            this.database = database;
            this.objectName = objectName;
        }

        public ColumnTransformer transform(String column) {
            return new ColumnTransformer(database, this, objectName, column);
        }

        public TableTransformer renameTo(String newName) {
            database.renameTable(getTableName(objectName), getTableName(newName));
            database.renameTableDependencies(getTableName(objectName), getTableName(newName));
            return this;
        }

        public TableTransformer removeField(String column) {
            database.dropColumn(getTableName(objectName), column);
            return this;
        }

        public TableTransformer addField(String column, ColumnType.Primitive type) {
            database.addColumn(getTableName(objectName), column, type.getSqlName());
            return this;
        }

        @Deprecated
        public TableTransformer addField(String column, ColumnType.Array type) {
            //TODO
            return this;
        }

        @Deprecated
        public TableTransformer addField(String column, ColumnType.Collection type) {
            //TODO
            return this;
        }

        @Deprecated
        public TableTransformer addField(String column, ColumnType.ModelType type) {
            //TODO
            return this;
        }

        public void apply() {
            //TODO
        }

    }

    public static class ColumnTransformer {
        DatabaseHelper database;
        TableTransformer tableTransformer;
        String columnName;
        String objectName;

        public ColumnTransformer(DatabaseHelper database, TableTransformer tableTransformer, String objectName, String columnName) {
            this.database = database;
            this.tableTransformer = tableTransformer;
            this.columnName = columnName;
            this.objectName = objectName;
        }

        @Deprecated
        public TableTransformer renameTo(String newName) {
            database.renameColumn(getTableName(objectName),columnName,newName);
            return tableTransformer;
        }

        @Deprecated
        public TableTransformer type(ColumnType.Primitive fromType, ColumnType.Primitive newType) {
            //primitive

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

            return tableTransformer;
        }

        @Deprecated
        public TableTransformer type(ColumnType.Primitive fromType, ColumnType.Array newType) {
            //TODO
            //remove old
            tableTransformer.removeField(columnName);
            //add new
            tableTransformer.addField(columnName, newType);
            return tableTransformer;
        }

        @Deprecated
        public TableTransformer type(ColumnType.Primitive fromType, ColumnType.Collection newType) {
            //TODO
            //remove old
            tableTransformer.removeField(columnName);
            //add new
            tableTransformer.addField(columnName, newType);
            return tableTransformer;
        }

        @Deprecated
        public TableTransformer type(ColumnType.Array fromType, ColumnType.Collection newType) {
            if (!(ColumnType.Array.ArrayOfBooleans.equals(fromType) && ColumnType.Collection.ListOfBooleans.equals(newType)) &&
                    !(ColumnType.Array.ArrayOfStrings.equals(fromType) && ColumnType.Collection.ListOfStrings.equals(newType)) &&
                    !(ColumnType.Array.ArrayOfInts.equals(fromType) && ColumnType.Collection.ListOfInts.equals(newType)) &&
                    !(ColumnType.Array.ArrayOfFloats.equals(fromType) && ColumnType.Collection.ListOfFloats.equals(newType))) {

            }
            //else nothing to do
            return tableTransformer;
        }

        @Deprecated
        public TableTransformer type(ColumnType.Collection fromType, ColumnType.Array newType) {
            if (!(ColumnType.Array.ArrayOfBooleans.equals(newType) && ColumnType.Collection.ListOfBooleans.equals(fromType)) &&
                    !(ColumnType.Array.ArrayOfStrings.equals(newType) && ColumnType.Collection.ListOfStrings.equals(fromType)) &&
                    !(ColumnType.Array.ArrayOfInts.equals(newType) && ColumnType.Collection.ListOfInts.equals(fromType)) &&
                    !(ColumnType.Array.ArrayOfFloats.equals(newType) && ColumnType.Collection.ListOfFloats.equals(fromType))) {

            }
            //else nothing to do
            return tableTransformer;
        }

        public TableTransformer type(ColumnType.Primitive fromType, ColumnType.ModelType newType) {
            //TODO
            //remove old
            tableTransformer.removeField(columnName);
            //add new
            tableTransformer.addField(columnName, newType);
            return tableTransformer;
        }

        public TableTransformer type(ColumnType.Collection fromType, ColumnType.ModelType newType) {
            //TODO
            //remove old
            tableTransformer.removeField(columnName);
            //add new
            tableTransformer.addField(columnName, newType);
            return tableTransformer;
        }

        public TableTransformer type(ColumnType.Array fromType, ColumnType.ModelType newType) {
            //TODO
            //remove old
            tableTransformer.removeField(columnName);
            //add new
            tableTransformer.addField(columnName, newType);
            return tableTransformer;
        }

        public TableTransformer type(ColumnType.ModelType fromType, ColumnType.ModelType newType) {
            //TODO
            //remove old
            tableTransformer.removeField(columnName);
            //add new
            tableTransformer.addField(columnName, newType);
            return tableTransformer;
        }
    }

    public static class TableCreator {
        DatabaseHelper database;
        String objectName;

        public TableCreator(DatabaseHelper database, String objectName) {
            this.database = database;
            this.objectName = objectName;
        }

        public TableCreator field(String name, ColumnType type) {
            //TODO
            return this;
        }
    }
}

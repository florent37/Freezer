package com.github.florent37.rxandroidorm.migration;

import android.database.sqlite.SQLiteDatabase;

/**
 * Created by florentchampigny on 28/01/2016.
 */
public class Migrator {

    DatabaseHelper databaseHelper;

    public Migrator(SQLiteDatabase database) {
        this.databaseHelper = new DatabaseHelper(database);
    }

    public TableCreator createModel(String objectName) {
        return new TableCreator(databaseHelper, objectName);
    }

    public TableTransformer update(String objectName) {
        return new TableTransformer(databaseHelper, objectName);
    }

    public Migrator dropAndRecreate(String objectName) {
        databaseHelper.dropTable(getTableName(objectName));
        createModel(objectName);
        return this;
    }

    public Migrator remove(String objectName) {
        databaseHelper.dropTable(getTableName(objectName));
        return this;
    }

    public Migrator addTable(TableCreator tableCreator) {
        String creationQuery = tableCreator.creationQuery;
        databaseHelper.executeSql(creationQuery);
        return this;
    }

    protected static String getTableName(String objectName) {
        return objectName.toUpperCase();
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

        public TableTransformer addField(String column, ColumnType.Array type) {
            //nothing to do
            return this;
        }

        public TableTransformer addField(String column, ColumnType.Collection type) {
            //nothing to do
            return this;
        }

        public TableTransformer addField(String column, ColumnType.ModelType type) {
            database.createAssociationTable(objectName, type.objectName);
            return this;
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

        public TableTransformer renameTo(String newName) {
            database.renameColumn(getTableName(objectName), columnName, newName);
            return tableTransformer;
        }

        @Deprecated
        public TableTransformer type(ColumnType.Primitive fromType, ColumnType.Primitive newType) {
            if (!fromType.equals(newType)) {
                //try to transform
            }
            return tableTransformer;
        }

        @Deprecated
        public TableTransformer type(ColumnType.Primitive fromType, ColumnType.Array newType) {
            tableTransformer.removeField(columnName);
            tableTransformer.addField(columnName, newType);
            return tableTransformer;
        }

        @Deprecated
        public TableTransformer type(ColumnType.Primitive fromType, ColumnType.Collection newType) {
            tableTransformer.removeField(columnName);
            tableTransformer.addField(columnName, newType);
            return tableTransformer;
        }

        @Deprecated
        public TableTransformer type(ColumnType.Array fromType, ColumnType.Collection newType) {
            if (!(ColumnType.Array.ArrayOfBooleans.equals(fromType) && ColumnType.Collection.ListOfBooleans.equals(newType)) &&
                !(ColumnType.Array.ArrayOfStrings.equals(fromType) && ColumnType.Collection.ListOfStrings.equals(newType)) &&
                !(ColumnType.Array.ArrayOfInts.equals(fromType) && ColumnType.Collection.ListOfInts.equals(newType)) &&
                !(ColumnType.Array.ArrayOfFloats.equals(fromType) && ColumnType.Collection.ListOfFloats.equals(newType))) {
                //try to transform
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
                //try to transform
            }
            //else nothing to do
            return tableTransformer;
        }

        public TableTransformer type(ColumnType.Primitive fromType, ColumnType.ModelType newType) {
            tableTransformer.removeField(columnName);
            tableTransformer.addField(columnName, newType);
            return tableTransformer;
        }

        public TableTransformer type(ColumnType.Collection fromType, ColumnType.ModelType newType) {
            tableTransformer.removeField(columnName);
            tableTransformer.addField(columnName, newType);
            return tableTransformer;
        }

        public TableTransformer type(ColumnType.Array fromType, ColumnType.ModelType newType) {
            tableTransformer.removeField(columnName);
            tableTransformer.addField(columnName, newType);
            return tableTransformer;
        }

        public TableTransformer type(ColumnType.ModelType fromType, ColumnType.ModelType newType) {
            tableTransformer.removeField(columnName);
            tableTransformer.addField(columnName, newType);
            return tableTransformer;
        }
    }

}

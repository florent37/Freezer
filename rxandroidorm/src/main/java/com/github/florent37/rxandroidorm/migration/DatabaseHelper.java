package com.github.florent37.rxandroidorm.migration;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by florentchampigny on 29/01/2016.
 */
public class DatabaseHelper {

    protected static final String TAG = "DatabaseHelper";

    SQLiteDatabase database;

    public DatabaseHelper(SQLiteDatabase database) {
        this.database = database;
    }

    public static String getIdAssociationColumn(String tableName) {
        return tableName.toLowerCase() + "_id";
    }

    public void executeSql(String sql) {
        Log.d(TAG, sql);
        database.beginTransaction();
        try {
            database.execSQL(sql);
            database.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e(TAG, sql, e);
        } finally {
            database.endTransaction();
        }

    }

    public String createTableString(String tableName, List<TableColumn> columns) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("CREATE TABLE ").append(tableName).append("(");

        for (int i = 0, size = columns.size(); i < size; ++i) {
            TableColumn tableColumn = columns.get(i);
            stringBuilder.append(tableColumn.name).append(" ").append(tableColumn.type);
            if (tableColumn.primaryKey)
                stringBuilder.append(" PRIMARY KEY");
            if (i < size - 1)
                stringBuilder.append(",");
        }

        stringBuilder.append(");");
        return stringBuilder.toString();
    }

    public void createTable(String tableName, List<TableColumn> columns) {
        executeSql(createTableString(tableName, columns));
    }

    public void createAssociationTable(String tableNameFrom, String tableNameTo) {
        executeSql(createAssociationTableString(tableNameFrom, tableNameTo));
    }

    public String createAssociationTableString(String tableNameFrom, String tableNameTo) {
        String tableName = tableNameFrom.toUpperCase() + "_" + tableNameTo.toUpperCase();
        List<TableColumn> columns = new ArrayList<>();

        columns.add(new TableColumn("_id", "number", true));
        columns.add(new TableColumn(getIdAssociationColumn(tableNameFrom), "number", false));
        columns.add(new TableColumn(getIdAssociationColumn(tableNameTo), "number", false));

        return createTableString(tableName, columns);
    }

    public void dropColumn(String tableName, String colToRemove) {
        final List<TableColumn> updatedTableColumns = getTableColumns(tableName, colToRemove);
        final String columnsSeperated = TextUtils.join(",", TableColumn.getNames(updatedTableColumns));

        final String oldTable = tableName + "_old";
        renameTable(tableName, oldTable);

        // Creating the table on its new format (no redundant columns)
        createTable(tableName, updatedTableColumns);

        // Populating the table with the data
        executeSql("INSERT INTO " + tableName + "(" + columnsSeperated + ") SELECT " + columnsSeperated + " FROM " + tableName + "_old;");
        dropTable(oldTable);
    }

    public void renameColumn(String tableName, String oldName, String newName) {
        final List<TableColumn> updatedTableColumns = getTableColumns(tableName, null);

        final String oldColumnsSeperated = TextUtils.join(",", TableColumn.getNames(updatedTableColumns));
        TableColumn.rename(updatedTableColumns, oldName, newName);
        final String newColumnsSeperated = TextUtils.join(",", TableColumn.getNames(updatedTableColumns));

        final String oldTable = tableName + "_old";
        renameTable(tableName, oldTable);

        // Creating the table on its new format (no redundant columns)
        createTable(tableName, updatedTableColumns);

        // Populating the table with the data
        executeSql("INSERT INTO " + tableName + "(" + newColumnsSeperated + ") SELECT " + oldColumnsSeperated + " FROM " + tableName + "_old;");
        dropTable(oldTable);
    }

    public void addColumn(String tableName, String column, String sqlTypeName) {
        executeSql(String.format("ALTER TABLE %s ADD COLUMN %s %s", tableName, column, sqlTypeName));
    }

    public void dropTable(String tableName) {
        executeSql("DROP TABLE " + tableName + ";");
    }

    public void renameTable(String tableName, String newName) {
        executeSql("ALTER TABLE " + tableName + " RENAME TO " + newName + ";");
    }

    public void renameTableDependencies(String tableName, String newName) {
        for (String associationName : getTableNamesLike(tableName)) {
            String newAssociationName = associationName.replace(tableName, newName);
            renameTable(associationName, newAssociationName);
            renameColumn(newAssociationName, getIdAssociationColumn(tableName), getIdAssociationColumn(newName));
        }
    }

    public List<TableColumn> getTableColumns(String tableName, String except) {
        List<TableColumn> columns = new ArrayList<>();

        Cursor cur = database.rawQuery("pragma table_info(" + tableName + ");", null);

        while (cur.moveToNext()) {
            String name = cur.getString(cur.getColumnIndex("name"));
            if (except == null || !except.equals(name)) {
                String type = cur.getString(cur.getColumnIndex("type"));
                boolean primaryKey = cur.getInt(cur.getColumnIndex("pk")) == 1;
                columns.add(new TableColumn(name, type, primaryKey));
            }
        }
        cur.close();

        return columns;
    }

    public List<String> getTableNamesLike(String tableName) {
        List<String> tablesNames = new ArrayList<>();
        Cursor cur = database.rawQuery("SELECT name FROM sqlite_master WHERE type = \"table\" AND name like '%" + tableName + "%'", null);

        while (cur.moveToNext()) {
            tablesNames.add(cur.getString(0));
        }
        cur.close();

        return tablesNames;
    }

    public static class TableColumn {
        String name;
        String type;
        boolean primaryKey;

        public TableColumn(String name, String type, boolean primaryKey) {
            this.name = name;
            this.type = type;
            this.primaryKey = primaryKey;
        }

        public static List<String> getNames(List<TableColumn> columns) {
            List<String> names = new ArrayList<>();
            for (TableColumn tableColumn : columns)
                names.add(tableColumn.name);
            return names;
        }

        public static void rename(List<TableColumn> columns, String oldName, String newName) {
            for (int i = 0, size = columns.size(); i < size; ++i) {
                if (oldName.equals(columns.get(i).name))
                    columns.get(i).name = newName;
            }
        }
    }
}

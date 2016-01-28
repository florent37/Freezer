package com.github.florent37.orm.migration;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by florentchampigny on 28/01/2016.
 */
public class FreezerMigrator {
    public static TableCreator createObject(String objectName) {
        return new TableCreator(objectName);
    }

    protected static String getTableName(String objectName) {
        return objectName.toUpperCase();
    }

    public TableTransformer update(String objectName) {
        return new TableTransformer(objectName);
    }

    public FreezerMigrator addTable(TableCreator tableCreator) {
        //TODO
        return this;
    }

    public static class TableTransformer {
        String objectName;

        List<String> operation = new ArrayList<String>();

        public TableTransformer(String objectName) {
            this.objectName = objectName;
        }

        public ColumnTransformer transform(String column) {
            return new ColumnTransformer(this, objectName, column);
        }

        public TableTransformer name(String newName) {
            operation.add(String.format("ALTER TABLE %s RENAME TO %s", getTableName(objectName), getTableName(newName)));
            return this;
        }

        public TableTransformer removeField(String column) {
            operation.add(String.format("ALTER TABLE %s REMOVE COLUMN %s", getTableName(objectName), column));
            return this;
        }

        public TableTransformer addField(String column, ColumnType type) {
            //primitive
            if (type.isPrimitive()) {
                operation.add(String.format("ALTER TABLE %s ADD COLUMN %s %s", getTableName(objectName), column, type.getSqlName()));
            }

            //else TODO
            return this;
        }

        public void apply() {
            //TODO
        }

    }

    public static class ColumnTransformer {
        TableTransformer tableTransformer;
        String columnName;
        String objectName;

        List<String> operation = new ArrayList<String>();

        public ColumnTransformer(TableTransformer tableTransformer, String objectName, String columnName) {
            this.tableTransformer = tableTransformer;
            this.objectName = objectName;
            this.columnName = columnName;
        }

        public TableTransformer name(String newName) {
            //primitive
            operation.add(String.format("ALTER TABLE %s RENAME COLUMN %s TO %s", getTableName(objectName), columnName, newName));

            //else TODO
            return tableTransformer;
        }

        public TableTransformer type(ColumnType fromType, ColumnType newType) {
            //TODO
            return tableTransformer;
        }
    }

    public static class TableCreator {
        String objectName;

        public TableCreator(String objectName) {
            this.objectName = objectName;
        }

        public TableCreator field(String name, ColumnType type) {
            //TODO
            return this;
        }
    }
}

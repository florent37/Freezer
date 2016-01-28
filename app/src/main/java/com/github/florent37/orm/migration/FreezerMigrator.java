package com.github.florent37.orm.migration;

/**
 * Created by florentchampigny on 28/01/2016.
 */
public class FreezerMigrator {
    public static TableCreator createObject(String objectName) {
        return new TableCreator(objectName);
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

        public TableTransformer(String objectName) {
            //TODO
            this.objectName = objectName;
        }

        public ColumnTransformer transform(String column) {
            //TODO
            return new ColumnTransformer(this, column);
        }

        public TableTransformer name(String newName) {
            //TODO
            return this;
        }

        public void apply() {
            //TODO
        }

        public TableTransformer removeField(String column) {
            //TODO
            return this;
        }

        public TableTransformer addField(String column, ColumnType type) {
            //TODO
            return this;
        }

    }

    public static class ColumnTransformer {
        TableTransformer tableTransformer;
        String columnName;

        public ColumnTransformer(TableTransformer tableTransformer, String columnName) {
            this.tableTransformer = tableTransformer;
            this.columnName = columnName;
        }

        public TableTransformer name(String newName) {
            //TODO
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

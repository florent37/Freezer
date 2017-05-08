package com.github.florent37.rxandroidorm.migration;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by florentchampigny on 29/01/2016.
 */
public class TableCreator {
    DatabaseHelper database;
    String objectName;
    String creationQuery;

    List<Field> fieldList = new ArrayList<>();

    public TableCreator(DatabaseHelper database, String objectName) {
        this.database = database;
        this.objectName = objectName;
    }

    public TableCreator field(String name, ColumnType.Primitive type) {
        fieldList.add(new Field(name,type));
        return this;
    }

    @Deprecated
    public TableCreator field(String name, ColumnType.Array type) {
        //TODO
        return this;
    }

    @Deprecated
    public TableCreator field(String name, ColumnType.Collection type) {
        //TODO
        return this;
    }

    @Deprecated
    public TableCreator field(String name, ColumnType.ModelType type) {
        //TODO
        return this;
    }

    public TableCreator build(){
        String tableName = objectName.toUpperCase();

        StringBuilder query = new StringBuilder();
        query.append("CREATE TABLE ").append(tableName).append("(")
                .append("_id number PRIMARY KEY");

        for(int i=0, size=fieldList.size();i<size;++i){
            Field f = fieldList.get(i);
            query.append(" ,").append(f.name).append(" ").append(ColumnType.getSqlName(f.type));
        }
        query.append(");");

        creationQuery = query.toString();

        return this;
    }

    class Field{
        String name;
        Object type;

        public Field(String name, Object type) {
            this.name = name;
            this.type = type;
        }
    }
}

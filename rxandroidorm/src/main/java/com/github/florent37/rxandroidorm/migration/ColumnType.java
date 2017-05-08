package com.github.florent37.rxandroidorm.migration;

/**
 * Created by florentchampigny on 28/01/2016.
 */
public class ColumnType {

    public static ModelType ofModel(String objectName) {
        return new ModelType(objectName,false);
    }

    public static ModelType collectionOfModel(String objectName) {
        return new ModelType(objectName,true);
    }

    public enum Collection {
        ListOfInts("MODEL_INT"),
        ListOfFloats("MODEL_FLOAT"),
        ListOfBooleans("MODEL_BOOLEAN"),
        ListOfStrings("MODEL_STRING");

        private String associationTable;

        Collection(String associationTable) {
            this.associationTable = associationTable;
        }

        public java.lang.String getAssociationTable() {
            return associationTable;
        }
    }

    public enum Array {
        ArrayOfInts("MODEL_INT"),
        ArrayOfFloats("MODEL_FLOAT"),
        ArrayOfBooleans("MODEL_BOOLEAN"),
        ArrayOfStrings("MODEL_STRING");

        private String associationTable;

        Array(String associationTable) {
            this.associationTable = associationTable;
        }

        public java.lang.String getAssociationTable() {
            return associationTable;
        }
    }

    public enum Primitive {
        Int("number"),
        Float("real"),
        Boolean("number"),
        String("text"),
        Date("text"),
        ;

        private String sqlName;

        Primitive(String sqlName) {
            this.sqlName = sqlName;
        }

        public java.lang.String getSqlName() {
            return sqlName;
        }
    }

    public static class ModelType {
        String objectName;
        boolean collection;

        protected ModelType(String objectName, boolean collection) {
            this.objectName = objectName;
            this.collection = collection;
        }

    }

    public static String getSqlName(Object type){
        if(type instanceof Primitive)
            return ((Primitive)type).getSqlName();
        return ""; //TODO
    }
}

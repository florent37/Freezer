package fr.xebia.android.freezer.migration;

/**
 * Created by florentchampigny on 28/01/2016.
 */
public class ColumnType {

    @Deprecated
    public static ModelType ofModel(String objectName) {
        return new ModelType(objectName);
    }

    @Deprecated
    public static ModelType collectionOfModel(String objectName) {
        return new ModelType(objectName);
    }

    public enum Collection {
        @Deprecated
        ListOfInts,
        @Deprecated
        ListOfFloats,
        @Deprecated
        ListOfBooleans,
        @Deprecated
        ListOfStrings
    }

    public enum Array {
        @Deprecated
        ArrayOfInts,
        @Deprecated
        ArrayOfFloats,
        @Deprecated
        ArrayOfBooleans,
        @Deprecated
        ArrayOfStrings;
    }

    public enum Primitive {
        Int("number"),
        Float("real"),
        Boolean("number"),
        String("text");

        private String sqlName;

        Primitive(String sqlName) {
            this.sqlName = sqlName;
        }

        public java.lang.String getSqlName() {
            return sqlName;
        }
    }

    @Deprecated
    public static class ModelType {
        String objectName;

        protected ModelType(String objectName) {
            this.objectName = objectName;
        }

    }

    public static String getSqlName(Object type){
        if(type instanceof Primitive)
            return ((Primitive)type).getSqlName();
        return ""; //TODO
    }
}

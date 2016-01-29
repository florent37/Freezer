package fr.xebia.android.freezer.migration;

/**
 * Created by florentchampigny on 28/01/2016.
 */
public class ColumnType {

    public static ModelType ofModel(String objectName) {
        return new ModelType(objectName);
    }

    public enum Collection {
        ListOfInts,
        ListOfFloats,
        ListOfBooleans,
        ListOfStrings
    }

    public enum Array {
        ArrayOfInts,
        ArrayOfFloats,
        ArrayOfBooleans,
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

    public static class ModelType {
        String objectName;

        protected ModelType(String objectName) {
            this.objectName = objectName;
        }

    }
}

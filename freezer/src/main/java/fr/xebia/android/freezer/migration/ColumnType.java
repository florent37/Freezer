package fr.xebia.android.freezer.migration;

/**
 * Created by florentchampigny on 28/01/2016.
 */
public enum ColumnType {

    Int(FieldType.TYPE_PRIMITIVE,"number"),
    Float(FieldType.TYPE_PRIMITIVE, "real"),
    Boolean(FieldType.TYPE_PRIMITIVE, "number"),
    String(FieldType.TYPE_PRIMITIVE, "text"),

    ListOfInts(FieldType.TYPE_COLLECTION, null),
    ListOfFloats(FieldType.TYPE_COLLECTION, null),
    ListOfBooleans(FieldType.TYPE_COLLECTION, null),
    ListOfStrings(FieldType.TYPE_COLLECTION, null),

    ArrayOfInts(FieldType.TYPE_ARRAY, null),
    ArrayOfFloats(FieldType.TYPE_ARRAY, null),
    ArrayOfBooleans(FieldType.TYPE_ARRAY, null),
    ArrayOfStrings(FieldType.TYPE_ARRAY, null),

    Custom(FieldType.TYPE_CUSTOM, null)
    ;

    private int type;
    private String sqlName;

    ColumnType(int type, String sqlName) {
        this.type = type;
        this.sqlName = sqlName;
    }

    public static ColumnType ofTable(String objectName) {
        return null; //TODO
    }

    public boolean isPrimitive(){
        return type == FieldType.TYPE_PRIMITIVE;
    }

    public boolean isArray(){
        return type == FieldType.TYPE_ARRAY;
    }

    public boolean isCollection(){
        return type == FieldType.TYPE_COLLECTION;
    }

    public java.lang.String getSqlName() {
        return sqlName;
    }
}

package com.github.florent37.orm.migration;

/**
 * Created by florentchampigny on 28/01/2016.
 */
public enum  ColumnType {
    Int,
    Float,
    Boolean,
    String,

    ListOfInts,
    ListOfFloats,
    ListOfBooleans,
    ListOfStrings,

    ArrayOfInts,
    ArrayOfFloats,
    ArrayOfBooleans,
    ArrayOfStrings;

    public static ColumnType ofTable(String objectName) {
        return null; //TODO
    }
}

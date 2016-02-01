package fr.xebia.android.freezer.generator;

import com.squareup.javapoet.ArrayTypeName;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import fr.xebia.android.freezer.Constants;
import fr.xebia.android.freezer.ProcessUtils;

import javax.lang.model.element.Modifier;

/**
 * Created by florentchampigny on 26/01/2016.
 */
public class PrimitiveCursorHelperGenerator {
    public TypeSpec generate() {
        return TypeSpec.classBuilder(Constants.PRIMITIVE_CURSOR_HELPER)
                .addModifiers(Modifier.PUBLIC)
                .addMethod(MethodSpec.methodBuilder("create")
                        .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                        .returns(ArrayTypeName.get(String[].class))
                        .addCode("return new String[]{ ")
                        .addCode("\"create table $L ( $L integer primary key autoincrement, _object_id integer, value integer, $L text )\",\n", Constants.PRIMITIVE_TABLE_INT, Constants.FIELD_ID, Constants.FIELD_NAME)
                        .addCode("\"create table $L ( $L integer primary key autoincrement, _object_id integer, value text, $L text )\",\n", Constants.PRIMITIVE_TABLE_STRING, Constants.FIELD_ID, Constants.FIELD_NAME)
                        .addCode("\"create table $L ( $L integer primary key autoincrement, _object_id integer, value float, $L text )\",\n", Constants.PRIMITIVE_TABLE_FLOAT, Constants.FIELD_ID, Constants.FIELD_NAME)
                        .addCode("\"create table $L ( $L integer primary key autoincrement, _object_id integer, value integer, $L text )\"\n", Constants.PRIMITIVE_TABLE_BOOLEAN, Constants.FIELD_ID, Constants.FIELD_NAME)
                        .addCode("};")
                        .build())

                //region integers

                .addMethod(MethodSpec.methodBuilder("getIntegers")
                        .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                        .returns(ProcessUtils.listOf(Integer.class))
                        .addParameter(Constants.databaseClassName, "db")
                        .addParameter(TypeName.LONG, "objectId")
                        .addParameter(ClassName.get(String.class), "variable")
                        .addStatement("$T objects = new $T()", ProcessUtils.listOf(Integer.class), ProcessUtils.arraylistOf(Integer.class))
                        .addStatement("$T cursor = db.rawQuery(\"SELECT value FROM $L WHERE _object_id = ? AND _field_name= ?\", new String[]{String.valueOf(objectId), variable})", Constants.cursorClassName, Constants.PRIMITIVE_TABLE_INT)
                        .addStatement("cursor.moveToFirst()")
                        .beginControlFlow("while (!cursor.isAfterLast())")
                        .addStatement("objects.add(cursor.getInt(0))")
                        .addStatement("cursor.moveToNext()")
                        .addStatement("cursor.close()")
                        .endControlFlow()
                        .addStatement("return objects")
                        .build())

                .addMethod(MethodSpec.methodBuilder("getIntegersArray")
                        .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                        .returns(ArrayTypeName.get(int[].class))
                        .addParameter(Constants.databaseClassName, "db")
                        .addParameter(TypeName.LONG, "objectId")
                        .addParameter(ClassName.get(String.class), "variable")
                        .addStatement("List<Integer> objects = getIntegers(db,objectId,variable)")
                        .addStatement("if(objects == null) return null")
                        .addStatement("int[] array = new int[objects.size()]")
                        .beginControlFlow("for(int i=0,size=objects.size();i<size;++i)")
                        .addStatement("array[i] = objects.get(i)")
                        .endControlFlow()
                        .addStatement("return array")
                        .build())

                .addMethod(MethodSpec.methodBuilder("addIntegers")
                        .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                        .addParameter(Constants.databaseClassName, "db")
                        .addParameter(TypeName.LONG, "objectId")
                        .addParameter(ClassName.get(String.class), "variable")
                        .addParameter(ProcessUtils.listOf(Integer.class), "values")
                        .beginControlFlow("for(Integer value : values)")
                        .addStatement("$T contentValues = new $T()", Constants.contentValuesClassName, Constants.contentValuesClassName)
                        .addStatement("contentValues.put($S,objectId)", "_object_id")
                        .addStatement("contentValues.put($S,variable)", Constants.FIELD_NAME)
                        .addStatement("contentValues.put($S,value)", "value")
                        .addStatement("db.insert($S, null, contentValues)", Constants.PRIMITIVE_TABLE_INT)
                        .endControlFlow()
                        .build())

                .addMethod(MethodSpec.methodBuilder("addIntegers")
                        .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                        .addParameter(Constants.databaseClassName, "db")
                        .addParameter(TypeName.LONG, "objectId")
                        .addParameter(ClassName.get(String.class), "variable")
                        .addParameter(ArrayTypeName.get(int[].class), "values")
                        .beginControlFlow("for(Integer value : values)")
                        .addStatement("$T contentValues = new $T()", Constants.contentValuesClassName, Constants.contentValuesClassName)
                        .addStatement("contentValues.put($S,objectId)", "_object_id")
                        .addStatement("contentValues.put($S,variable)", Constants.FIELD_NAME)
                        .addStatement("contentValues.put($S,value)", "value")
                        .addStatement("db.insert($S, null, contentValues)", Constants.PRIMITIVE_TABLE_INT)
                        .endControlFlow()
                        .build())

                //endregion

                //region longs

                .addMethod(MethodSpec.methodBuilder("getLongs")
                        .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                        .returns(ProcessUtils.listOf(Long.class))
                        .addParameter(Constants.databaseClassName, "db")
                        .addParameter(TypeName.LONG, "objectId")
                        .addParameter(ClassName.get(String.class), "variable")
                        .addStatement("$T objects = new $T()", ProcessUtils.listOf(Long.class), ProcessUtils.arraylistOf(Long.class))
                        .addStatement("$T cursor = db.rawQuery(\"SELECT value FROM $L WHERE _object_id = ? AND _field_name= ?\", new String[]{String.valueOf(objectId), variable})", Constants.cursorClassName, Constants.PRIMITIVE_TABLE_LONG)
                        .addStatement("cursor.moveToFirst()")
                        .beginControlFlow("while (!cursor.isAfterLast())")
                        .addStatement("objects.add(cursor.getLong(0))")
                        .addStatement("cursor.moveToNext()")
                        .addStatement("cursor.close()")
                        .endControlFlow()
                        .addStatement("return objects")
                        .build())

                .addMethod(MethodSpec.methodBuilder("getLongsArray")
                        .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                        .returns(ArrayTypeName.get(long[].class))
                        .addParameter(Constants.databaseClassName, "db")
                        .addParameter(TypeName.LONG, "objectId")
                        .addParameter(ClassName.get(String.class), "variable")
                        .addStatement("List<Long> objects = getLongs(db,objectId,variable)")
                        .addStatement("if(objects == null) return null")
                        .addStatement("long[] array = new long[objects.size()]")
                        .beginControlFlow("for(int i=0,size=objects.size();i<size;++i)")
                        .addStatement("array[i] = objects.get(i)")
                        .endControlFlow()
                        .addStatement("return array")
                        .build())

                .addMethod(MethodSpec.methodBuilder("addLongs")
                        .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                        .addParameter(Constants.databaseClassName, "db")
                        .addParameter(TypeName.LONG, "objectId")
                        .addParameter(ClassName.get(String.class), "variable")
                        .addParameter(ProcessUtils.listOf(Long.class), "values")
                        .beginControlFlow("for(Long value : values)")
                        .addStatement("$T contentValues = new $T()", Constants.contentValuesClassName, Constants.contentValuesClassName)
                        .addStatement("contentValues.put($S,objectId)", "_object_id")
                        .addStatement("contentValues.put($S,variable)", Constants.FIELD_NAME)
                        .addStatement("contentValues.put($S,value)", "value")
                        .addStatement("db.insert($S, null, contentValues)", Constants.PRIMITIVE_TABLE_LONG)
                        .endControlFlow()
                        .build())

                .addMethod(MethodSpec.methodBuilder("addLongs")
                        .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                        .addParameter(Constants.databaseClassName, "db")
                        .addParameter(TypeName.LONG, "objectId")
                        .addParameter(ClassName.get(String.class), "variable")
                        .addParameter(ArrayTypeName.get(long[].class), "values")
                        .beginControlFlow("for(Long value : values)")
                        .addStatement("$T contentValues = new $T()", Constants.contentValuesClassName, Constants.contentValuesClassName)
                        .addStatement("contentValues.put($S,objectId)", "_object_id")
                        .addStatement("contentValues.put($S,variable)", Constants.FIELD_NAME)
                        .addStatement("contentValues.put($S,value)", "value")
                        .addStatement("db.insert($S, null, contentValues)", Constants.PRIMITIVE_TABLE_LONG)
                        .endControlFlow()
                        .build())

                //endregion

                //region strings

                .addMethod(MethodSpec.methodBuilder("getStrings")
                        .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                        .returns(ProcessUtils.listOf(String.class))
                        .addParameter(Constants.databaseClassName, "db")
                        .addParameter(TypeName.LONG, "objectId")
                        .addParameter(ClassName.get(String.class), "variable")
                        .addStatement("$T objects = new $T()", ProcessUtils.listOf(String.class), ProcessUtils.arraylistOf(String.class))
                        .addStatement("$T cursor = db.rawQuery(\"SELECT value FROM $L WHERE _object_id = ? AND _field_name= ?\", new String[]{String.valueOf(objectId), variable})", Constants.cursorClassName, Constants.PRIMITIVE_TABLE_STRING)
                        .addStatement("cursor.moveToFirst()")
                        .beginControlFlow("while (!cursor.isAfterLast())")
                        .addStatement("objects.add(cursor.getString(0))")
                        .addStatement("cursor.moveToNext()")
                        .addStatement("cursor.close()")
                        .endControlFlow()
                        .addStatement("return objects")
                        .build())

                .addMethod(MethodSpec.methodBuilder("getStringsArray")
                        .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                        .returns(ArrayTypeName.get(String[].class))
                        .addParameter(Constants.databaseClassName, "db")
                        .addParameter(TypeName.LONG, "objectId")
                        .addParameter(ClassName.get(String.class), "variable")
                        .addStatement("List<String> objects = getStrings(db,objectId,variable)")
                        .addStatement("if(objects == null) return null")
                        .addStatement("String[] array = new String[objects.size()]")
                        .beginControlFlow("for(int i=0,size=objects.size();i<size;++i)")
                        .addStatement("array[i] = objects.get(i)")
                        .endControlFlow()
                        .addStatement("return array")
                        .build())

                .addMethod(MethodSpec.methodBuilder("addStrings")
                        .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                        .addParameter(Constants.databaseClassName, "db")
                        .addParameter(TypeName.LONG, "objectId")
                        .addParameter(ClassName.get(String.class), "variable")
                        .addParameter(ProcessUtils.listOf(String.class), "values")
                        .beginControlFlow("for(String value : values)")
                        .addStatement("$T contentValues = new $T()", Constants.contentValuesClassName, Constants.contentValuesClassName)
                        .addStatement("contentValues.put($S,objectId)", "_object_id")
                        .addStatement("contentValues.put($S,variable)", Constants.FIELD_NAME)
                        .addStatement("contentValues.put($S,value)", "value")
                        .addStatement("db.insert($S, null, contentValues)", Constants.PRIMITIVE_TABLE_STRING)
                        .endControlFlow()
                        .build())

                .addMethod(MethodSpec.methodBuilder("addStrings")
                        .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                        .addParameter(Constants.databaseClassName, "db")
                        .addParameter(TypeName.LONG, "objectId")
                        .addParameter(ClassName.get(String.class), "variable")
                        .addParameter(ArrayTypeName.get(String[].class), "values")
                        .beginControlFlow("for(String value : values)")
                        .addStatement("$T contentValues = new $T()", Constants.contentValuesClassName, Constants.contentValuesClassName)
                        .addStatement("contentValues.put($S,objectId)", "_object_id")
                        .addStatement("contentValues.put($S,variable)", Constants.FIELD_NAME)
                        .addStatement("contentValues.put($S,value)", "value")
                        .addStatement("db.insert($S, null, contentValues)", Constants.PRIMITIVE_TABLE_STRING)
                        .endControlFlow()
                        .build())

                //endregion

                //region floats

                .addMethod(MethodSpec.methodBuilder("getFloats")
                        .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                        .returns(ProcessUtils.listOf(Float.class))
                        .addParameter(Constants.databaseClassName, "db")
                        .addParameter(TypeName.LONG, "objectId")
                        .addParameter(ClassName.get(String.class), "variable")
                        .addStatement("$T objects = new $T()", ProcessUtils.listOf(Float.class), ProcessUtils.arraylistOf(Float.class))
                        .addStatement("$T cursor = db.rawQuery(\"SELECT value FROM $L WHERE _object_id = ? AND _field_name= ?\", new String[]{String.valueOf(objectId), variable})", Constants.cursorClassName, Constants.PRIMITIVE_TABLE_FLOAT)
                        .addStatement("cursor.moveToFirst()")
                        .beginControlFlow("while (!cursor.isAfterLast())")
                        .addStatement("objects.add(cursor.getFloat(0))")
                        .addStatement("cursor.moveToNext()")
                        .addStatement("cursor.close()")
                        .endControlFlow()
                        .addStatement("return objects")
                        .build())

                .addMethod(MethodSpec.methodBuilder("getFloatsArray")
                        .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                        .returns(ArrayTypeName.get(float[].class))
                        .addParameter(Constants.databaseClassName, "db")
                        .addParameter(TypeName.LONG, "objectId")
                        .addParameter(ClassName.get(String.class), "variable")
                        .addStatement("List<Float> objects = getFloats(db,objectId,variable)")
                        .addStatement("if(objects == null) return null")
                        .addStatement("float[] array = new float[objects.size()]")
                        .beginControlFlow("for(int i=0,size=objects.size();i<size;++i)")
                        .addStatement("array[i] = objects.get(i)")
                        .endControlFlow()
                        .addStatement("return array")
                        .build())

                .addMethod(MethodSpec.methodBuilder("addFloats")
                        .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                        .addParameter(Constants.databaseClassName, "db")
                        .addParameter(TypeName.LONG, "objectId")
                        .addParameter(ClassName.get(String.class), "variable")
                        .addParameter(ProcessUtils.listOf(Float.class), "values")
                        .beginControlFlow("for(float value : values)")
                        .addStatement("$T contentValues = new $T()", Constants.contentValuesClassName, Constants.contentValuesClassName)
                        .addStatement("contentValues.put($S,objectId)", "_object_id")
                        .addStatement("contentValues.put($S,variable)", Constants.FIELD_NAME)
                        .addStatement("contentValues.put($S,value)", "value")
                        .addStatement("db.insert($S, null, contentValues)", Constants.PRIMITIVE_TABLE_FLOAT)
                        .endControlFlow()
                        .build())

                .addMethod(MethodSpec.methodBuilder("addFloats")
                        .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                        .addParameter(Constants.databaseClassName, "db")
                        .addParameter(TypeName.LONG, "objectId")
                        .addParameter(ClassName.get(String.class), "variable")
                        .addParameter(ArrayTypeName.get(float[].class), "values")
                        .beginControlFlow("for(float value : values)")
                        .addStatement("$T contentValues = new $T()", Constants.contentValuesClassName, Constants.contentValuesClassName)
                        .addStatement("contentValues.put($S,objectId)", "_object_id")
                        .addStatement("contentValues.put($S,variable)", Constants.FIELD_NAME)
                        .addStatement("contentValues.put($S,value)", "value")
                        .addStatement("db.insert($S, null, contentValues)", Constants.PRIMITIVE_TABLE_FLOAT)
                        .endControlFlow()
                        .build())

                //endregion

                //region double

                .addMethod(MethodSpec.methodBuilder("getDoubles")
                        .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                        .returns(ProcessUtils.listOf(Double.class))
                        .addParameter(Constants.databaseClassName, "db")
                        .addParameter(TypeName.LONG, "objectId")
                        .addParameter(ClassName.get(String.class), "variable")
                        .addStatement("$T objects = new $T()", ProcessUtils.listOf(Double.class), ProcessUtils.arraylistOf(Double.class))
                        .addStatement("$T cursor = db.rawQuery(\"SELECT value FROM $L WHERE _object_id = ? AND _field_name= ?\", new String[]{String.valueOf(objectId), variable})", Constants.cursorClassName, Constants.PRIMITIVE_TABLE_DOUBLE)
                        .addStatement("cursor.moveToFirst()")
                        .beginControlFlow("while (!cursor.isAfterLast())")
                        .addStatement("objects.add(cursor.getDouble(0))")
                        .addStatement("cursor.moveToNext()")
                        .addStatement("cursor.close()")
                        .endControlFlow()
                        .addStatement("return objects")
                        .build())

                .addMethod(MethodSpec.methodBuilder("getDoublesArray")
                        .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                        .returns(ArrayTypeName.get(double[].class))
                        .addParameter(Constants.databaseClassName, "db")
                        .addParameter(TypeName.LONG, "objectId")
                        .addParameter(ClassName.get(String.class), "variable")
                        .addStatement("List<Double> objects = getDoubles(db,objectId,variable)")
                        .addStatement("if(objects == null) return null")
                        .addStatement("double[] array = new double[objects.size()]")
                        .beginControlFlow("for(int i=0,size=objects.size();i<size;++i)")
                        .addStatement("array[i] = objects.get(i)")
                        .endControlFlow()
                        .addStatement("return array")
                        .build())

                .addMethod(MethodSpec.methodBuilder("addDoubles")
                        .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                        .addParameter(Constants.databaseClassName, "db")
                        .addParameter(TypeName.LONG, "objectId")
                        .addParameter(ClassName.get(String.class), "variable")
                        .addParameter(ProcessUtils.listOf(Double.class), "values")
                        .beginControlFlow("for(double value : values)")
                        .addStatement("$T contentValues = new $T()", Constants.contentValuesClassName, Constants.contentValuesClassName)
                        .addStatement("contentValues.put($S,objectId)", "_object_id")
                        .addStatement("contentValues.put($S,variable)", Constants.FIELD_NAME)
                        .addStatement("contentValues.put($S,value)", "value")
                        .addStatement("db.insert($S, null, contentValues)", Constants.PRIMITIVE_TABLE_DOUBLE)
                        .endControlFlow()
                        .build())

                .addMethod(MethodSpec.methodBuilder("addDoubles")
                        .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                        .addParameter(Constants.databaseClassName, "db")
                        .addParameter(TypeName.LONG, "objectId")
                        .addParameter(ClassName.get(String.class), "variable")
                        .addParameter(ArrayTypeName.get(double[].class), "values")
                        .beginControlFlow("for(double value : values)")
                        .addStatement("$T contentValues = new $T()", Constants.contentValuesClassName, Constants.contentValuesClassName)
                        .addStatement("contentValues.put($S,objectId)", "_object_id")
                        .addStatement("contentValues.put($S,variable)", Constants.FIELD_NAME)
                        .addStatement("contentValues.put($S,value)", "value")
                        .addStatement("db.insert($S, null, contentValues)", Constants.PRIMITIVE_TABLE_DOUBLE)
                        .endControlFlow()
                        .build())

                //endregion

                //region booleans

                .addMethod(MethodSpec.methodBuilder("getBooleans")
                        .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                        .returns(ProcessUtils.listOf(Boolean.class))
                        .addParameter(Constants.databaseClassName, "db")
                        .addParameter(TypeName.LONG, "objectId")
                        .addParameter(ClassName.get(String.class), "variable")
                        .addStatement("$T objects = new $T()", ProcessUtils.listOf(Boolean.class), ProcessUtils.arraylistOf(Boolean.class))
                        .addStatement("$T cursor = db.rawQuery(\"SELECT value FROM $L WHERE _object_id = ? AND _field_name= ?\", new String[]{String.valueOf(objectId), variable})", Constants.cursorClassName, Constants.PRIMITIVE_TABLE_BOOLEAN)
                        .addStatement("cursor.moveToFirst()")
                        .beginControlFlow("while (!cursor.isAfterLast())")
                        .addStatement("objects.add(cursor.getInt(0) == 1 ? true : false)")
                        .addStatement("cursor.moveToNext()")
                        .addStatement("cursor.close()")
                        .endControlFlow()
                        .addStatement("return objects")
                        .build())

                .addMethod(MethodSpec.methodBuilder("getBooleansArray")
                        .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                        .returns(ArrayTypeName.get(boolean[].class))
                        .addParameter(Constants.databaseClassName, "db")
                        .addParameter(TypeName.LONG, "objectId")
                        .addParameter(ClassName.get(String.class), "variable")
                        .addStatement("List<Boolean> objects = getBooleans(db,objectId,variable)")
                        .addStatement("if(objects == null) return null")
                        .addStatement("boolean[] array = new boolean[objects.size()]")
                        .beginControlFlow("for(int i=0,size=objects.size();i<size;++i)")
                        .addStatement("array[i] = objects.get(i)")
                        .endControlFlow()
                        .addStatement("return array")
                        .build())

                .addMethod(MethodSpec.methodBuilder("addBooleans")
                        .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                        .addParameter(Constants.databaseClassName, "db")
                        .addParameter(TypeName.LONG, "objectId")
                        .addParameter(ClassName.get(String.class), "variable")
                        .addParameter(ProcessUtils.listOf(Boolean.class), "values")
                        .beginControlFlow("for(boolean value : values)")
                        .addStatement("$T contentValues = new $T()", Constants.contentValuesClassName, Constants.contentValuesClassName)
                        .addStatement("contentValues.put($S,objectId)", "_object_id")
                        .addStatement("contentValues.put($S,variable)", Constants.FIELD_NAME)
                        .addStatement("contentValues.put($S,value ? 1 : 0)", "value")
                        .addStatement("db.insert($S, null, contentValues)", Constants.PRIMITIVE_TABLE_BOOLEAN)
                        .endControlFlow()
                        .build())

                .addMethod(MethodSpec.methodBuilder("addBooleans")
                        .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                        .addParameter(Constants.databaseClassName, "db")
                        .addParameter(TypeName.LONG, "objectId")
                        .addParameter(ClassName.get(String.class), "variable")
                        .addParameter(ArrayTypeName.get(boolean[].class), "values")
                        .beginControlFlow("for(boolean value : values)")
                        .addStatement("$T contentValues = new $T()", Constants.contentValuesClassName, Constants.contentValuesClassName)
                        .addStatement("contentValues.put($S,objectId)", "_object_id")
                        .addStatement("contentValues.put($S,variable)", Constants.FIELD_NAME)
                        .addStatement("contentValues.put($S,value ? 1 : 0)", "value")
                        .addStatement("db.insert($S, null, contentValues)", Constants.PRIMITIVE_TABLE_BOOLEAN)
                        .endControlFlow()
                        .build())

                //endregion

                .build();
    }
}
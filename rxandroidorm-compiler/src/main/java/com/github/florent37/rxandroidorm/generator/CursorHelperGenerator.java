package com.github.florent37.rxandroidorm.generator;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.VariableElement;

import com.github.florent37.rxandroidorm.Constants;
import com.github.florent37.rxandroidorm.Dependency;
import com.github.florent37.rxandroidorm.ProcessUtils;

/**
 * Created by florentchampigny on 18/01/2016.
 */
public class CursorHelperGenerator {

    Element element;
    String objectName;
    TypeName modelType;
    List<VariableElement> fields;
    List<VariableElement> otherClassFields;
    List<VariableElement> collections;
    List<Dependency> dependencies = new ArrayList<>();

    public CursorHelperGenerator(Element element) {
        this.element = element;
        this.objectName = ProcessUtils.getObjectName(element);
        this.modelType = TypeName.get(element.asType());
        this.fields = ProcessUtils.getPrimitiveFields(element);
        this.otherClassFields = ProcessUtils.getNonPrimitiveClassFields(element);
        this.collections = ProcessUtils.getCollectionsOfPrimitiveFields(element);
    }

    public TypeSpec generate() {

        MethodSpec.Builder fromCursorB = MethodSpec.methodBuilder("fromCursor")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(modelType)
                .addParameter(Constants.cursorClassName, "cursor")
                .addParameter(Constants.databaseClassName, "db")
                .addStatement("$T object = new $T()", modelType, ProcessUtils.getModelProxy(element))

                .addStatement("long objectId = cursor.getLong(cursor.getColumnIndex($S))", Constants.FIELD_ID)
                .addStatement("$L(objectId)", ProcessUtils.setModelId("object"));

        //for
        for (int i = 0; i < fields.size(); ++i) {
            VariableElement variableElement = fields.get(i);
            if (ProcessUtils.isPrimitive(variableElement)) {
                fromCursorB.addStatement("int index$L = cursor.getColumnIndex($S)", i, variableElement.getSimpleName());
                fromCursorB.beginControlFlow("if(index$L != -1)", i);
                String cursor = "cursor.get$L(index$L)";

                if (ProcessUtils.isDate(variableElement)) {
                    fromCursorB.addCode("try{ \n")
                            .addStatement("String date$L = cursor.getString(cursor.getColumnIndex($S))", i, variableElement.getSimpleName())
                            .addStatement("if(date$L != null) object.$L = new $T($S).parse(date$L)",
                                    i, variableElement.getSimpleName(), Constants.simpleDateFormatClassName, Constants.DATE_FORMAT, i)
                            .addCode("} catch ($T e) { e.printStackTrace(); }", TypeName.get(Exception.class));
                } else {
                    cursor = String.format(ProcessUtils.getFieldCast(variableElement), cursor);

                    fromCursorB.addStatement("object.$L = " + cursor, variableElement.getSimpleName(), ProcessUtils.getFieldType(variableElement), i);
                }
                fromCursorB.endControlFlow();
            }
        }

        for (int i = 0; i < otherClassFields.size(); ++i) {
            VariableElement variableElement = otherClassFields.get(i);

            fromCursorB.addCode("\n");
            String JOIN_NAME = ProcessUtils.getTableName(objectName) + "_" + ProcessUtils.getTableName(variableElement);

            fromCursorB.addStatement("$T cursor$L = db.rawQuery($S,new String[]{String.valueOf(objectId), $S})", Constants.cursorClassName, i, "SELECT * FROM " + ProcessUtils.getTableName(variableElement) + ", " + JOIN_NAME + " WHERE " + JOIN_NAME + "." + ProcessUtils.getKeyName(objectName) + " = ? AND " + ProcessUtils.getTableName(variableElement) + "." + Constants.FIELD_ID + " = " + JOIN_NAME + "." + ProcessUtils.getKeyName(variableElement) + " AND " + JOIN_NAME + "." + Constants.FIELD_NAME + "= ?", ProcessUtils.getObjectName(variableElement));

            fromCursorB.addStatement("$T objects$L = $T.get(cursor$L,db)", ProcessUtils.listOf(variableElement), i, ProcessUtils.getFieldCursorHelperClass(variableElement), i);

            if (ProcessUtils.isCollection(variableElement))
                fromCursorB.addStatement("if(!objects$L.isEmpty()) object.$L = objects$L", i, ProcessUtils.getObjectName(variableElement), i);
            else
                fromCursorB.addStatement("if(!objects$L.isEmpty()) object.$L = objects$L.get(0)", i, ProcessUtils.getObjectName(variableElement), i);

            fromCursorB.addStatement("cursor$L.close()", i);
        }

        for (int i = 0; i < collections.size(); ++i) {
            VariableElement variableElement = collections.get(i);
            fromCursorB.addStatement("object.$L = $T.$L(db,objectId,$S)", ProcessUtils.getObjectName(variableElement), Constants.primitiveCursorHelper, ProcessUtils.getPrimitiveCursorHelperFunction(variableElement), ProcessUtils.getObjectName(variableElement));
        }

        fromCursorB.addCode("\n").addStatement("return object");

        MethodSpec.Builder getValuesB = MethodSpec.methodBuilder("getValues")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(Constants.contentValuesClassName)
                .addParameter(modelType, "object")
                .addParameter(ClassName.get(String.class), "name")
                .addStatement("$T values = new $T()", Constants.contentValuesClassName, Constants.contentValuesClassName)
                .addStatement("if(name != null) values.put($S,name)", Constants.FIELD_NAME);

        for (int i = 0; i < fields.size(); ++i) {
            VariableElement variableElement = fields.get(i);
            if (ProcessUtils.isPrimitive(variableElement)) {
                if (ProcessUtils.isDate(variableElement)) {
                    getValuesB.addStatement("if(object.$L != null) values.put($S, new $T($S).format(object.$L))", variableElement.getSimpleName(), variableElement.getSimpleName(), Constants.simpleDateFormatClassName, Constants.DATE_FORMAT, variableElement.getSimpleName());
                } else if (!ProcessUtils.isIdField(variableElement)) {
                    String statement = "values.put($S,object.$L)";
                    if (ProcessUtils.isModelId(variableElement))
                        statement = "if(" + ProcessUtils.getCursorHelperName("object") + " != 0) " + statement;
                    getValuesB.addStatement(statement, variableElement.getSimpleName(), variableElement.getSimpleName());
                }
            }
        }

        getValuesB.addStatement(ProcessUtils.getModelId(element, "object", "id"));
        getValuesB.addStatement("if(id != null && id != 0) values.put($S, id)", "_id");

        List<MethodSpec> joinMethods = new ArrayList<>();
        Set<String> addedMethodsNames = new HashSet<>();
        for (VariableElement variableElement : otherClassFields) {
            String JOIN_NAME = ProcessUtils.getTableName(objectName) + "_" + ProcessUtils.getTableName(variableElement);
            if (!addedMethodsNames.contains(JOIN_NAME)) {
                joinMethods.add(MethodSpec.methodBuilder("get" + JOIN_NAME + "Values")
                        .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                        .returns(Constants.contentValuesClassName)
                        .addParameter(TypeName.LONG, "objectId")
                        .addParameter(TypeName.LONG, "secondObjectId")
                        .addParameter(ClassName.get(String.class), "name")
                        .addStatement("$T values = new $T()", Constants.contentValuesClassName, Constants.contentValuesClassName)
                        .addStatement("values.put($S,objectId)", ProcessUtils.getKeyName(this.objectName))
                        .addStatement("values.put($S,secondObjectId)", ProcessUtils.getKeyName(variableElement))
                        .addStatement("values.put($S,name)", Constants.FIELD_NAME)
                        .addStatement("return values").build());
                addedMethodsNames.add(JOIN_NAME);
            }
        }

        getValuesB.addStatement("return values");

        MethodSpec get = MethodSpec.methodBuilder("get")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(ProcessUtils.listOf(modelType))
                .addParameter(Constants.cursorClassName, "cursor")
                .addParameter(Constants.databaseClassName, "db")
                .addStatement("$T objects = new $T()", ProcessUtils.listOf(modelType), ProcessUtils.arraylistOf(modelType))
                .addStatement("cursor.moveToFirst()")
                .addCode("while (!cursor.isAfterLast()) {\n")
                .addStatement("    $T object = fromCursor(cursor,db)", modelType)
                .addStatement("    objects.add(object)")
                .addStatement("    cursor.moveToNext()")
                .addCode("}\n")
                .addStatement("return objects")
                .build();

        return TypeSpec.classBuilder(ProcessUtils.getCursorHelperName(objectName))
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addMethod(fromCursorB.build())
                .addMethod(getValuesB.build())
                .addMethod(get)
                .addMethods(joinMethods)
                .addMethods(generateInsertMethods())
                .addMethods(generateUpdateMethod())
                .build();

    }

    protected List<MethodSpec> generateUpdateMethod() {
        List<MethodSpec> methodSpecs = new ArrayList<>();

        MethodSpec.Builder updateB = MethodSpec.methodBuilder("update")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(TypeName.LONG)
                .addParameter(Constants.databaseClassName, "database")
                .addParameter(modelType, "object");

        updateB.addStatement(ProcessUtils.getModelId(element, "object", "objectId"));

        updateB.beginControlFlow("if(objectId != null)");
        updateB.addStatement("database.update($S, getValues(object,null), $S, new String[]{String.valueOf(objectId)})", ProcessUtils.getTableName(objectName), Constants.FIELD_ID + " = ?");

        //for (int i = 0; i < collections.size(); ++i) {
        //    VariableElement variableElement = collections.get(i);
        //    updateB.addStatement("if(object.$L != null) $T.$L(database,objectId,$S,object.$L)", ProcessUtils.getObjectName(variableElement), Constants.primitiveCursorHelper, ProcessUtils.addPrimitiveCursorHelperFunction(variableElement), ProcessUtils.getObjectName(variableElement), ProcessUtils.getObjectName(variableElement));
        //}

        updateB.endControlFlow();

        for (VariableElement variableElement : otherClassFields) {
            updateB.addStatement("$T.updateFor$L(database,object.$L, objectId , $S)", ProcessUtils.getFieldCursorHelperClass(variableElement), objectName, ProcessUtils.getObjectName(variableElement), ProcessUtils.getObjectName(variableElement));

            String JOINTABLE = ProcessUtils.getTableName(objectName) + "_" + ProcessUtils.getTableName(variableElement);

            MethodSpec.Builder updateForB = MethodSpec.methodBuilder("updateFor" + objectName)
                    .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                    .addParameter(Constants.databaseClassName, "database")
                    .addParameter(ProcessUtils.getFieldClass(variableElement), "child")
                    .addParameter(TypeName.LONG, "parentId")
                    .addParameter(ClassName.get(String.class), "variable")

                    .beginControlFlow("if(child == null)")
                    .addStatement("database.delete($S, \"$L = ? AND $L = ?\", new String[]{String.valueOf(parentId), variable})", JOINTABLE, ProcessUtils.getKeyName(objectName), Constants.FIELD_NAME)
                    .endControlFlow()

                    .beginControlFlow("else");
            {
                updateForB.addStatement(ProcessUtils.getModelId(variableElement, "child", "objectId"));
            }

            updateForB
                    .beginControlFlow("if(objectId != null)")
                    .addStatement("update(database,child)")
                    .endControlFlow()

                    .beginControlFlow("else")
                    .addStatement("insertFor$L(database,child,parentId,variable)", objectName)
                    .endControlFlow()

                    .endControlFlow();

            MethodSpec update = updateForB.build();

            MethodSpec.Builder updateAllB = MethodSpec.methodBuilder("updateFor" + objectName)
                    .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                    .addParameter(Constants.databaseClassName, "database")
                    .addParameter(ProcessUtils.listOf(ProcessUtils.getFieldClass(variableElement)), "objects")
                    .addParameter(TypeName.LONG, "parentId")
                    .addParameter(ClassName.get(String.class), "variable")

                    .addStatement("database.delete($S, \"$L = ? AND $L = ?\", new String[]{String.valueOf(parentId), variable})", JOINTABLE, ProcessUtils.getKeyName(objectName), Constants.FIELD_NAME)
                    .beginControlFlow("if(objects != null)")
                    .beginControlFlow("for($T child : objects)", ProcessUtils.getFieldClass(variableElement));

            {
                updateAllB.addStatement(ProcessUtils.getModelId(variableElement, "child", "objectId"));

                updateAllB
                        .beginControlFlow("if(objectId != null)")
                        .addStatement("update(database,child)")
                        .addStatement("database.insert($S, null, get$LValues(parentId, objectId, variable))", JOINTABLE, JOINTABLE)
                        .endControlFlow()

                        .beginControlFlow("else")
                        .addStatement("insertFor$L(database,child,parentId,variable)", objectName)
                        .endControlFlow();
            }

            updateAllB
                    .endControlFlow()
                    .endControlFlow();

            dependencies.add(new Dependency(ProcessUtils.getFieldClass(variableElement), Arrays.asList(update, updateAllB.build())));
        }

        methodSpecs.add(updateB.addStatement("return objectId").build());

        methodSpecs.add(MethodSpec.methodBuilder("update")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .addParameter(Constants.databaseClassName, "database")
                .addParameter(ProcessUtils.listOf(modelType), "objects")
                .addStatement("for($T object : objects) update(database,object)", modelType)
                .build());

        return methodSpecs;
    }

    protected List<MethodSpec> generateInsertMethods() {
        List<MethodSpec> methodSpecs = new ArrayList<>();

        MethodSpec.Builder insertB = MethodSpec.methodBuilder("insert")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(TypeName.LONG)
                .addParameter(Constants.databaseClassName, "database")
                .addParameter(modelType, "object")
                .addStatement("long objectId = database.insertWithOnConflict($S, null, getValues(object,null), android.database.sqlite.SQLiteDatabase.CONFLICT_REPLACE)", ProcessUtils.getTableName(objectName));

        Element idField = ProcessUtils.getIdField(element);
        if (idField != null)
            insertB.addStatement("object.$L = objectId", ProcessUtils.getObjectName(idField));

        for (VariableElement variableElement : otherClassFields) {
            insertB.addStatement("$T.insertFor$L(database,object.$L, objectId , $S)", ProcessUtils.getFieldCursorHelperClass(variableElement), objectName, ProcessUtils.getObjectName(variableElement), ProcessUtils.getObjectName(variableElement));

            String JOINTABLE = ProcessUtils.getTableName(objectName) + "_" + ProcessUtils.getTableName(variableElement);

            MethodSpec insert = MethodSpec.methodBuilder("insertFor" + objectName)
                    .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                    .addParameter(Constants.databaseClassName, "database")
                    .addParameter(ProcessUtils.getFieldClass(variableElement), "child")
                    .addParameter(TypeName.LONG, "parentId")
                    .addParameter(ClassName.get(String.class), "variable")

                    .beginControlFlow("if(child != null)")
                    .addStatement("long objectId = insert(database,child)")
                    .addStatement("database.insert($S, null, get$LValues(parentId, objectId, variable))", JOINTABLE, JOINTABLE)
                    .endControlFlow()

                    .build();

            MethodSpec insertAll = MethodSpec.methodBuilder("insertFor" + objectName)
                    .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                    .addParameter(Constants.databaseClassName, "database")
                    .addParameter(ProcessUtils.listOf(ProcessUtils.getFieldClass(variableElement)), "objects")
                    .addParameter(TypeName.LONG, "parentId")
                    .addParameter(ClassName.get(String.class), "variable")

                    .beginControlFlow("if(objects != null)")
                    .beginControlFlow("for($T child : objects)", ProcessUtils.getFieldClass(variableElement))
                    .addStatement("insertFor$L(database,child, parentId, variable)", objectName)
                    .endControlFlow()
                    .endControlFlow()

                    .build();

            MethodSpec getTABLE_NAMEvalues = MethodSpec.methodBuilder("get" + JOINTABLE + "Values")
                    .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                    .returns(Constants.contentValuesClassName)
                    .addParameter(TypeName.LONG, "objectId")
                    .addParameter(TypeName.LONG, "secondObjectId")
                    .addParameter(ClassName.get(String.class), "name")
                    .addStatement("$T values = new $T()", Constants.contentValuesClassName, Constants.contentValuesClassName)
                    .addStatement("values.put($S,objectId)", ProcessUtils.getKeyName(this.objectName))
                    .addStatement("values.put($S,secondObjectId)", ProcessUtils.getKeyName(variableElement))
                    .addStatement("values.put($S,name)", Constants.FIELD_NAME)
                    .addStatement("return values").build();

            dependencies.add(new Dependency(ProcessUtils.getFieldClass(variableElement), Arrays.asList(insert, insertAll, getTABLE_NAMEvalues)));
        }

        for (int i = 0; i < collections.size(); ++i) {
            VariableElement variableElement = collections.get(i);
            insertB.addStatement("if(object.$L != null) $T.$L(database,objectId,$S,object.$L)", ProcessUtils.getObjectName(variableElement), Constants.primitiveCursorHelper, ProcessUtils.addPrimitiveCursorHelperFunction(variableElement), ProcessUtils.getObjectName(variableElement), ProcessUtils.getObjectName(variableElement));
        }

        methodSpecs.add(insertB.addStatement("return objectId").build());

        methodSpecs.add(MethodSpec.methodBuilder("insert")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .addParameter(Constants.databaseClassName, "database")
                .addParameter(ProcessUtils.listOf(modelType), "objects")
                .addStatement("for($T object : objects) insert(database,object)", modelType)
                .build());

        return methodSpecs;
    }

    public List getDependencies() {
        return dependencies;
    }
}

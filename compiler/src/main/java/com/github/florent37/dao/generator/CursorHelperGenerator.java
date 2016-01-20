package com.github.florent37.dao.generator;

import com.github.florent37.dao.Constants;
import com.github.florent37.dao.Dependency;
import com.github.florent37.dao.FridgeUtils;
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

/**
 * Created by florentchampigny on 18/01/2016.
 */
public class CursorHelperGenerator {

    Element element;
    String objectName;
    TypeName modelType;
    List<VariableElement> fields;
    List<VariableElement> otherClassFields;
    List<Dependency> dependencies = new ArrayList();

    public CursorHelperGenerator(Element element) {
        this.element = element;
        this.objectName = FridgeUtils.getObjectName(element);
        this.modelType = TypeName.get(element.asType());
        this.fields = FridgeUtils.getFields(element);
        this.otherClassFields = FridgeUtils.getNonPrimitiveClassFields(element);
    }

    public TypeSpec generate() {

        MethodSpec.Builder fromCursorSimple = MethodSpec.methodBuilder("fromCursor")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(modelType)
                .addParameter(Constants.cursorClassName, "cursor")
                .addParameter(Constants.databaseClassName, "db")
                .addStatement("return fromCursor(cursor,db,0)");

        MethodSpec.Builder fromCursorB = MethodSpec.methodBuilder("fromCursor")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(modelType)
                .addParameter(Constants.cursorClassName, "cursor")
                .addParameter(Constants.databaseClassName, "db")
                .addParameter(TypeName.INT, "start")
                .addStatement("$T object = new $T()", modelType, modelType);

        //for
        for (int i = 0; i < fields.size(); ++i) {
            VariableElement variableElement = fields.get(i);
            if (FridgeUtils.isPrimitive(variableElement)) {
                String cursor = "cursor.get$L(start + $L)";
                cursor = String.format(FridgeUtils.getFieldCast(variableElement), cursor);

                fromCursorB.addStatement("object.$L = " + cursor, variableElement.getSimpleName(), FridgeUtils.getFieldType(variableElement), i);
            } else {

                fromCursorB.addCode("\n");
                String JOIN_NAME = FridgeUtils.getTableName(objectName) + "_" + FridgeUtils.getTableName(variableElement);

                fromCursorB.addStatement("$T cursor$L = db.rawQuery($S,new String[]{String.valueOf(object._id), $S})", Constants.cursorClassName, i, "SELECT * FROM " + FridgeUtils.getTableName(variableElement) + ", " + JOIN_NAME + " WHERE " + JOIN_NAME + "." + FridgeUtils.getKeyName(objectName) + " = ? AND " + FridgeUtils.getTableName(variableElement) + "." + Constants.FIELD_ID + " = " + JOIN_NAME + "." + FridgeUtils.getKeyName(variableElement) + " AND " + JOIN_NAME + "." + Constants.FIELD_NAME + "= ?", FridgeUtils.getObjectName(variableElement));

                if (FridgeUtils.isCollection(variableElement)) {
                    fromCursorB.addStatement("object.$L = $T.get(cursor$L,db)", FridgeUtils.getObjectName(variableElement), FridgeUtils.getFieldCursorHelperClass(variableElement), i);
                } else {
                    fromCursorB.addStatement("$T objects$L = $T.get(cursor$L,db)", FridgeUtils.listOf(variableElement), i, FridgeUtils.getFieldCursorHelperClass(variableElement), i);
                    fromCursorB.addStatement("if(!objects$L.isEmpty()) object.$L = objects$L.get(0)", i, FridgeUtils.getObjectName(variableElement), i);
                }

                fromCursorB.addStatement("cursor$L.close()", i);
            }
        }

        fromCursorB.addCode("\n").addStatement("return object");

        MethodSpec.Builder getValuesB = MethodSpec.methodBuilder("getValues")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(Constants.contentValuesClassName)
                .addParameter(modelType, "object")
                .addParameter(ClassName.get(String.class), "name")
                .addStatement("$T values = new $T()", Constants.contentValuesClassName, Constants.contentValuesClassName)
                .addStatement("if(name != null) values.put($S,name)", Constants.FIELD_NAME);

        //for
        for (int i = 0; i < fields.size(); ++i) {
            VariableElement variableElement = fields.get(i);
            if (FridgeUtils.isPrimitive(variableElement)) {
                String statement = "values.put($S,object.$L)";
                if (FridgeUtils.isModelId(variableElement))
                    statement = "if(object." + Constants.FIELD_ID + " != 0) " + statement;
                getValuesB.addStatement(statement, variableElement.getSimpleName(), variableElement.getSimpleName());
            }
        }

        List<MethodSpec> joinMethods = new ArrayList<>();
        Set<String> addedMethodsNames = new HashSet<>();
        for (VariableElement variableElement : otherClassFields) {
            String JOIN_NAME = FridgeUtils.getTableName(objectName) + "_" + FridgeUtils.getTableName(variableElement);
            if (!addedMethodsNames.contains(JOIN_NAME)) {
                joinMethods.add(MethodSpec.methodBuilder("get" + JOIN_NAME + "Values")
                        .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                        .returns(Constants.contentValuesClassName)
                        .addParameter(TypeName.LONG, "objectId")
                        .addParameter(TypeName.LONG, "secondObjectId")
                        .addParameter(ClassName.get(String.class), "name")
                        .addStatement("$T values = new $T()", Constants.contentValuesClassName, Constants.contentValuesClassName)
                        .addStatement("values.put($S,objectId)", FridgeUtils.getKeyName(this.objectName))
                        .addStatement("values.put($S,secondObjectId)", FridgeUtils.getKeyName(variableElement))
                        .addStatement("values.put($S,name)", Constants.FIELD_NAME)
                        .addStatement("return values").build());
                addedMethodsNames.add(JOIN_NAME);
            }
        }

        getValuesB.addStatement("return values");

        MethodSpec get = MethodSpec.methodBuilder("get")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(FridgeUtils.listOf(modelType))
                .addParameter(Constants.cursorClassName, "cursor")
                .addParameter(Constants.databaseClassName, "db")
                .addStatement("$T objects = new $T()", FridgeUtils.listOf(modelType), FridgeUtils.arraylistOf(modelType))
                .addStatement("cursor.moveToFirst()")
                .addCode("while (!cursor.isAfterLast()) {\n")
                .addStatement("    $T object = fromCursor(cursor,db)", modelType)
                .addStatement("    objects.add(object)")
                .addStatement("    cursor.moveToNext()")
                .addCode("}\n")
                .addStatement("return objects")
                .build();

        return TypeSpec.classBuilder(FridgeUtils.getCursorHelperName(objectName))
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addMethod(fromCursorSimple.build())
                .addMethod(fromCursorB.build())
                .addMethod(getValuesB.build())
                .addMethod(get)
                .addMethods(joinMethods)
                .addMethods(generateInsertMethods())
                .build();

    }

    protected List<MethodSpec> generateInsertMethods() {
        List<MethodSpec> methodSpecs = new ArrayList<>();

        MethodSpec.Builder insertB = MethodSpec.methodBuilder("insert")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .addParameter(Constants.databaseClassName, "database")
                .addParameter(modelType, "object")
                .addStatement("object.$L = database.insert($S, null, getValues(object,null))", Constants.FIELD_ID, FridgeUtils.getTableName(objectName));

        for (VariableElement variableElement : otherClassFields) {
            insertB.addStatement("$T.insertFor$L(database,object.$L,object.$L, $S)", FridgeUtils.getFieldCursorHelperClass(variableElement), objectName, FridgeUtils.getObjectName(variableElement), Constants.FIELD_ID, FridgeUtils.getObjectName(variableElement));

            String JOINTABLE = FridgeUtils.getTableName(objectName) + "_" + FridgeUtils.getTableName(variableElement);

            MethodSpec insert = MethodSpec.methodBuilder("insertFor"+objectName)
                    .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                    .addParameter(Constants.databaseClassName, "database")
                    .addParameter(FridgeUtils.getFieldClass(variableElement), "child")
                    .addParameter(TypeName.LONG, "parentId")
                    .addParameter(ClassName.get(String.class), "variable")

                    .beginControlFlow("if(child != null)")
                    .addStatement("child._id = database.insert($S, null, $T.getValues(child,null))", FridgeUtils.getTableName(variableElement), FridgeUtils.getFieldCursorHelperClass(variableElement))
                    .addStatement("database.insert($S, null, get$LValues(parentId,child._id, variable))", JOINTABLE, JOINTABLE)
                    .endControlFlow()

                    .build();

            MethodSpec insertAll = MethodSpec.methodBuilder("insertFor"+objectName)
                    .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                    .addParameter(Constants.databaseClassName, "database")
                    .addParameter(FridgeUtils.listOf(FridgeUtils.getFieldClass(variableElement)), "objects")
                    .addParameter(TypeName.LONG, "parentId")
                    .addParameter(ClassName.get(String.class), "variable")

                    .beginControlFlow("if(objects != null)")
                    .beginControlFlow("for($T child : objects)", FridgeUtils.getFieldClass(variableElement))
                    .addStatement("insertFor$L(database,child, parentId, variable)",objectName)
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
                    .addStatement("values.put($S,objectId)", FridgeUtils.getKeyName(this.objectName))
                    .addStatement("values.put($S,secondObjectId)", FridgeUtils.getKeyName(variableElement))
                    .addStatement("values.put($S,name)", Constants.FIELD_NAME)
                    .addStatement("return values").build();

            dependencies.add(new Dependency(FridgeUtils.getFieldClass(variableElement), Arrays.asList(insert, insertAll, getTABLE_NAMEvalues)));

            //String JOINTABLE = TABLE_NAME + "_" + FridgeUtils.getTableName(variableElement);
            //if (!FridgeUtils.isCollection(variableElement)) {
            //    addB.addStatement("if(object.$L != null) object.$L._id = database.insert($S, null, $T.getValues(object.$L,$S))", FridgeUtils.getObjectName(variableElement), FridgeUtils.getObjectName(variableElement), FridgeUtils.getTableName(variableElement), FridgeUtils.getFieldCursorHelperClass(variableElement), FridgeUtils.getObjectName(variableElement), FridgeUtils.getObjectName(variableElement));
            //} else {
            //    addB.beginControlFlow("if(object.$L != null)", FridgeUtils.getObjectName(variableElement))
            //            .beginControlFlow("for($T child : object.$L)", FridgeUtils.getFieldClass(variableElement), FridgeUtils.getObjectName(variableElement))
            //            .addStatement("child._id = database.insert($S, null, $T.getValues(child,null))", FridgeUtils.getTableName(variableElement), FridgeUtils.getFieldCursorHelperClass(variableElement))
            //            .addStatement("database.insert($S, null, $T.get$LValues(object._id,child._id, $S))", JOINTABLE, modelCursorHelperClassName, JOINTABLE, FridgeUtils.getObjectName(variableElement))
            //            .endControlFlow()
            //            .endControlFlow();
            //}
        }

        methodSpecs.add(insertB.build());

        methodSpecs.add(MethodSpec.methodBuilder("insert")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .addParameter(Constants.databaseClassName, "database")
                .addParameter(FridgeUtils.listOf(modelType), "objects")
                .addStatement("for($T object : objects) insert(database,object)", modelType)
                .build());

        return methodSpecs;
    }

    public List getDependencies() {
        return dependencies;
    }
}

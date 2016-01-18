package com.github.florent37.dao.generator;

import com.github.florent37.dao.Constants;
import com.github.florent37.dao.FreezerUtils;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.util.ArrayList;
import java.util.List;

import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.VariableElement;

/**
 * Created by florentchampigny on 18/01/2016.
 */
public class CursorHelperGenerator {

    String objectName;
    TypeName modelType;
    List<VariableElement> fields;
    List<VariableElement> otherClassFields;

    public CursorHelperGenerator(Element element) {
        this.objectName = FreezerUtils.getObjectName(element);
        this.modelType = TypeName.get(element.asType());
        this.fields = FreezerUtils.getFields(element);
        this.otherClassFields = FreezerUtils.getNonPrimitiveClassFields(element);
    }

    public TypeSpec generate() {

        MethodSpec.Builder fromCursorSimple = MethodSpec.methodBuilder("fromCursor")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(modelType)
                .addParameter(Constants.cursorClassName, "cursor")
                .addStatement("return fromCursor(cursor,0)");

        MethodSpec.Builder fromCursorB = MethodSpec.methodBuilder("fromCursor")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(modelType)
                .addParameter(Constants.cursorClassName, "cursor")
                .addParameter(TypeName.INT, "start")
                .addStatement("$T object = new $T()", modelType, modelType);


        //for
        for (int i = 0; i < fields.size(); ++i) {
            VariableElement variableElement = fields.get(i);
            if (FreezerUtils.isPrimitive(variableElement)) {
                String cursor = "cursor.get$L(start + $L)";
                cursor = String.format(FreezerUtils.getFieldCast(variableElement), cursor);

                fromCursorB.addStatement("object.$L = " + cursor, variableElement.getSimpleName(), FreezerUtils.getFieldType(variableElement), i);
            } else {
                fromCursorB.addStatement("object.$L = $T.fromCursor(cursor,2+3); //+3 for _id & object_id & secondObject_id", variableElement.getSimpleName(), FreezerUtils.getFieldCursorHelperClass(variableElement));
            }
        }

        fromCursorB.addStatement("return object");

        MethodSpec.Builder getValuesB = MethodSpec.methodBuilder("getValues")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(Constants.contentValuesClassName)
                .addParameter(modelType, "object")
                .addStatement("$T values = new $T()", Constants.contentValuesClassName, Constants.contentValuesClassName);


        //for
        for (int i = 0; i < fields.size(); ++i) {
            VariableElement variableElement = fields.get(i);
            if (FreezerUtils.isPrimitive(variableElement)) {
                String statement = "values.put($S,object.$L)";
                if (FreezerUtils.isModelId(variableElement))
                    statement = "if(object."+Constants.FIELD_ID+" != 0) "+statement;
                getValuesB.addStatement(statement, variableElement.getSimpleName(), variableElement.getSimpleName());
            }
        }

        List<MethodSpec> joinMethods = new ArrayList<>();
        for(VariableElement variableElement : otherClassFields){
            String JOIN_NAME = FreezerUtils.getTableName(objectName)+"_"+FreezerUtils.getTableName(variableElement);
            joinMethods.add(MethodSpec.methodBuilder("get"+JOIN_NAME+"Values")
                    .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                    .returns(Constants.contentValuesClassName)
                    .addParameter(TypeName.LONG, "objectId")
                    .addParameter(TypeName.LONG, "secondObjectId")
                    .addStatement("$T values = new $T()", Constants.contentValuesClassName, Constants.contentValuesClassName)
                    .addStatement("values.put($S,objectId)",FreezerUtils.getKeyName(this.objectName))
                    .addStatement("values.put($S,secondObjectId)",FreezerUtils.getKeyName(variableElement))
                    .addStatement("return values").build());
        }

        getValuesB.addStatement("return values");

        MethodSpec get = MethodSpec.methodBuilder("get")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(FreezerUtils.listOf(modelType))
                .addParameter(Constants.cursorClassName, "cursor")
                .addStatement("$T objects = new $T()", FreezerUtils.listOf(modelType), FreezerUtils.arraylistOf(modelType))
                .addStatement("cursor.moveToFirst()")
                .addCode("while (!cursor.isAfterLast()) {\n")
                .addStatement("    $T object = fromCursor(cursor)", modelType)
                .addStatement("    objects.add(object)")
                .addStatement("    cursor.moveToNext()")
                .addCode("}\n")
                .addStatement("return objects")
                .build();

        return TypeSpec.classBuilder(FreezerUtils.getCursorHelperName(objectName))
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addMethod(fromCursorSimple.build())
                .addMethod(fromCursorB.build())
                .addMethod(getValuesB.build())
                .addMethod(get)
                .addMethods(joinMethods)
                .build();

    }

}

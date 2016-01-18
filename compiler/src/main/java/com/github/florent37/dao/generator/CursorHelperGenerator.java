package com.github.florent37.dao.generator;

import com.github.florent37.dao.Constants;
import com.github.florent37.dao.FreezerUtils;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

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

    public CursorHelperGenerator(Element element) {
        this.objectName = FreezerUtils.getObjectName(element);
        this.modelType = TypeName.get(element.asType());
        this.fields = FreezerUtils.getFields(element);
    }

    public TypeSpec generate() {
        MethodSpec.Builder fromCursorB = MethodSpec.methodBuilder("fromCursor")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(modelType)
                .addParameter(Constants.cursorClassName, "cursor")
                .addStatement("$T object = new $T()", modelType, modelType);

        //for
        for (int i = 0; i < fields.size(); ++i) {
            VariableElement variableElement = fields.get(i);
            if (FreezerUtils.isPrimitive(variableElement)) {
                String cursor = "cursor.get$L($L)";
                cursor = String.format(FreezerUtils.getFieldCast(variableElement), cursor);

                fromCursorB.addStatement("object.$L = " + cursor, variableElement.getSimpleName(), FreezerUtils.getFieldType(variableElement), i);
            } else {
                fromCursorB.addStatement("//$L", variableElement.getSimpleName());
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
            } else
                getValuesB.addStatement("//$L", variableElement.getSimpleName());
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
                .addMethod(fromCursorB.build())
                .addMethod(getValuesB.build())
                .addMethod(get)
                .build();

    }

}

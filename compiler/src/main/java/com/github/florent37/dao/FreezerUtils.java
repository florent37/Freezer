package com.github.florent37.dao;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;

import java.util.List;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.ElementFilter;

/**
 * Created by florentchampigny on 17/01/16.
 */
public class FreezerUtils {

    protected static List<VariableElement> getFields(Element element) {
        return ElementFilter.fieldsIn(element.getEnclosedElements());
    }

    protected static String getFieldType(VariableElement variableElement) {
        TypeName typeName = TypeName.get(variableElement.asType());
        if (typeName == TypeName.INT || typeName == TypeName.BOOLEAN || typeName == TypeName.LONG || typeName == TypeName.BYTE)
            return "Int";
        else if(typeName == TypeName.FLOAT)
            return "Float";
        else if (ClassName.get(String.class).equals(typeName))
            return "String";
        return "";
    }

    protected static String getFieldCast(VariableElement variableElement) {
        TypeName typeName = TypeName.get(variableElement.asType());
        if (typeName == TypeName.BOOLEAN || typeName == TypeName.LONG || typeName == TypeName.BYTE)
            return "(1 == %s)";
        return "%s";
    }

    protected static String getFieldTableType(VariableElement variableElement) {
        TypeName typeName = TypeName.get(variableElement.asType());
        if (typeName == TypeName.INT || typeName == TypeName.BOOLEAN || typeName == TypeName.LONG || typeName == TypeName.BYTE)
            return "integer";
        if (typeName == TypeName.FLOAT)
            return "real";
        else if (ClassName.get(String.class).equals(typeName))
            return "text";
        return "";
    }

    protected static String getObjectName(Element element) {
        return ((TypeElement) element).getSimpleName().toString();
    }

    protected static String getObjectPackage(Element element) {
        return ((TypeElement) element).getEnclosingElement().toString();
    }
}

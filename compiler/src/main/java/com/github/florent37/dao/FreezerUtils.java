package com.github.florent37.dao;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;

import java.util.List;

import javax.lang.model.element.Element;
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
        if (typeName == TypeName.INT)
            return "Int";
        else if (ClassName.get(String.class).equals(typeName))
            return "String";
        return "";
    }

    protected static String getFieldTableType(VariableElement variableElement) {
        TypeName typeName = TypeName.get(variableElement.asType());
        if (typeName == TypeName.INT)
            return "integer";
        else if (ClassName.get(String.class).equals(typeName))
            return "text";
        return "";
    }

}

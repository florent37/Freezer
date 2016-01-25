package com.github.florent37.orm;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.util.ArrayList;
import java.util.List;

import javax.lang.model.element.Element;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.ElementFilter;

/**
 * Created by florentchampigny on 17/01/16.
 */
public class ProcessUtils {

    public static List<VariableElement> getFields(Element element) {
        return ElementFilter.fieldsIn(element.getEnclosedElements());
    }

    public static List<VariableElement> getPrimitiveFields(Element element) {
        List<VariableElement> primitives = new ArrayList<>();
        for (VariableElement e : getFields(element)) {
            if (isPrimitive(e))
                primitives.add(e);
        }
        return primitives;
    }

    public static List<VariableElement> getNonPrimitiveClassFields(Element element) {
        List<VariableElement> nonPrimitive = new ArrayList<>();
        for (VariableElement e : getFields(element)) {
            if (!isPrimitive(e))
                nonPrimitive.add(e);
        }
        return nonPrimitive;
    }

    public static String getFieldType(VariableElement variableElement) {
        TypeName typeName = TypeName.get(variableElement.asType());
        if (typeName == TypeName.INT || typeName == TypeName.BOOLEAN || typeName == TypeName.LONG || typeName == TypeName.BYTE)
            return "Int";
        else if (typeName == TypeName.FLOAT)
            return "Float";
        else if (ClassName.get(String.class).equals(typeName))
            return "String";
        return "";
    }

    public static String getFieldCast(VariableElement variableElement) {
        TypeName typeName = TypeName.get(variableElement.asType());
        if (typeName == TypeName.BOOLEAN)
            return "(1 == %s)";
        return "%s";
    }

    public static String getFieldTableType(VariableElement variableElement) {
        TypeName typeName = TypeName.get(variableElement.asType());
        if (typeName == TypeName.INT || typeName == TypeName.BOOLEAN || typeName == TypeName.LONG || typeName == TypeName.BYTE)
            return "integer";
        if (typeName == TypeName.FLOAT)
            return "real";
        else if (ClassName.get(String.class).equals(typeName))
            return "text";
        return "";
    }

    public static String getObjectName(Element element) {
        return element.getSimpleName().toString();
    }

    public static String getObjectPackage(Element element) {
        return element.getEnclosingElement().toString();
    }

    public static TypeName getCursorHelper(Element element) {
        return ClassName.get(getObjectPackage(element), getCursorHelperName(getObjectName(element)));
    }

    public static String getCursorHelperName(String objectName) {
        return objectName + Constants.CURSOR_HELPER_SUFFIX;
    }

    public static String getQueryBuilderName(String modelName) {
        return modelName + Constants.QUERY_BUILDER_SUFFIX;
    }

    public static TypeName getQueryBuilder(Element element) {
        return ClassName.get(getObjectPackage(element), getObjectName(element) + Constants.QUERY_BUILDER_SUFFIX);
    }

    public static String getModelDaoName(Element element) {
        return getModelDaoName(getObjectName(element));
    }

    public static String getModelDaoName(String modelName) {
        return modelName + Constants.DAO_SUFFIX;
    }

    public static ClassName getModelDao(Element element) {
        return ClassName.get(getObjectPackage(element), getModelDaoName(element));
    }

    public static ParameterizedTypeName listOf(TypeName type) {
        return ParameterizedTypeName.get(ClassName.get(List.class), type);
    }

    public static ParameterizedTypeName listOf(Element element) {
        return ParameterizedTypeName.get(ClassName.get(List.class), ClassName.get(element.asType()));
    }

    public static ParameterizedTypeName listOf(Class classe) {
        return ParameterizedTypeName.get(ClassName.get(List.class), ClassName.get(classe));
    }

    public static ParameterizedTypeName arraylistOf(TypeName type) {
        return ParameterizedTypeName.get(ClassName.get(ArrayList.class), type);
    }

    public static ParameterizedTypeName arraylistOf(Class classe) {
        return ParameterizedTypeName.get(ClassName.get(ArrayList.class), ClassName.get(classe));
    }

    public static boolean isPrimitive(Element element) {
        return isPrimitive(TypeName.get(element.asType()));
    }

    public static boolean isModelId(VariableElement variableElement) {
        return Constants.FIELD_ID.equals(variableElement.getSimpleName().toString());
    }

    public static boolean isPrimitive(TypeName typeName) {
        return typeName.isPrimitive() || (ClassName.get(String.class).equals(typeName));
    }

    public static String getQueryCast(VariableElement variableElement) {
        TypeName typeName = TypeName.get(variableElement.asType());
        if (ClassName.get(String.class).equals(typeName))
            return "$L";
        else if (typeName == TypeName.BOOLEAN)
            return "String.valueOf($L ? 1 : 0)";
        else
            return "String.valueOf($L)";
    }

    public static String getKeyName(VariableElement variableElement) {
        return getKeyName(getFieldClassName(variableElement));
    }

    public static String getKeyName(String modelName) {
        return modelName.toLowerCase() + "_id";
    }

    public static String getTableName(String elementName) {
        return elementName.toUpperCase();
    }

    public static String getTableName(Element element) {
        return getTableName(getFieldClassName(element));
    }

    public static List<TypeName> getParameters(Element element) {
        try {
            return ((ParameterizedTypeName) ParameterizedTypeName.get(element.asType())).typeArguments;
        }catch (Exception e){
            return null;
        }
    }

    public static TypeName getEnclosedTypeName(Element element) {
        List<TypeName> parameters = getParameters(element);
        if (parameters == null || parameters.isEmpty()) return null;
        else return parameters.get(0);
    }

    public static TypeName getFieldCursorHelperClass(VariableElement element) {
        return ClassName.bestGuess(getFieldClass(element).toString() + Constants.CURSOR_HELPER_SUFFIX);
    }

    public static TypeName getFieldQueryBuilderClass(VariableElement element) {
        return ClassName.bestGuess(getFieldClass(element).toString() + Constants.QUERY_BUILDER_SUFFIX);
    }

    public static TypeName getFieldClass(Element element) {
        TypeName enclosed = getEnclosedTypeName(element);
        if(enclosed != null)
            return enclosed;
        else return ClassName.get(element.asType());
    }

    public static String getFieldClassName(Element element){
        String name;

        TypeName t = getFieldClass(element);
        if(t instanceof ClassName){
            ClassName className = (ClassName)t;
            name = className.simpleName();
        }else name = t.toString();

        return name;
    }

    public static boolean isCollection(Element element) {
        return getEnclosedTypeName(element) != null;
    }

    public static String getMethodId(MethodSpec methodSpec){
        return methodSpec.name+methodSpec.parameters.toString();
    }

    protected static List<String> getMethodsNames(TypeSpec typeSpec) {
        List<String> names = new ArrayList<>();
        for (MethodSpec methodSpec : typeSpec.methodSpecs)
            names.add(methodSpec.name + methodSpec.parameters.toString());
        return names;
    }

    public static TypeName getElementEnumColumn(Element element) {
        return ClassName.bestGuess(getFieldClass(element).toString() + Constants.ENUM_COLUMN_SUFFIX);
    }
}

package com.github.florent37.rxandroidorm;

import com.github.florent37.rxandroidorm.annotations.Id;
import com.github.florent37.rxandroidorm.annotations.Ignore;
import com.squareup.javapoet.ArrayTypeName;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.ElementFilter;

/**
 * Created by florentchampigny on 17/01/16.
 */
public class ProcessUtils {

    public static List<VariableElement> filterIgnore(List<VariableElement> elements) {
        List<VariableElement> filtered = new ArrayList<>();
        for (VariableElement variableElement : elements) {
            if (variableElement.getAnnotation(Ignore.class) == null && !Constants.PARCEL_CREATOR.equals(
                ProcessUtils.getObjectName(variableElement)) || ProcessUtils.isNotVariable(variableElement)) {
                filtered.add(variableElement);
            }
        }
        return filterStaticFinal(filtered);
    }

    private static boolean isNotVariable(VariableElement variableElement) {
        return variableElement.getKind() == ElementKind.ENUM ||
                variableElement.getKind() == ElementKind.INTERFACE ||
                variableElement.getKind() == ElementKind.CLASS;
    }

    public static List<VariableElement> filterStaticFinal(List<VariableElement> elements) {
        List<VariableElement> filtered = new ArrayList<>();
        for (VariableElement variableElement : elements) {
            final Set<Modifier> modifiers = variableElement.getModifiers();
            if (!modifiers.containsAll(Arrays.asList(Modifier.FINAL, Modifier.STATIC))) {
                filtered.add(variableElement);
            }
        } return filtered;
    }

    public static List<VariableElement> getFields(Element element) {
        return filterIgnore(ElementFilter.fieldsIn(element.getEnclosedElements()));
    }

    public static List<VariableElement> getPrimitiveFields(Element element) {
        List<VariableElement> primitives = new ArrayList<>();
        for (VariableElement e : getFields(element)) {
            if (isPrimitive(e) && !isCollectionOfPrimitive(e) && !isNotVariable(e)) {
                primitives.add(e);
            }
        }
        return filterIgnore(primitives);
    }

    public static List<VariableElement> getCollectionsOfPrimitiveFields(Element element) {
        List<VariableElement> collectionsOfPrimitives = new ArrayList<>();
        for (VariableElement e : getFields(element)) {
            if (isCollectionOfPrimitive(e) && !isNotVariable(e)) {
                collectionsOfPrimitives.add(e);
            }
        }
        return filterIgnore(collectionsOfPrimitives);
    }

    public static List<VariableElement> getNonPrimitiveClassFields(Element element) {
        List<VariableElement> nonPrimitive = new ArrayList<>();
        for (VariableElement e : getFields(element)) {
            if (!isPrimitive(e) && !isCollectionOfPrimitive(e) && !isNotVariable(e)) {
                nonPrimitive.add(e);
            }
        }
        return filterIgnore(nonPrimitive);
    }

    public static boolean hasIdField(Element element) {
        return getIdField(element) != null;
    }

    public static Element getIdField(Element element) {
        for (VariableElement e : getFields(element)) {
            if (isIdField(e)) {
                return e;
            }
        }
        return null;
    }

    public static String getFieldType(VariableElement variableElement) {
        TypeName typeName = getFieldClass(variableElement);
        if (typeName == TypeName.INT || typeName == TypeName.BOOLEAN || typeName == TypeName.BYTE) {
            return "Int";
        } else if (typeName == TypeName.LONG) {
            return "Long";
        } else if (typeName == TypeName.FLOAT) {
            return "Float";
        } else if (typeName == TypeName.DOUBLE) {
            return "Double";
        } else if (ClassName.get(String.class).equals(typeName)) {
            return "String";
        }
        return "";
    }

    public static String getFieldCast(VariableElement variableElement) {
        TypeName typeName = getFieldClass(variableElement);
        if (typeName == TypeName.BOOLEAN) {
            return "(1 == %s)";
        }
        return "%s";
    }

    public static String getFieldTableType(Element variableElement) {
        TypeName typeName = getFieldClass(variableElement);
        if (typeName == TypeName.INT
            || typeName == TypeName.BOOLEAN
            || typeName == TypeName.LONG
            || typeName == TypeName.BYTE) {
            return "integer";
        }
        if (typeName == TypeName.FLOAT) {
            return "real";
        } else if (ClassName.get(String.class).equals(typeName) || isDate(typeName)) {
            return "text";
        }
        return null;
    }

    public static String getObjectName(Element element) {
        return element.getSimpleName().toString();
    }

    public static boolean isIdField(Element element) {
        return element.getAnnotation(Id.class) != null && TypeName.LONG.equals(TypeName.get(element.asType()));
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
        return ParameterizedTypeName.get(ClassName.get(List.class), getFieldClass(element));
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

    public static TypeName unbox(TypeName typeName) {
        try {
            return typeName.unbox();
        } catch (Exception e) {
            return typeName;
        }
    }

    public static boolean isPrimitive(TypeName typeName) {
        return typeName.isPrimitive()
            || unbox(typeName).isPrimitive()
            || (ClassName.get(String.class).equals(typeName))
            || isDate(typeName);
    }

    public static boolean isCollectionOfPrimitive(Element element) {
        return isCollection(element) && isPrimitive(getFieldClass(element));
    }

    public static String getQueryCast(VariableElement variableElement) {
        TypeName typeName = getFieldClass(variableElement);
        if (ClassName.get(String.class).equals(typeName)) {
            return "$L";
        } else if (typeName == TypeName.BOOLEAN || typeName.equals(TypeName.get(Boolean.class))) {
            return "String.valueOf($L ? 1 : 0)";
        } else {
            return "String.valueOf($L)";
        }
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
        } catch (Exception e) {
            return null;
        }
    }

    public static TypeName getEnclosedTypeName(Element element) {
        List<TypeName> parameters = getParameters(element);
        if (parameters == null || parameters.isEmpty()) {
            return null;
        } else {
            return parameters.get(0);
        }
    }

    public static TypeName getFieldCursorHelperClass(VariableElement element) {
        return ClassName.bestGuess(getFieldClass(element).toString() + Constants.CURSOR_HELPER_SUFFIX);
    }

    public static TypeName getFieldQueryBuilderClass(VariableElement element) {
        return ClassName.bestGuess(getFieldClass(element).toString() + Constants.QUERY_BUILDER_SUFFIX);
    }

    public static TypeName getFieldClass(Element element) {
        if (isArray(element)) {
            TypeName typeName = getArrayEnclosedType(element);
            return unbox(typeName);
        } else {
            TypeName enclosed = unbox(getEnclosedTypeName(element));
            if (enclosed != null) {
                return enclosed;
            } else {
                return unbox(TypeName.get(element.asType()));
            }
        }
    }

    public static String getFieldClassName(Element element) {
        String name;

        TypeName t = getFieldClass(element);
        if (t instanceof ClassName) {
            ClassName className = (ClassName) t;
            name = className.simpleName();
        } else {
            name = t.toString();
        }

        return name;
    }

    public static boolean isArray(Element element) {
        try {
            return getArrayEnclosedType(element) != null;
        } catch (Exception e) {
            return false;
        }
    }

    public static TypeName getArrayEnclosedType(Element element) {
        return ((ArrayTypeName) ArrayTypeName.get(element.asType())).componentType;
    }

    public static boolean isCollection(Element element) {
        return isArray(element) || getEnclosedTypeName(element) != null;
    }

    public static String getMethodId(MethodSpec methodSpec) {
        return methodSpec.name + methodSpec.parameters.toString();
    }

    public static TypeName getElementEnumColumn(Element element) {
        return ClassName.bestGuess(getFieldClass(element).toString() + Constants.ENUM_COLUMN_SUFFIX);
    }

    public static String getModelId(Element element, String elementVarialbe, String idVariableName) {
        StringBuilder stringBuilder = new StringBuilder();
        Element idField = getIdField(element);
        if (idField != null) {
            stringBuilder.append("java.lang.Long ")
                .append(idVariableName)
                .append(" = ")
                .append(elementVarialbe)
                .append(".")
                .append(getObjectName(idField));
        } else {
            stringBuilder.append("java.lang.Long ").append(idVariableName).append(" = ");
            stringBuilder.append(elementVarialbe)
                .append(" instanceof ")
                .append(Constants.entityProxyClassString)
                .append(" ? ");
            stringBuilder.append(
                String.format("((%s.%s)%s).%s()", Constants.DAO_PACKAGE, Constants.MODEL_ENTITY_PROXY_INTERFACE,
                    elementVarialbe, Constants.MODEL_ENTITY_PROXY_GET_ID_METHOD));
            stringBuilder.append(": null");
        }
        return stringBuilder.toString();
    }

    public static String setModelId(String variable) {
        return String.format("((%s.%s)%s).%s", Constants.DAO_PACKAGE, Constants.MODEL_ENTITY_PROXY_INTERFACE, variable,
            Constants.MODEL_ENTITY_PROXY_SET_ID_METHOD);
    }

    public static TypeName getModelProxy(Element element) {
        return ClassName.get(getObjectPackage(element), getObjectName(element) + Constants.MODEL_ENTITY_PROXY);
    }

    public static String getPrimitiveCursorHelperFunction(Element element) {
        TypeName typeName = getFieldClass(element);
        if (isArray(element)) {
            if (getArrayEnclosedType(element).isPrimitive()) {
                if (ClassName.get(String.class).equals(typeName)) {
                    return "getStringsPrimitiveArray";
                } else if (TypeName.INT.equals(typeName)) {
                    return "getIntegersPrimitiveArray";
                } else if (TypeName.LONG.equals(typeName)) {
                    return "getLongsPrimitiveArray";
                } else if (TypeName.FLOAT.equals(typeName)) {
                    return "getFloatsPrimitiveArray";
                } else if (TypeName.DOUBLE.equals(typeName)) {
                    return "getDoublesPrimitiveArray";
                } else if (TypeName.BOOLEAN.equals(typeName)) {
                    return "getBooleansPrimitiveArray";
                }
            } else {
                if (ClassName.get(String.class).equals(typeName)) {
                    return "getStringsArray";
                } else if (TypeName.INT.equals(typeName)) {
                    return "getIntegersArray";
                } else if (TypeName.LONG.equals(typeName)) {
                    return "getLongsArray";
                } else if (TypeName.FLOAT.equals(typeName)) {
                    return "getFloatsArray";
                } else if (TypeName.DOUBLE.equals(typeName)) {
                    return "getDoublesArray";
                } else if (TypeName.BOOLEAN.equals(typeName)) {
                    return "getBooleansArray";
                }
            }
        } else {
            if (ClassName.get(String.class).equals(typeName)) {
                return "getStrings";
            } else if (TypeName.INT.equals(typeName)) {
                return "getIntegers";
            } else if (TypeName.LONG.equals(typeName)) {
                return "getLongs";
            } else if (TypeName.FLOAT.equals(typeName)) {
                return "getFloats";
            } else if (TypeName.DOUBLE.equals(typeName)) {
                return "getDoubles";
            } else if (TypeName.BOOLEAN.equals(typeName)) {
                return "getBooleans";
            }
        }
        return null;
    }

    public static String addPrimitiveCursorHelperFunction(Element element) {
        TypeName typeName = getFieldClass(element);
        if (ClassName.get(String.class).equals(typeName)) {
            return "addStrings";
        } else if (TypeName.INT.equals(typeName)) {
            return "addIntegers";
        } else if (TypeName.LONG.equals(typeName)) {
            return "addLongs";
        } else if (TypeName.FLOAT.equals(typeName)) {
            return "addFloats";
        } else if (TypeName.DOUBLE.equals(typeName)) {
            return "addDoubles";
        } else if (TypeName.BOOLEAN.equals(typeName)) {
            return "addBooleans";
        }
        return null;
    }

    public static ClassName getSelectorName(Element element) {
        TypeName typeName = getFieldClass(element);
        if (isCollection(element)) {
            if (TypeName.INT.equals(typeName) || TypeName.LONG.equals(typeName) || TypeName.FLOAT.equals(typeName)) {
                return Constants.queryBuilder_ListNumberSelectorClassName;
            }
            if (TypeName.BOOLEAN.equals(typeName)) {
                return Constants.queryBuilder_ListBooleanSelectorClassName;
            }
            if (TypeName.get(String.class).equals(typeName)) {
                return Constants.queryBuilder_ListStringSelectorClassName;
            }
        } else {
            if (TypeName.INT.equals(typeName) || TypeName.LONG.equals(typeName) || TypeName.FLOAT.equals(typeName)) {
                return Constants.queryBuilder_NumberSelectorClassName;
            }
            if (TypeName.BOOLEAN.equals(typeName)) {
                return Constants.queryBuilder_BooleanSelectorClassName;
            }
            if (TypeName.get(String.class).equals(typeName)) {
                return Constants.queryBuilder_StringSelectorClassName;
            }
            if (isDate(typeName)) {
                return Constants.queryBuilder_DateSelectorClassName;
            }
        }
        return null;
    }

    public static TypeName getUnboxedClass(Element element) {
        TypeName typeName = getFieldClass(element);
        if (TypeName.INT.equals(typeName)) {
            return TypeName.get(Integer.class);
        }
        if (TypeName.LONG.equals(typeName)) {
            return TypeName.get(Long.class);
        }
        if (TypeName.FLOAT.equals(typeName)) {
            return TypeName.get(Float.class);
        }
        if (TypeName.DOUBLE.equals(typeName)) {
            return TypeName.get(Double.class);
        }
        if (TypeName.BOOLEAN.equals(typeName)) {
            return TypeName.get(Boolean.class);
        }
        if (TypeName.get(String.class).equals(typeName)) {
            return typeName;
        }
        return null;
    }

    public static boolean isDate(Element element) {
        return isDate(getFieldClass(element));
    }

    public static boolean isDate(TypeName typeName) {
        return Constants.dateClassName.equals(typeName);
    }

    protected static List<String> getMethodsNames(TypeSpec typeSpec) {
        List<String> names = new ArrayList<>();
        for (MethodSpec methodSpec : typeSpec.methodSpecs) {
            names.add(methodSpec.name + methodSpec.parameters.toString());
        }
        return names;
    }

    public static TypeName observableOf(TypeName typeName){
        return ParameterizedTypeName.get(Constants.RX_OBSERVABLE, typeName);
    }

    public static TypeName observableSourceOf(TypeName typeName){
        return ParameterizedTypeName.get(Constants.RX_OBSERVABLE_SOURCE, typeName);
    }

    public static TypeName functionOf(TypeName...typeName){
        return ParameterizedTypeName.get(Constants.RX_FUNCTION, typeName);
    }
}

package fr.xebia.android.freezer.generator;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeVariableName;
import fr.xebia.android.freezer.Constants;
import fr.xebia.android.freezer.ProcessUtils;

import java.util.ArrayList;
import java.util.List;

import javax.lang.model.element.Modifier;

/**
 * Created by florentchampigny on 26/01/2016.
 */
public class QueryBuilderGenerator {

    public TypeSpec generate() {
        return TypeSpec.classBuilder(Constants.QUERY_BUILDER_SUFFIX)
                .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)

                .addField(ClassName.get(StringBuilder.class), "queryBuilder", Modifier.PROTECTED)
                .addField(ClassName.get(StringBuilder.class), "orderBuilder", Modifier.PROTECTED)
                .addField(ProcessUtils.listOf(String.class), "args", Modifier.PROTECTED)
                .addField(ProcessUtils.listOf(String.class), "fromTables", Modifier.PROTECTED)
                .addField(ProcessUtils.listOf(String.class), "fromTablesNames", Modifier.PROTECTED)
                .addField(ProcessUtils.listOf(String.class), "fromTablesId", Modifier.PROTECTED)
                .addField(TypeName.BOOLEAN, "named", Modifier.PROTECTED)
                .addField(ClassName.get(Constants.DAO_PACKAGE, Constants.QUERY_LOGGER), "logger", Modifier.PROTECTED)

                .addMethod(MethodSpec.constructorBuilder()
                        .addModifiers(Modifier.PUBLIC)
                        .addStatement("this.queryBuilder = new $T()", ClassName.get(StringBuilder.class))
                        .addStatement("this.orderBuilder = new $T()", ClassName.get(StringBuilder.class))
                        .addStatement("this.args = new $T()", ProcessUtils.arraylistOf(String.class))
                        .addStatement("this.fromTables = new $T()", ProcessUtils.arraylistOf(String.class))
                        .addStatement("this.fromTablesNames = new $T()", ProcessUtils.arraylistOf(String.class))
                        .addStatement("this.fromTablesId = new $T()", ProcessUtils.arraylistOf(String.class))
                        .build())

                .addMethod(MethodSpec.constructorBuilder()
                        .addModifiers(Modifier.PUBLIC)
                        .addParameter(TypeName.BOOLEAN, "named")
                        .addParameter(ClassName.get(Constants.DAO_PACKAGE, Constants.QUERY_LOGGER), "logger")
                        .addStatement("this()")
                        .addStatement("this.named = named")
                        .addStatement("this.logger = logger")
                        .build())

                .addMethod(MethodSpec.methodBuilder("appendOr")
                        .addModifiers(Modifier.PROTECTED)
                        .addStatement("queryBuilder.append($S)", " or ")
                        .build())

                .addMethod(MethodSpec.methodBuilder("appendAnd")
                        .addModifiers(Modifier.PROTECTED)
                        .addStatement("queryBuilder.append($S)", " and ")
                        .build())

                .addMethod(MethodSpec.methodBuilder("appendBeginGroup")
                        .addModifiers(Modifier.PROTECTED)
                        .addStatement("queryBuilder.append($S)", " ( ")
                        .build())

                .addMethod(MethodSpec.methodBuilder("appendEndGroup")
                        .addModifiers(Modifier.PROTECTED)
                        .addStatement("queryBuilder.append($S)", " ) ")
                        .build())

                .addMethod(MethodSpec.methodBuilder("appendSortAsc")
                        .addModifiers(Modifier.PROTECTED)
                        .addParameter(ClassName.get(String.class), "column")
                        .addParameter(ClassName.get(String.class), "tableName")
                        .addStatement("if(orderBuilder.length() != 0) queryBuilder.append(',')")
                        .addStatement("orderBuilder.append(tableName).append(column)")
                        .addStatement("orderBuilder.append($S)", " ASC ")
                        .build())

                .addMethod(MethodSpec.methodBuilder("appendSortDesc")
                        .addModifiers(Modifier.PROTECTED)
                        .addParameter(ClassName.get(String.class), "column")
                        .addParameter(ClassName.get(String.class), "tableName")
                        .addStatement("if(orderBuilder.length() != 0) queryBuilder.append(',')")
                        .addStatement("orderBuilder.append(tableName).append(column)")
                        .addStatement("orderBuilder.append($S)", " DESC ")
                        .build())

                .addMethod(MethodSpec.methodBuilder("query")
                        .returns(TypeName.get(String.class))
                        .addModifiers(Modifier.PUBLIC)
                        .addParameter(TypeName.get(String.class), "fromTable")
                        .addParameter(TypeName.get(String.class), "joinTable")
                        .addParameter(TypeName.get(String.class), "joinIdFrom")
                        .addParameter(TypeName.get(String.class), "joinIdTo")
                        .addParameter(TypeName.get(String.class), "table")
                        .addParameter(TypeName.get(String.class), "variable")
                        .addParameter(ProcessUtils.listOf(String.class), "args")
                        .addStatement("args.addAll(this.args)")
                        .addStatement("queryBuilder.append(\" AND \").append(joinTable).append(\".\").append(joinIdFrom).append(\"  = \").append(fromTable).append(\".$L\")", Constants.FIELD_ID)
                        .addStatement("queryBuilder.append(\" AND \").append(joinTable).append(\".\").append(joinIdTo).append(\"  = \").append(table).append(\".$L\")", Constants.FIELD_ID)
                        .addStatement("queryBuilder.append(\" AND \").append(joinTable).append(\".$L  = '\").append(variable).append(\"'\")", Constants.FIELD_NAME)
                        .addStatement("return queryBuilder.toString().replace($S,table)", Constants.QUERY_NAMED)
                        .build())

                .addMethod(MethodSpec.methodBuilder("constructArgs")
                        .returns(TypeName.get(String[].class))
                        .addModifiers(Modifier.PROTECTED)
                        .addStatement("return args.toArray(new String[args.size()])")
                        .build())

                .addMethod(MethodSpec.methodBuilder("constructQuery")
                        .returns(TypeName.get(String.class))
                        .addModifiers(Modifier.PUBLIC)
                        .addStatement("$T query = new $T()", ClassName.get(StringBuilder.class), ClassName.get(StringBuilder.class))
                        .addStatement("for($T s : fromTables) query.append($S).append(s)", ClassName.get(String.class), ", ")
                        .addStatement("if (queryBuilder.length() != 0) query.append($S)", " where ")
                        .addStatement("query.append(queryBuilder.toString())")
                        .addStatement("if(orderBuilder.length() != 0) query.append($S)", " ORDER BY ")
                        .addStatement("query.append(orderBuilder.toString())")
                        .addStatement("return query.toString()")
                        .build())

                .addMethod(MethodSpec.methodBuilder("appendQuery")
                        .addModifiers(Modifier.PROTECTED)
                        .addParameter(TypeName.get(String.class), "conditional")
                        .addParameter(TypeName.get(String.class), "arg")
                        .addStatement("if (named) queryBuilder.append($S)", "NAMED.")
                        .addStatement("queryBuilder.append(conditional)")
                        .addStatement("if(arg != null) args.add(arg)")
                        .build())

                .addMethod(MethodSpec.methodBuilder("getTableId")
                        .addModifiers(Modifier.PROTECTED)
                        .returns(ClassName.get(String.class))
                        .addParameter(ClassName.get(String.class), "tableName")
                        .addStatement("$T tableId", ClassName.get(String.class))
                        .addStatement("int tablePos = fromTablesNames.indexOf(tableName)")
                        .addStatement("if(tablePos != -1) tableId = fromTablesId.get(tablePos)")
                        .addStatement("else{ tableId = $S + fromTables.size(); fromTablesId.add(tableId); fromTables.add(tableName + \" \" + tableId); fromTablesNames.add(tableName); }", Constants.QUERY_TABLE_VARIABLE)
                        .addStatement("return tableId")

                        .build())

                .addTypes(generateSelectors())

                .build();

    }

    protected List<TypeSpec> generateSelectors() {
        List<TypeSpec> typeSpecs = new ArrayList<>();

        typeSpecs.add(TypeSpec.classBuilder(Constants.SELECTOR_NUMBER)
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .addTypeVariable(TypeVariableName.get("Q1", Constants.queryBuilderClassName))
                .addField(TypeVariableName.get("Q1"), "queryBuilder", Modifier.PROTECTED)
                .addField(TypeName.get(String.class), "column", Modifier.PROTECTED)

                .addMethod(MethodSpec.constructorBuilder()
                        .addModifiers(Modifier.PUBLIC)
                        .addParameter(TypeVariableName.get("Q1"), "queryBuilder")
                        .addParameter(TypeName.get(String.class), "column")
                        .addStatement("this.queryBuilder = queryBuilder")
                        .addStatement("this.column = column")
                        .build())

                .addMethod(MethodSpec.methodBuilder("equalsTo")
                        .addModifiers(Modifier.PUBLIC)
                        .returns(TypeVariableName.get("Q1"))
                        .addParameter(TypeName.FLOAT, "value")
                        .addStatement("queryBuilder.appendQuery(column+\" = ?\",String.valueOf(value))")
                        .addStatement("return queryBuilder")
                        .build())

                .addMethod(MethodSpec.methodBuilder("notEqualsTo")
                        .addModifiers(Modifier.PUBLIC)
                        .returns(TypeVariableName.get("Q1"))
                        .addParameter(TypeName.FLOAT, "value")
                        .addStatement("queryBuilder.appendQuery(column+\" != ?\",String.valueOf(value))")
                        .addStatement("return queryBuilder")
                        .build())

                .addMethod(MethodSpec.methodBuilder("between")
                        .addModifiers(Modifier.PUBLIC)
                        .returns(TypeVariableName.get("Q1"))
                        .addParameter(TypeName.FLOAT, "min")
                        .addParameter(TypeName.FLOAT, "max")
                        .addStatement("queryBuilder.appendQuery(column+\" > ?\",String.valueOf(min))")
                        .addStatement("queryBuilder.appendAnd()")
                        .addStatement("queryBuilder.appendQuery(column+\" < ?\",String.valueOf(max))")
                        .addStatement("return queryBuilder")
                        .build())

                .addMethod(MethodSpec.methodBuilder("greatherThan")
                        .addModifiers(Modifier.PUBLIC)
                        .returns(TypeVariableName.get("Q1"))
                        .addParameter(TypeName.FLOAT, "value")
                        .addStatement("queryBuilder.appendQuery(column+\" > ?\",String.valueOf(value))")
                        .addStatement("return queryBuilder")
                        .build())

                .addMethod(MethodSpec.methodBuilder("lessThan")
                        .addModifiers(Modifier.PUBLIC)
                        .returns(TypeVariableName.get("Q1"))
                        .addParameter(TypeName.FLOAT, "value")
                        .addStatement("queryBuilder.appendQuery(column+\" < ?\",String.valueOf(value))")
                        .addStatement("return queryBuilder")
                        .build())

                .build());

        typeSpecs.add(TypeSpec.classBuilder(Constants.SELECTOR_NUMBER_LIST)
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .addTypeVariable(TypeVariableName.get("P1", Constants.queryBuilderClassName))
                .addField(TypeVariableName.get("P1"), "queryBuilder", Modifier.PROTECTED)
                .addField(TypeName.get(String.class), "column", Modifier.PROTECTED)

                .addMethod(MethodSpec.constructorBuilder()
                        .addModifiers(Modifier.PUBLIC)
                        .addParameter(TypeVariableName.get("P1"), "queryBuilder")
                        .addParameter(TypeName.get(String.class), "column")
                        .addStatement("this.queryBuilder = queryBuilder")
                        .addStatement("this.column = column")
                        .build())

                .addMethod(MethodSpec.methodBuilder("contains")
                        .addModifiers(Modifier.PUBLIC)
                        .returns(ParameterizedTypeName.get(Constants.queryBuilder_NumberSelectorClassName, TypeVariableName.get("P1")))
                        .addStatement("return new $L<$L>(queryBuilder,column)", Constants.SELECTOR_NUMBER, "P1")
                        .build())

                .build());

        typeSpecs.add(TypeSpec.classBuilder(Constants.SELECTOR_STRING)
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .addTypeVariable(TypeVariableName.get("Q2", Constants.queryBuilderClassName))
                .addField(TypeVariableName.get("Q2"), "queryBuilder", Modifier.PROTECTED)
                .addField(TypeName.get(String.class), "column",  Modifier.PROTECTED)

                .addMethod(MethodSpec.constructorBuilder()
                        .addModifiers(Modifier.PUBLIC)
                        .addParameter(TypeVariableName.get("Q2"), "queryBuilder")
                        .addParameter(TypeName.get(String.class), "column")
                        .addStatement("this.queryBuilder = queryBuilder")
                        .addStatement("this.column = column")
                        .build())

                .addMethod(MethodSpec.methodBuilder("equalsTo")
                        .addModifiers(Modifier.PUBLIC)
                        .returns(TypeVariableName.get("Q2"))
                        .addParameter(TypeName.get(String.class), "value")
                        .addStatement("queryBuilder.appendQuery(column+\" = ?\",value)")
                        .addStatement("return queryBuilder")
                        .build())

                .addMethod(MethodSpec.methodBuilder("notEqualsTo")
                        .addModifiers(Modifier.PUBLIC)
                        .returns(TypeVariableName.get("Q2"))
                        .addParameter(TypeName.get(String.class), "value")
                        .addStatement("queryBuilder.appendQuery(column+\" != ?\",value)")
                        .addStatement("return queryBuilder")
                        .build())

                .addMethod(MethodSpec.methodBuilder("contains")
                        .addModifiers(Modifier.PUBLIC)
                        .returns(TypeVariableName.get("Q2"))
                        .addParameter(TypeName.get(String.class), "value")
                        .addStatement("queryBuilder.appendQuery(column+\" LIKE '%\"+value+\"%'\",null)")
                        .addStatement("return queryBuilder")
                        .build())

                .build());

        typeSpecs.add(TypeSpec.classBuilder(Constants.SELECTOR_STRING_LIST)
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .addTypeVariable(TypeVariableName.get("P2", Constants.queryBuilderClassName))
                .addField(TypeVariableName.get("P2"), "queryBuilder", Modifier.PROTECTED)
                .addField(TypeName.get(String.class), "column", Modifier.PROTECTED)

                .addMethod(MethodSpec.constructorBuilder()
                        .addModifiers(Modifier.PUBLIC)
                        .addParameter(TypeVariableName.get("P2"), "queryBuilder")
                        .addParameter(TypeName.get(String.class), "column")
                        .addStatement("this.queryBuilder = queryBuilder")
                        .addStatement("this.column = column")
                        .build())

                .addMethod(MethodSpec.methodBuilder("contains")
                        .addModifiers(Modifier.PUBLIC)
                        .returns(ParameterizedTypeName.get(Constants.queryBuilder_StringSelectorClassName, TypeVariableName.get("P2")))
                        .addStatement("return new $L<$L>(queryBuilder,column)", Constants.SELECTOR_STRING, "P2")
                        .build())

                .build());

        typeSpecs.add(TypeSpec.classBuilder(Constants.SELECTOR_BOOLEAN)
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .addTypeVariable(TypeVariableName.get("Q3", Constants.queryBuilderClassName))
                .addField(TypeVariableName.get("Q3"), "queryBuilder", Modifier.PROTECTED)
                .addField(TypeName.get(String.class), "column", Modifier.PROTECTED)

                .addMethod(MethodSpec.constructorBuilder()
                        .addModifiers(Modifier.PUBLIC)
                        .addParameter(TypeVariableName.get("Q3"), "queryBuilder")
                        .addParameter(TypeName.get(String.class), "column")
                        .addStatement("this.queryBuilder = queryBuilder")
                        .addStatement("this.column = column")
                        .build())

                .addMethod(MethodSpec.methodBuilder("equalsTo")
                        .addModifiers(Modifier.PUBLIC)
                        .returns(TypeVariableName.get("Q3"))
                        .addParameter(TypeName.BOOLEAN, "value")
                        .addStatement("queryBuilder.appendQuery(column+\" = ?\", String.valueOf(value ? 1 : 0))")
                        .addStatement("return queryBuilder")
                        .build())

                .addMethod(MethodSpec.methodBuilder("isTrue")
                        .addModifiers(Modifier.PUBLIC)
                        .returns(TypeVariableName.get("Q3"))
                        .addStatement("queryBuilder.appendQuery(column+\" = ?\", String.valueOf(1))")
                        .addStatement("return queryBuilder")
                        .build())

                .addMethod(MethodSpec.methodBuilder("isFalse")
                        .addModifiers(Modifier.PUBLIC)
                        .returns(TypeVariableName.get("Q3"))
                        .addStatement("queryBuilder.appendQuery(column+\" = ?\", String.valueOf(0))")
                        .addStatement("return queryBuilder")
                        .build())

                .build());

        typeSpecs.add(TypeSpec.classBuilder(Constants.SELECTOR_BOOLEAN_LIST)
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .addTypeVariable(TypeVariableName.get("P3", Constants.queryBuilderClassName))
                .addField(TypeVariableName.get("P3"), "queryBuilder", Modifier.PROTECTED)
                .addField(TypeName.get(String.class), "column", Modifier.PROTECTED)

                .addMethod(MethodSpec.constructorBuilder()
                        .addModifiers(Modifier.PUBLIC)
                        .addParameter(TypeVariableName.get("P3"), "queryBuilder")
                        .addParameter(TypeName.get(String.class), "column")
                        .addStatement("this.queryBuilder = queryBuilder")
                        .addStatement("this.column = column")
                        .build())

                .addMethod(MethodSpec.methodBuilder("contains")
                        .addModifiers(Modifier.PUBLIC)
                        .returns(ParameterizedTypeName.get(Constants.queryBuilder_BooleanSelectorClassName, TypeVariableName.get("P3")))
                        .addStatement("return new $L<$L>(queryBuilder,column)", Constants.SELECTOR_BOOLEAN, "P3")
                        .build())

                .build());

        return typeSpecs;
    }
}

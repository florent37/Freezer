package com.github.florent37.dao.generator;

import com.github.florent37.dao.Constants;
import com.github.florent37.dao.ProcessUtils;
import com.squareup.javapoet.ArrayTypeName;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.VariableElement;

/**
 * Created by florentchampigny on 08/01/2016.
 */
public class ModelDaoGenerator {

    String modelName;
    String modelPackage;
    TypeName modelClassName;

    TypeName modelCursorHelperClassName;

    TypeName queryBuilderClassName;

    TypeName enumColums;

    String TABLE_NAME;

    TypeSpec queryBuilder;
    TypeSpec dao;

    List<VariableElement> fields;
    List<VariableElement> otherClassFields;

    public ModelDaoGenerator(Element element) {
        this.modelName = ProcessUtils.getObjectName(element);
        this.modelPackage = ProcessUtils.getObjectPackage(element);

        this.modelClassName = TypeName.get(element.asType());
        this.modelCursorHelperClassName = ProcessUtils.getCursorHelper(element);

        this.TABLE_NAME = ProcessUtils.getTableName(element);

        this.queryBuilderClassName = ProcessUtils.getQueryBuilder(element);
        this.enumColums = ProcessUtils.getElementEnumColumn(element);

        this.fields = ProcessUtils.getPrimitiveFields(element);
        this.otherClassFields = ProcessUtils.getNonPrimitiveClassFields(element);
    }

    public TypeSpec getDao() {
        return dao;
    }

    public TypeSpec getQueryBuilder() {
        return queryBuilder;
    }

    public ModelDaoGenerator generate() {

        TypeName listObjectsClassName = ProcessUtils.listOf(modelClassName);

        this.queryBuilder = TypeSpec.classBuilder(ProcessUtils.getQueryBuilderName(modelName)) //UserDAOQueryBuilder
                .addModifiers(Modifier.PUBLIC)

                .addField(ClassName.get(StringBuilder.class), "queryBuilder")
                .addField(ProcessUtils.listOf(String.class), "args")
                .addField(ProcessUtils.listOf(String.class), "fromTables")
                .addField(ProcessUtils.listOf(String.class), "fromTablesNames")
                .addField(ProcessUtils.listOf(String.class), "fromTablesId")
                .addField(TypeName.BOOLEAN, "named")

                .addMethod(MethodSpec.constructorBuilder()
                        .addModifiers(Modifier.PUBLIC)
                        .addStatement("this.queryBuilder = new $T()", ClassName.get(StringBuilder.class))
                        .addStatement("this.args = new $T()", ProcessUtils.arraylistOf(String.class))
                        .addStatement("this.fromTables = new $T()", ProcessUtils.arraylistOf(String.class))
                        .addStatement("this.fromTablesNames = new $T()", ProcessUtils.arraylistOf(String.class))
                        .addStatement("this.fromTablesId = new $T()", ProcessUtils.arraylistOf(String.class))
                        .build())

                .addMethod(MethodSpec.constructorBuilder()
                        .addModifiers(Modifier.PUBLIC)
                        .addParameter(TypeName.BOOLEAN, "named")
                        .addStatement("this()")
                        .addStatement("this.named = named")
                        .build())

                .addMethods(generateQueryMethods())

                .addMethod(MethodSpec.methodBuilder("or")
                        .returns(queryBuilderClassName)
                        .addModifiers(Modifier.PUBLIC)
                        .addStatement("queryBuilder.append($S)", " or ")
                        .addStatement("return this")
                        .build())

                .addMethod(MethodSpec.methodBuilder("and")
                        .returns(queryBuilderClassName)
                        .addModifiers(Modifier.PUBLIC)
                        .addStatement("queryBuilder.append($S)", " and ")
                        .addStatement("return this")
                        .build())

                .addMethod(MethodSpec.methodBuilder("beginGroup")
                        .returns(queryBuilderClassName)
                        .addModifiers(Modifier.PUBLIC)
                        .addStatement("queryBuilder.append($S)", " ( ")
                        .addStatement("return this")
                        .build())

                .addMethod(MethodSpec.methodBuilder("endGroup")
                        .returns(queryBuilderClassName)
                        .addModifiers(Modifier.PUBLIC)
                        .addStatement("queryBuilder.append($S)", " ) ")
                        .addStatement("return this")
                        .build())

                .addMethod(MethodSpec.methodBuilder("asList")
                        .returns(listObjectsClassName)
                        .addModifiers(Modifier.PUBLIC)
                        .addStatement("return execute()")
                        .build())

                .addMethod(MethodSpec.methodBuilder("first")
                        .returns(modelClassName)
                        .addModifiers(Modifier.PUBLIC)
                        .addStatement("$T objects = asList()", listObjectsClassName)
                        .addStatement("if(objects.isEmpty()) return null")
                        .addStatement("else return objects.get(0)")
                        .build())

                //TODO
                .addMethod(MethodSpec.methodBuilder("sortAsc")
                        .returns(queryBuilderClassName)
                        .addModifiers(Modifier.PUBLIC)
                        .addParameter(ArrayTypeName.of(enumColums), "columns")
                        .varargs()
                        .addStatement("queryBuilder.append($S);", " ORDER BY ")
                        .addStatement("boolean first = true")
                        .beginControlFlow("for($T c : columns)", enumColums)
                        .addStatement("if(!first) queryBuilder.append(','); else first = false ")
                        .addStatement("queryBuilder.append($S).append(c)", " " + TABLE_NAME + " .")
                        .endControlFlow()
                        .addStatement("queryBuilder.append($S)", " ASC ")
                        .addStatement("return this")
                        .build())

                .addMethod(MethodSpec.methodBuilder("sortDesc")
                        .returns(queryBuilderClassName)
                        .addModifiers(Modifier.PUBLIC)
                        .addParameter(ArrayTypeName.of(enumColums), "columns")
                        .varargs()
                        .addStatement("queryBuilder.append($S);", " ORDER BY ")
                        .addStatement("boolean first = true")
                        .beginControlFlow("for($T c : columns)", enumColums)
                        .addStatement("if(!first) queryBuilder.append(','); else first = false ")
                        .addStatement("queryBuilder.append($S).append(c)", " " + TABLE_NAME + " .")
                        .endControlFlow()
                        .addStatement("queryBuilder.append($S)", " DESC ")
                        .addStatement("return this")
                        .build())

                        //TODO
                .addMethod(MethodSpec.methodBuilder("sum")
                        .returns(TypeName.get(Number.class))
                        .addParameter(enumColums, "column")
                        .addModifiers(Modifier.PUBLIC)
                        .addStatement("return Float.valueOf(3)")
                        .build())

                        //TODO
                .addMethod(MethodSpec.methodBuilder("min")
                        .returns(TypeName.get(Number.class))
                        .addParameter(enumColums, "column")
                        .addModifiers(Modifier.PUBLIC)
                        .addStatement("return Float.valueOf(3)")
                        .build())

                        //TODO
                .addMethod(MethodSpec.methodBuilder("max")
                        .returns(TypeName.get(Number.class))
                        .addParameter(enumColums, "column")
                        .addModifiers(Modifier.PUBLIC)
                        .addStatement("return Float.valueOf(3)")
                        .build())

                        //TODO
                .addMethod(MethodSpec.methodBuilder("average")
                        .returns(TypeName.get(Number.class))
                        .addParameter(enumColums, "column")
                        .addModifiers(Modifier.PUBLIC)
                        .addStatement("return Float.valueOf(3)")
                        .build())

                .addMethod(MethodSpec.methodBuilder("constructArgs")
                        .returns(TypeName.get(String[].class))
                        .addModifiers(Modifier.PRIVATE)
                        .addStatement("return args.toArray(new String[args.size()])")
                        .build())

                .addMethod(MethodSpec.methodBuilder("constructQuery")
                        .returns(TypeName.get(String.class))
                        .addModifiers(Modifier.PUBLIC)
                        .addStatement("$T query = new $T()", ClassName.get(StringBuilder.class), ClassName.get(StringBuilder.class))
                        .addStatement("for($T s : fromTables) query.append($S).append(s)", ClassName.get(String.class), ", ")
                        .addStatement("if (args.size() != 0) query.append($S)", " where ")
                        .addStatement("query.append(queryBuilder.toString())")
                        .addStatement("return query.toString()")
                        .build())

                .addMethod(MethodSpec.methodBuilder("getTableId")
                        .addModifiers(Modifier.PRIVATE)
                        .returns(ClassName.get(String.class))
                        .addParameter(ClassName.get(String.class), "tableName")
                        .addStatement("$T tableId", ClassName.get(String.class))
                        .addStatement("int tablePos = fromTablesNames.indexOf(tableName)")
                        .addStatement("if(tablePos != -1) tableId = fromTablesId.get(tablePos)")
                        .addStatement("else{ tableId = $S + fromTables.size(); fromTablesId.add(tableId); fromTables.add(tableName + \" \" + tableId); fromTablesNames.add(tableName); }", Constants.QUERY_TABLE_VARIABLE)
                        .addStatement("return tableId")

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

                .addMethod(MethodSpec.methodBuilder("execute")
                        .returns(listObjectsClassName)
                        .addModifiers(Modifier.PRIVATE)
                        .addStatement("$T db = $T.getInstance().open().getDatabase()", Constants.databaseClassName, Constants.daoClassName)
                        .addStatement("$T cursor = db.rawQuery($S + constructQuery(), constructArgs())", Constants.cursorClassName, String.format("select distinct %s.* from %s ", TABLE_NAME, TABLE_NAME))
                        .addStatement("$T objects = $T.get(cursor,db)", listObjectsClassName, modelCursorHelperClassName)
                        .addStatement("cursor.close()")
                        .addStatement("$T.getInstance().close()", Constants.daoClassName)
                        .addStatement("return objects")
                        .build())

                .build();

        this.dao = TypeSpec.classBuilder(ProcessUtils.getModelDaoName(modelName)) //UserDAO
                .addModifiers(Modifier.PUBLIC)

                .addMethod(MethodSpec.constructorBuilder().addModifiers(Modifier.PUBLIC).build())

                .addMethod(MethodSpec.methodBuilder("create")
                        .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                        .returns(ArrayTypeName.get(String[].class))

                                //for
                        .addStatement("return new $T[]{$L}", ClassName.get(String.class), generateCreationString())
                        .build())

                .addMethod(MethodSpec.methodBuilder("update")
                        .returns(ClassName.get(String.class))
                        .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                        .addStatement("return $S", "")
                        .build())

                .addMethod(MethodSpec.methodBuilder("selectWhere")
                        .addModifiers(Modifier.PUBLIC)
                        .returns(queryBuilderClassName)
                        .addStatement("return new $T()", queryBuilderClassName)
                        .build())

                .addMethod(MethodSpec.methodBuilder("where")
                        .addModifiers(Modifier.PUBLIC)
                        .addModifiers(Modifier.STATIC)
                        .returns(queryBuilderClassName)
                        .addStatement("return new $T(true)", queryBuilderClassName)
                        .build())

                .addMethod(MethodSpec.methodBuilder("add")
                        .addParameter(modelClassName, "object")
                        .returns(TypeName.LONG)
                        .addModifiers(Modifier.PUBLIC)
                        .addStatement("$T database = $T.getInstance().open().getDatabase()", Constants.databaseClassName, Constants.daoClassName)
                        .addStatement("$T.insert(database,object)", modelCursorHelperClassName)
                        .addStatement("$T.getInstance().close()", Constants.daoClassName)
                        .addStatement("return object.$L", Constants.FIELD_ID)
                        .build())

                .addMethod(MethodSpec.methodBuilder("add")
                        .addParameter(ProcessUtils.listOf(modelClassName), "objects")
                        .addModifiers(Modifier.PUBLIC)
                        .addStatement("for($T object : objects) add(object)", modelClassName)
                        .build())

                .addMethod(MethodSpec.methodBuilder("delete")
                        .addParameter(modelClassName, "object")
                        .addModifiers(Modifier.PUBLIC)
                        .returns(TypeName.VOID)
                        .addStatement("$T.getInstance().open().getDatabase().delete($S, $S, new String[]{String.valueOf(object.$L)})", Constants.daoClassName, TABLE_NAME, "_id = ?", Constants.FIELD_ID)
                        .addStatement("$T.getInstance().close()", Constants.daoClassName)
                        .build())

                .addMethod(MethodSpec.methodBuilder("deleteAll")
                        .addModifiers(Modifier.PUBLIC)
                        .returns(TypeName.VOID)
                        .addStatement("$T.getInstance().open().getDatabase().execSQL($S)", Constants.daoClassName, "delete from " + TABLE_NAME)
                        .addStatement("$T.getInstance().close()", Constants.daoClassName)
                        .build())

                .addMethod(MethodSpec.methodBuilder("count")
                        .addModifiers(Modifier.PUBLIC)
                        .returns(TypeName.INT)
                        .addStatement("$T db = $T.getInstance().open().getDatabase()", Constants.databaseClassName, Constants.daoClassName)
                        .addStatement("$T cursor = db.rawQuery($S,null)", Constants.cursorClassName, "select count(*) from " + TABLE_NAME)
                        .addStatement("cursor.moveToFirst()")
                        .addStatement("int recCount = cursor.getInt(0)")
                        .addStatement("cursor.close()")
                        .addStatement("$T.getInstance().close()", Constants.daoClassName)
                        .addStatement("return recCount")
                        .build())

                .build();

        return this;
    }

    protected List<MethodSpec> generateQueryMethods() {
        List<MethodSpec> methodSpecs = new ArrayList<>();

        for (VariableElement variableElement : fields) {
            methodSpecs.add(MethodSpec.methodBuilder(variableElement.getSimpleName() + "Equals")
                    .returns(queryBuilderClassName)
                    .addModifiers(Modifier.PUBLIC)

                    .addParameter(TypeName.get(variableElement.asType()), variableElement.getSimpleName().toString())
                    .addStatement("if(named) queryBuilder.append($S)", Constants.QUERY_NAMED + ".")
                    .addStatement("queryBuilder.append(\"$L = ?\")", variableElement.getSimpleName())
                    .addStatement("args.add(" + ProcessUtils.getQueryCast(variableElement) + ")", variableElement.getSimpleName())
                    .addStatement("return this")
                    .build());
        }

        for (VariableElement variableElement : otherClassFields) {

            String JOINTABLE = ProcessUtils.getTableName(modelName) + "_" + ProcessUtils.getTableName(variableElement);

            methodSpecs.add(MethodSpec.methodBuilder(variableElement.getSimpleName().toString())
                    .returns(queryBuilderClassName)
                    .addModifiers(Modifier.PUBLIC)
                    .addParameter(ProcessUtils.getFieldQueryBuilderClass(variableElement), "query")
                    .addStatement("queryBuilder.append('(').append(query.query($S,getTableId($S),$S,$S,getTableId($S),$S,args)).append(')')", TABLE_NAME, JOINTABLE, ProcessUtils.getKeyName(modelName), ProcessUtils.getKeyName(variableElement), ProcessUtils.getTableName(variableElement), ProcessUtils.getObjectName(variableElement))
                    .addStatement("return this")
                    .build());
        }

        return methodSpecs;
    }

    protected String generateCreationString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append('"').append("create table ").append(TABLE_NAME).append(" (_id integer primary key autoincrement, ").append(generateTableCreate()).append(");").append('"');

        Set<String> addedTables = new HashSet<>();

        for (VariableElement variableElement : otherClassFields) {
            String table = TABLE_NAME + "_" + ProcessUtils.getTableName(variableElement);
            if (!addedTables.contains(table)) {
                stringBuilder
                        .append(",\n")
                        .append('"')
                        .append("create table ").append(table)
                        .append(" ( _id integer primary key autoincrement, ")
                        .append(ProcessUtils.getKeyName(modelName)).append(" integer, ")
                        .append(ProcessUtils.getKeyName(variableElement)).append(" integer, ")
                        .append(Constants.FIELD_NAME).append(" text )")
                        .append('"');
                addedTables.add(table);
            }
        }

        return stringBuilder.toString();
    }

    protected String generateTableCreate() {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < fields.size(); ++i) {
            VariableElement variableElement = fields.get(i);
            if (!Constants.FIELD_ID.equals(variableElement.getSimpleName().toString())) {
                stringBuilder
                        .append(variableElement.getSimpleName())
                        .append(" ")
                        .append(ProcessUtils.getFieldTableType(variableElement));
                if (i < fields.size() - 1)
                    stringBuilder.append(",");
            }
        }
        return stringBuilder.toString();
    }
}

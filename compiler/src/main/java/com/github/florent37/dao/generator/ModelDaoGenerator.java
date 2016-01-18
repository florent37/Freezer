package com.github.florent37.dao.generator;

import com.github.florent37.dao.Constants;
import com.github.florent37.dao.FreezerUtils;
import com.squareup.javapoet.ArrayTypeName;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.util.ArrayList;
import java.util.List;

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

    String TABLE_NAME;

    TypeSpec queryBuilder;
    TypeSpec dao;

    List<VariableElement> fields;
    List<VariableElement> otherClassFields;

    public ModelDaoGenerator(Element element) {
        this.modelName = FreezerUtils.getObjectName(element);
        this.modelPackage = FreezerUtils.getObjectPackage(element);

        this.modelClassName = TypeName.get(element.asType());
        this.modelCursorHelperClassName = FreezerUtils.getCursorHelper(element);

        this.TABLE_NAME = FreezerUtils.getTableName(element);

        this.queryBuilderClassName = FreezerUtils.getQueryBuilder(element);

        this.fields = FreezerUtils.getPrimitiveFields(element);
        this.otherClassFields = FreezerUtils.getNonPrimitiveClassFields(element);
    }

    public TypeSpec getDao() {
        return dao;
    }

    public TypeSpec getQueryBuilder() {
        return queryBuilder;
    }

    public ModelDaoGenerator generate() {

        TypeName listObjectsClassName = FreezerUtils.listOf(modelClassName);

        this.queryBuilder = TypeSpec.classBuilder(FreezerUtils.getQueryBuilderName(modelName)) //UserDAOQueryBuilder
                .addModifiers(Modifier.PUBLIC)

                .addField(ClassName.get(StringBuilder.class), "queryBuilder")
                .addField(FreezerUtils.listOf(String.class), "args")

                .addMethod(MethodSpec.constructorBuilder()
                        .addModifiers(Modifier.PUBLIC)
                        .addStatement("this.queryBuilder = new $T()", ClassName.get(StringBuilder.class))
                        .addStatement("this.args = new $T()", FreezerUtils.arraylistOf(String.class))
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

                .addMethod(MethodSpec.methodBuilder("constructArgs")
                        .returns(TypeName.get(String[].class))
                        .addModifiers(Modifier.PRIVATE)
                        .addStatement("return args.toArray(new String[args.size()])")
                        .build())

                .addMethod(MethodSpec.methodBuilder("constructQuery")
                        .returns(TypeName.get(String.class))
                        .addModifiers(Modifier.PRIVATE)
                        .addStatement("if (queryBuilder.length() == 0) return $S", "")
                        .addStatement("return $S + queryBuilder.toString()", "where ")
                        .build())

                .addMethod(MethodSpec.methodBuilder("execute")
                        .returns(listObjectsClassName)
                        .addModifiers(Modifier.PRIVATE)
                        .addStatement("$T db = $T.getInstance().open().getDatabase()", Constants.databaseClassName, Constants.daoClassName)
                        .addStatement("$T cursor = db.rawQuery($S + constructQuery(), constructArgs())", Constants.cursorClassName, "select * from " + TABLE_NAME + " ")
                        .addStatement("$T objects = $T.get(cursor,db)", listObjectsClassName, modelCursorHelperClassName)
                        .addStatement("cursor.close()")
                        .addStatement("$T.getInstance().close()", Constants.daoClassName)
                        .addStatement("return objects")
                        .build())

                .build();

        MethodSpec.Builder addB = MethodSpec.methodBuilder("add")
                .addParameter(modelClassName, "object")
                .returns(TypeName.LONG)
                .addModifiers(Modifier.PUBLIC)
                .addStatement("$T database = $T.getInstance().open().getDatabase()", Constants.databaseClassName, Constants.daoClassName)
                .addStatement("object.$L = database.insert($S, null, $T.getValues(object))", Constants.FIELD_ID, TABLE_NAME, modelCursorHelperClassName);

        for (VariableElement variableElement : otherClassFields) {
            String JOINTABLE = TABLE_NAME + "_" + FreezerUtils.getTableName(variableElement);
            addB.addStatement("object.$L._id = database.insert($S, null, $T.getValues(object.$L))", FreezerUtils.getObjectName(variableElement), FreezerUtils.getTableName(variableElement), FreezerUtils.getFieldCursorHelperClass(variableElement), FreezerUtils.getObjectName(variableElement));
            addB.addStatement("database.insert($S, null, $T.get$LValues(object._id,object.$L._id))", JOINTABLE, modelCursorHelperClassName, JOINTABLE, FreezerUtils.getObjectName(variableElement));
        }

        addB.addStatement("$T.getInstance().close()", Constants.daoClassName)
                .addStatement("return object.$L", Constants.FIELD_ID);

        this.dao = TypeSpec.classBuilder(FreezerUtils.getModelDaoName(modelName)) //UserDAO
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

                .addMethod(addB.build())

                .addMethod(MethodSpec.methodBuilder("delete")
                        .addParameter(modelClassName, "object")
                        .addModifiers(Modifier.PUBLIC)
                        .returns(TypeName.VOID)
                        .addStatement("$T.getInstance().open().getDatabase().delete($S, $S, null)", Constants.daoClassName, TABLE_NAME, "_id = id")
                        .addStatement("$T.getInstance().close()", Constants.daoClassName)
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
                    .addStatement("queryBuilder.append(\"$L = ?\")", variableElement.getSimpleName())
                    .addStatement("args.add(" + FreezerUtils.getQueryCast(variableElement) + ")", variableElement.getSimpleName())
                    .addStatement("return this")
                    .build());
        }

        return methodSpecs;
    }

    protected String generateCreationString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append('"').append("create table ").append(TABLE_NAME).append(" (_id integer primary key autoincrement, ").append(generateTableCreate()).append(");").append('"');

        for (VariableElement variableElement : otherClassFields) {
            stringBuilder
                    .append(",\n")
                    .append('"')
                    .append("create table ").append(TABLE_NAME).append("_").append(FreezerUtils.getTableName(variableElement))
                    .append(" ( _id integer primary key autoincrement, ")
                    .append(FreezerUtils.getKeyName(modelName)).append(" integer, ")
                    .append(FreezerUtils.getKeyName(variableElement)).append(" integer );")
                    .append('"');
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
                        .append(FreezerUtils.getFieldTableType(variableElement));
                if (i < fields.size() - 1)
                    stringBuilder.append(",");
            }
        }
        return stringBuilder.toString();
    }
}

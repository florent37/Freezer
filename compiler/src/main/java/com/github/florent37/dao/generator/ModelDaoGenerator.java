package com.github.florent37.dao.generator;

import com.github.florent37.dao.Constants;
import com.github.florent37.dao.FreezerUtils;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
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

    public ModelDaoGenerator(Element element) {
        this.modelName = FreezerUtils.getObjectName(element);
        this.modelPackage = FreezerUtils.getObjectPackage(element);

        this.modelClassName = TypeName.get(element.asType());
        this.modelCursorHelperClassName = FreezerUtils.getCursorHelper(element);

        this.TABLE_NAME = modelName.toUpperCase();

        this.queryBuilderClassName = FreezerUtils.getQueryBuilder(element);

        this.fields =  FreezerUtils.getFields(element);
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
                        .addStatement("$T cursor = $T.getInstance().open().getDatabase().rawQuery($S + constructQuery(), constructArgs())", Constants.cursorClassName, Constants.daoClassName, "select * from " + TABLE_NAME + " ")
                        .addStatement("$T objects = $T.get(cursor)", listObjectsClassName, modelCursorHelperClassName)
                        .addStatement("cursor.close()")
                        .addStatement("$T.getInstance().close()",Constants.daoClassName)
                        .addStatement("return objects")
                        .build())

                .build();

        this.dao = TypeSpec.classBuilder(FreezerUtils.getModelDaoName(modelName)) //UserDAO
                .addModifiers(Modifier.PUBLIC)

                .addMethod(MethodSpec.constructorBuilder().addModifiers(Modifier.PUBLIC).build())

                .addMethod(MethodSpec.methodBuilder("create")
                        .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                        .returns(ClassName.get(String.class))
                        .addStatement("return $S", "create table " + TABLE_NAME + " ( _id integer primary key autoincrement, "+generateTableCreate()+" );")
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

                .addMethod(MethodSpec.methodBuilder("add")
                        .addParameter(modelClassName, "object")
                        .returns(TypeName.LONG)
                        .addModifiers(Modifier.PUBLIC)
                        .addStatement("$T values = $T.getValues(object)", Constants.contentValuesClassName, modelCursorHelperClassName)
                        .addStatement("long insertId = $T.getInstance().open().getDatabase().insert($S, null, values)", Constants.daoClassName, TABLE_NAME)
                        .addStatement("$T.getInstance().close()", Constants.daoClassName)
                        .addStatement("return insertId")
                        .build())

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

    protected List<MethodSpec> generateQueryMethods(){
        List<MethodSpec> methodSpecs = new ArrayList<>();

        for(VariableElement variableElement : fields){
            methodSpecs.add(MethodSpec.methodBuilder(variableElement.getSimpleName()+"Equals")
                    .returns(queryBuilderClassName)
                    .addModifiers(Modifier.PUBLIC)

                    .addParameter(TypeName.get(variableElement.asType()), variableElement.getSimpleName().toString())
                    .addStatement("queryBuilder.append(\"$L = ?\")", variableElement.getSimpleName())
                    .addStatement("args.add("+FreezerUtils.getQueryCast(variableElement)+")\n",variableElement.getSimpleName())
                    .addStatement("return this")
                    .build());
        }

        return methodSpecs;
    }

    protected String generateTableCreate() {
        StringBuilder stringBuilder = new StringBuilder();
        for(int i=0;i<fields.size();++i){
            VariableElement variableElement = fields.get(i);
            stringBuilder
                    .append(variableElement.getSimpleName())
                    .append(" ")
                    .append(FreezerUtils.getFieldTableType(variableElement));
            if(i<fields.size()-1)
                stringBuilder.append(",");
        }
        return stringBuilder.toString();
    }
}

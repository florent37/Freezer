package com.github.florent37.dao;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.util.ArrayList;
import java.util.List;

import javax.lang.model.element.Modifier;

/**
 * Created by florentchampigny on 08/01/2016.
 */
public class DaoGenerator {

    static final String DAO_PACKAGE = "com.github.florent37.dao";
    String modelName;
    TypeName modelClassName;

    TypeName modelCursorHelperClassName;

    String daoName;
    String queryBuilderName;
    TypeName thisDAOClassName;
    TypeName queryBuilderClassName;

    String TABLE_NAME;

    TypeSpec queryBuilder;
    TypeSpec dao;

    public DaoGenerator(String modelName, ClassName modelClassName, TypeName modelCursorHelperClassName) {
        this.modelName = modelName;
        this.modelClassName = modelClassName;
        this.modelCursorHelperClassName = modelCursorHelperClassName;

        this.TABLE_NAME = modelName.toUpperCase();

        this.daoName = modelName + "DAO";
        this.queryBuilderName = modelName + "DAOQueryBuilder";

        this.thisDAOClassName = ClassName.get(DAO_PACKAGE, daoName);
        this.queryBuilderClassName = ClassName.get(DAO_PACKAGE, queryBuilderName);

    }

    public TypeSpec getDao() {
        return dao;
    }

    public TypeSpec getQueryBuilder() {
        return queryBuilder;
    }

    public void generate() {

        TypeName contentValuesClassName = ClassName.get("android.content", "ContentValues");

        TypeName stringBuilderClassName = ClassName.get(StringBuilder.class);
        TypeName listStringClassName = ParameterizedTypeName.get(List.class, String.class);
        TypeName arrayListStringClassName = ParameterizedTypeName.get(ClassName.get(ArrayList.class), ClassName.get(String.class));
        TypeName listObjectsClassName = ParameterizedTypeName.get(ClassName.get(List.class), modelClassName);

        TypeName cursorClassName = ClassName.get("android.database", "Cursor");

        this.queryBuilder = TypeSpec.classBuilder(queryBuilderName) //UserDAOQueryBuilder
                .addModifiers(Modifier.PUBLIC)

                .addField(stringBuilderClassName, "queryBuilder")
                .addField(listStringClassName, "args")

                .addMethod(MethodSpec.constructorBuilder()
                        .addModifiers(Modifier.PUBLIC)
                        .addStatement("this.queryBuilder = new $T()", stringBuilderClassName)
                        .addStatement("this.args = new $T()", arrayListStringClassName)
                        .build())

                        //region for fields
                .addMethod(MethodSpec.methodBuilder("ageEquals")
                        .returns(queryBuilderClassName)
                        .addModifiers(Modifier.PUBLIC)

                        .addParameter(TypeName.INT, "age")
                        .addStatement("queryBuilder.append(\"$L = ?\")", "age")
                        .addStatement("args.add(String.valueOf($L))\n", "age")
                        .addStatement("return this")
                        .build())

                .addMethod(MethodSpec.methodBuilder("nameEquals")
                        .returns(queryBuilderClassName)
                        .addModifiers(Modifier.PUBLIC)

                        .addParameter(ClassName.get(String.class), "name")
                        .addStatement("queryBuilder.append(\"$L = ?\")", "name")
                        .addStatement("args.add($L)", "name")
                        .addStatement("return this")
                        .build())
                        //endregion

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
                        .addStatement("$T cursor = DAO.getInstance().open().getDatabase().rawQuery($S + constructQuery(), constructArgs())", cursorClassName, "select * from " + TABLE_NAME + " ")
                        .addStatement("$T objects = $T.get(cursor)", listObjectsClassName, modelCursorHelperClassName)
                        .addStatement("cursor.close()")
                        .addStatement("DAO.getInstance().close()")
                        .addStatement("return objects")
                        .build())

                .build();

        this.dao = TypeSpec.classBuilder(daoName) //UserDAO
                .addModifiers(Modifier.PUBLIC)

                .addMethod(MethodSpec.constructorBuilder().addModifiers(Modifier.PUBLIC).build())

                .addMethod(MethodSpec.methodBuilder("create")
                        .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                        .returns(ClassName.get(String.class))
                        .addStatement("return $S", "create table " + TABLE_NAME + " ( _id integer primary key autoincrement, age integer, name text not null );")
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
                        .addStatement("$T values = $T.getValues(object)", contentValuesClassName, modelCursorHelperClassName)
                        .addStatement("long insertId = DAO.getInstance().open().getDatabase().insert($S, null, values)", "USER")
                        .addStatement("DAO.getInstance().close()")
                        .addStatement("return insertId")
                        .build())

                .addMethod(MethodSpec.methodBuilder("delete")
                        .addParameter(modelClassName, "object")
                        .addModifiers(Modifier.PUBLIC)
                        .returns(thisDAOClassName)
                        .addStatement("DAO.getInstance().open().getDatabase().delete($S, $S, null)", "USER", "_id = id")
                        .addStatement("DAO.getInstance().close()")
                        .addStatement("return this")
                        .build())

                .build();
    }
}

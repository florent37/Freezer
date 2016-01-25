package com.github.florent37.orm.generator;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import javax.lang.model.element.Modifier;

/**
 * Created by florentchampigny on 18/01/2016.
 */
public class DAOGenerator {

    public DAOGenerator() {
    }

    public TypeSpec generate() {

        return TypeSpec.classBuilder(com.github.florent37.orm.Constants.DAO_CLASS_NAME)
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addField(com.github.florent37.orm.Constants.daoClassName, "INSTANCE", Modifier.PRIVATE, Modifier.STATIC)
                .addField(com.github.florent37.orm.Constants.databaseClassName, "database", Modifier.PRIVATE)
                .addField(com.github.florent37.orm.Constants.dbHelperClassName, "helper", Modifier.PRIVATE)

                .addMethod(MethodSpec.constructorBuilder().addModifiers(Modifier.PRIVATE).build())

                .addMethod(MethodSpec.methodBuilder("onCreate")
                        .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                        .addParameter(com.github.florent37.orm.Constants.applicationClassName, "application")
                        .addStatement("if(INSTANCE == null) INSTANCE = new $T()", com.github.florent37.orm.Constants.daoClassName)
                        .addStatement("INSTANCE.helper = new $T(application)", com.github.florent37.orm.Constants.dbHelperClassName)
                        .build())

                .addMethod(MethodSpec.methodBuilder("getInstance")
                        .returns(com.github.florent37.orm.Constants.daoClassName)
                        .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                        .addStatement("return INSTANCE")
                        .build())

                .addMethod(MethodSpec.methodBuilder("onDestroy")
                        .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                        .build())

                .addMethod(MethodSpec.methodBuilder("getDatabase")
                        .returns(com.github.florent37.orm.Constants.databaseClassName)
                        .addModifiers(Modifier.PUBLIC)
                        .addStatement("return database")
                        .build())

                .addMethod(MethodSpec.methodBuilder("open")
                        .returns(com.github.florent37.orm.Constants.daoClassName)
                        .addException(ClassName.get("android.database", "SQLException"))
                        .addModifiers(Modifier.PUBLIC)
                        .addStatement("database = helper.getWritableDatabase()")
                        .addStatement("return this")
                        .build())

                .addMethod(MethodSpec.methodBuilder("close")
                        .returns(com.github.florent37.orm.Constants.daoClassName)
                        .addModifiers(Modifier.PUBLIC)
                        .addStatement("helper.close()")
                        .addStatement("return this")
                        .build())

                .build();
    }

}

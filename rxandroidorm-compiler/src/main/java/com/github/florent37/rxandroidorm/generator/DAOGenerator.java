package com.github.florent37.rxandroidorm.generator;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import com.github.florent37.rxandroidorm.Constants;

import javax.lang.model.element.Modifier;

/**
 * Created by florentchampigny on 18/01/2016.
 */
public class DAOGenerator {

    public DAOGenerator() {
    }

    public TypeSpec generate() {

        return TypeSpec.classBuilder(Constants.DAO_CLASS_NAME)
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addField(Constants.daoClassName, "INSTANCE", Modifier.PRIVATE, Modifier.STATIC)
                .addField(Constants.databaseClassName, "database", Modifier.PRIVATE)
                .addField(Constants.dbHelperClassName, "helper", Modifier.PRIVATE)

                .addMethod(MethodSpec.constructorBuilder().addModifiers(Modifier.PRIVATE).build())

                .addMethod(MethodSpec.methodBuilder("onCreate")
                        .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                        .addParameter(Constants.applicationClassName, "application")
                        .addStatement("if(INSTANCE == null) INSTANCE = new $T()", Constants.daoClassName)
                        .addStatement("INSTANCE.helper = new $T(application)", Constants.dbHelperClassName)
                        .build())

                .addMethod(MethodSpec.methodBuilder("getInstance")
                        .returns(Constants.daoClassName)
                        .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                        .addStatement("return INSTANCE")
                        .build())

                .addMethod(MethodSpec.methodBuilder("onDestroy")
                        .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                        .build())

                .addMethod(MethodSpec.methodBuilder("getDatabase")
                        .returns(Constants.databaseClassName)
                        .addModifiers(Modifier.PUBLIC)
                        .addStatement("return database")
                        .build())

                .addMethod(MethodSpec.methodBuilder("open")
                        .returns(Constants.daoClassName)
                        .addException(ClassName.get("android.database", "SQLException"))
                        .addModifiers(Modifier.PUBLIC)
                        .addStatement("database = helper.getWritableDatabase()")
                        .addStatement("return this")
                        .build())

                .addMethod(MethodSpec.methodBuilder("close")
                        .returns(Constants.daoClassName)
                        .addModifiers(Modifier.PUBLIC)
                        .addStatement("helper.close()")
                        .addStatement("return this")
                        .build())

                .build();
    }

}

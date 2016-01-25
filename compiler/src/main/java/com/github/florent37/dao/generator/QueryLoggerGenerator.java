package com.github.florent37.dao.generator;

import com.github.florent37.dao.Constants;
import com.squareup.javapoet.ArrayTypeName;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import javax.lang.model.element.Modifier;
import javax.lang.model.type.ArrayType;

/**
 * Created by florentchampigny on 25/01/2016.
 */
public class QueryLoggerGenerator {

    public QueryLoggerGenerator() {
    }

    public TypeSpec generate() {
        return TypeSpec.interfaceBuilder(Constants.QUERY_LOGGER)
                .addModifiers(Modifier.PUBLIC)
                .addMethod(MethodSpec.methodBuilder("onQuery")
                        .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                        .addParameter(ClassName.get(String.class), "query")
                        .addParameter(ArrayTypeName.get(String[].class), "datas")
                        .build())
                .build();
    }

}

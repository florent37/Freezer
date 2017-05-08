package com.github.florent37.rxandroidorm.generator;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;

import com.github.florent37.rxandroidorm.Constants;
import com.github.florent37.rxandroidorm.ProcessUtils;

/**
 * Created by florentchampigny on 26/01/2016.
 */
public class ModelEntityProxyGenerator {

    Element element;

    public ModelEntityProxyGenerator(Element element) {
        this.element = element;
    }

    public static TypeSpec generateModelProxyInterface() {
        return TypeSpec.interfaceBuilder(Constants.MODEL_ENTITY_PROXY_INTERFACE)
                .addModifiers(Modifier.PUBLIC)
                .addMethod(MethodSpec.methodBuilder(Constants.MODEL_ENTITY_PROXY_GET_ID_METHOD)
                        .returns(TypeName.LONG)
                        .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                        .build())
                .addMethod(MethodSpec.methodBuilder(Constants.MODEL_ENTITY_PROXY_SET_ID_METHOD)
                        .returns(TypeName.VOID)
                        .addParameter(TypeName.LONG, "id")
                        .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                        .build())
                .build();
    }

    public TypeSpec generate() {
        String idFieldName = Constants.FIELD_ID;
        Element idField = ProcessUtils.getIdField(element);
        if (idField != null)
            idFieldName = ProcessUtils.getObjectName(idField);

        TypeSpec.Builder builder = TypeSpec.classBuilder(ProcessUtils.getObjectName(element) + Constants.MODEL_ENTITY_PROXY)
                .addModifiers(Modifier.PUBLIC)
                .superclass(TypeName.get(element.asType()))
                .addSuperinterface(ClassName.get(Constants.DAO_PACKAGE, Constants.MODEL_ENTITY_PROXY_INTERFACE));

        if (idField == null) {
            builder.addField(TypeName.LONG, Constants.FIELD_ID);
        }

        builder.addMethod(MethodSpec.methodBuilder(Constants.MODEL_ENTITY_PROXY_GET_ID_METHOD)
                .addModifiers(Modifier.PUBLIC)
                .returns(TypeName.LONG)
                .addStatement("return $L", idFieldName)
                .build())
                .addMethod(MethodSpec.methodBuilder(Constants.MODEL_ENTITY_PROXY_SET_ID_METHOD)
                        .addModifiers(Modifier.PUBLIC)
                        .returns(TypeName.VOID)
                        .addParameter(TypeName.LONG, "id")
                        .addStatement("this.$L = id", idFieldName)
                        .build());

        return builder.build();
    }

}

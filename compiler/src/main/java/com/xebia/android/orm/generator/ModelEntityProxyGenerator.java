package com.xebia.android.orm.generator;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.xebia.android.orm.Constants;
import com.xebia.android.orm.ProcessUtils;

import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;

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
        return TypeSpec.classBuilder(ProcessUtils.getObjectName(element) + Constants.MODEL_ENTITY_PROXY)
                .addModifiers(Modifier.PUBLIC)
                .superclass(TypeName.get(element.asType()))
                .addSuperinterface(ClassName.get(Constants.DAO_PACKAGE, Constants.MODEL_ENTITY_PROXY_INTERFACE))
                .addField(TypeName.LONG, Constants.FIELD_ID)
                .addMethod(MethodSpec.methodBuilder(Constants.MODEL_ENTITY_PROXY_GET_ID_METHOD)
                        .addModifiers(Modifier.PUBLIC)
                        .returns(TypeName.LONG)
                        .addStatement("return $L", Constants.FIELD_ID)
                        .build())
                .addMethod(MethodSpec.methodBuilder(Constants.MODEL_ENTITY_PROXY_SET_ID_METHOD)
                        .addModifiers(Modifier.PUBLIC)
                        .returns(TypeName.VOID)
                        .addParameter(TypeName.LONG, "id")
                        .addStatement("this.$L = id", Constants.FIELD_ID)
                        .build())
                .build();
    }

}

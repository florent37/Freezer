package com.github.florent37.rxandroidorm.generator;

import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.util.List;

import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.VariableElement;

import com.github.florent37.rxandroidorm.Constants;
import com.github.florent37.rxandroidorm.ProcessUtils;

/**
 * Created by florentchampigny on 22/01/2016.
 */
public class EnumColumnGenerator {

    Element element;
    List<VariableElement> fields;

    public EnumColumnGenerator(Element element) {
        this.element = element;
        this.fields = ProcessUtils.getFields(element);
    }

    public TypeSpec generate() {
        TypeSpec.Builder enumBuilder = TypeSpec.enumBuilder(ProcessUtils.getObjectName(element) + Constants.ENUM_COLUMN_SUFFIX)
                .addModifiers(Modifier.PUBLIC)
                .addField(String.class, Constants.ENUM_COLUMN_ELEMENT_NAME, Modifier.PRIVATE, Modifier.FINAL)
                .addField(TypeName.BOOLEAN, Constants.ENUM_COLUMN_IS_PRIMITIVE, Modifier.PRIVATE, Modifier.FINAL)
                .addMethod(MethodSpec.methodBuilder("getName")
                        .addModifiers(Modifier.PUBLIC)
                        .returns(TypeName.get(String.class))
                        .addStatement("return this.$L", Constants.ENUM_COLUMN_ELEMENT_NAME)
                        .build())
                .addMethod(MethodSpec.methodBuilder("isPrimitive")
                        .addModifiers(Modifier.PUBLIC)
                        .returns(TypeName.BOOLEAN)
                        .addStatement("return this.$L", Constants.ENUM_COLUMN_IS_PRIMITIVE)
                        .build())
                .addMethod(MethodSpec.constructorBuilder()
                        .addParameter(String.class, "name")
                        .addParameter(TypeName.BOOLEAN, "primitive")
                        .addStatement("this.$L = name", Constants.ENUM_COLUMN_ELEMENT_NAME)
                        .addStatement("this.$L = primitive", Constants.ENUM_COLUMN_IS_PRIMITIVE)
                        .build());

        for (VariableElement variableElement : fields) {
            String fieldSqlName = ProcessUtils.getObjectName(variableElement);
            if (ProcessUtils.isIdField(variableElement))
                fieldSqlName = Constants.FIELD_ID;
            boolean isPrimivive = ProcessUtils.isPrimitive(variableElement);
            enumBuilder.addEnumConstant(ProcessUtils.getObjectName(variableElement), TypeSpec.anonymousClassBuilder("$S, $L", fieldSqlName, isPrimivive)
                    .build());
        }

        return enumBuilder.build();
    }
}

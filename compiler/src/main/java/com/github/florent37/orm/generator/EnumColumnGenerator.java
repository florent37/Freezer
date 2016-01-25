package com.github.florent37.orm.generator;

import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.util.List;

import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.VariableElement;

/**
 * Created by florentchampigny on 22/01/2016.
 */
public class EnumColumnGenerator {

    Element element;
    List<VariableElement> fields;

    public EnumColumnGenerator(Element element) {
        this.element = element;
        this.fields = com.github.florent37.orm.ProcessUtils.getPrimitiveFields(element);
    }

    public TypeSpec generate() {
        TypeSpec.Builder enumBuilder = TypeSpec.enumBuilder(com.github.florent37.orm.ProcessUtils.getObjectName(element) + com.github.florent37.orm.Constants.ENUM_COLUMN_SUFFIX)
                .addModifiers(Modifier.PUBLIC)
                .addField(String.class, com.github.florent37.orm.Constants.ENUM_COLUMN_ELEMENT_NAME, Modifier.PRIVATE, Modifier.FINAL)
                .addMethod(MethodSpec.methodBuilder("getName")
                        .addModifiers(Modifier.PUBLIC)
                        .returns(TypeName.get(String.class))
                        .addStatement("return this.$L", com.github.florent37.orm.Constants.ENUM_COLUMN_ELEMENT_NAME)
                        .build())
                .addMethod(MethodSpec.constructorBuilder()
                        .addParameter(String.class, "name")
                        .addStatement("this.$L = name", com.github.florent37.orm.Constants.ENUM_COLUMN_ELEMENT_NAME)
                        .build());

        for (VariableElement variableElement : fields) {
            enumBuilder.addEnumConstant(com.github.florent37.orm.ProcessUtils.getObjectName(variableElement), TypeSpec.anonymousClassBuilder("$S", com.github.florent37.orm.ProcessUtils.getObjectName(variableElement))
                    .build());
        }

        return enumBuilder.build();
    }
}

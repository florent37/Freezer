package com.xebia.android.orm.generator;

import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.xebia.android.orm.Constants;
import com.xebia.android.orm.ProcessUtils;

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
        this.fields = ProcessUtils.getPrimitiveFields(element);
    }

    public TypeSpec generate() {
        TypeSpec.Builder enumBuilder = TypeSpec.enumBuilder(ProcessUtils.getObjectName(element) + Constants.ENUM_COLUMN_SUFFIX)
                .addModifiers(Modifier.PUBLIC)
                .addField(String.class, Constants.ENUM_COLUMN_ELEMENT_NAME, Modifier.PRIVATE, Modifier.FINAL)
                .addMethod(MethodSpec.methodBuilder("getName")
                        .addModifiers(Modifier.PUBLIC)
                        .returns(TypeName.get(String.class))
                        .addStatement("return this.$L", Constants.ENUM_COLUMN_ELEMENT_NAME)
                        .build())
                .addMethod(MethodSpec.constructorBuilder()
                        .addParameter(String.class, "name")
                        .addStatement("this.$L = name", Constants.ENUM_COLUMN_ELEMENT_NAME)
                        .build());

        for (VariableElement variableElement : fields) {
            enumBuilder.addEnumConstant(ProcessUtils.getObjectName(variableElement), TypeSpec.anonymousClassBuilder("$S", ProcessUtils.getObjectName(variableElement))
                    .build());
        }

        return enumBuilder.build();
    }
}

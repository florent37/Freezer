package com.github.florent37.rxandroidorm;

import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;

import java.util.List;

/**
 * Created by florentchampigny on 20/01/2016.
 */
public class Dependency {
    TypeName typeName;
    List<MethodSpec> methodsToAdd;

    public Dependency(TypeName typeName, List<MethodSpec> methodsToAdd) {
        this.typeName = typeName;
        this.methodsToAdd = methodsToAdd;
    }

    public TypeName getTypeName() {
        return typeName;
    }

    public void setTypeName(TypeName typeName) {
        this.typeName = typeName;
    }

    public List<MethodSpec> getMethodsToAdd() {
        return methodsToAdd;
    }

    public void setMethodsToAdd(List<MethodSpec> methodsToAdd) {
        this.methodsToAdd = methodsToAdd;
    }
}

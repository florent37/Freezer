package com.github.florent37.rxandroidorm;

import com.squareup.javapoet.TypeSpec;

import java.util.List;

import javax.lang.model.element.Element;

/**
 * Created by florentchampigny on 20/01/2016.
 */
public class CursorHelper {
    Element element;
    TypeSpec typeSpec;

    List<Dependency> dependencies;

    public CursorHelper(Element element, TypeSpec typeSpec, List<Dependency> dependencies) {
        this.element = element;
        this.typeSpec = typeSpec;
        this.dependencies = dependencies;
    }

    public String getPackage(){
        return ProcessUtils.getObjectPackage(element);
    }

    public Element getElement() {
        return element;
    }

    public TypeSpec getTypeSpec() {
        return typeSpec;
    }

    public void setTypeSpec(TypeSpec typeSpec) {
        this.typeSpec = typeSpec;
    }
}

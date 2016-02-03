package com.github.florent37.orm;

import com.google.common.truth.Truth;
import com.google.testing.compile.JavaFileObjects;
import com.google.testing.compile.JavaSourcesSubjectFactory;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.tools.JavaFileObject;

import fr.xebia.android.freezer.Processor;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

@RunWith(JUnitParamsRunner.class)
public class ProcessorTest {

    Processor buildableProcessor = new Processor();

    @Test
    @Parameters({
                        "cat/Cat.java, cat/CatColumns.java"
                })
    public void shouldSucceedAtGeneratingBuilder(String given, String expected) throws Exception {

        InputStream raw = getClass().getClassLoader().getResourceAsStream("toto.json");

        List<JavaFileObject> givenJavaFileObjects = getJavaObjects(given);

        JavaFileObject[] expectedJavaFileObjects = getJavaObjectsArray(expected);

        Truth.assertAbout(JavaSourcesSubjectFactory.javaSources())
                .that(givenJavaFileObjects)
                .processedWith(buildableProcessor)
                .compilesWithoutError()
                .and()
                .generatesSources(expectedJavaFileObjects[0], Arrays.copyOfRange(expectedJavaFileObjects, 1, expectedJavaFileObjects.length));
    }

    public List<JavaFileObject> getJavaObjects(String given) {
        String[] splitted = given.split(";");
        List<JavaFileObject> javaFileObjects = new ArrayList<>();
        for (String s : splitted) {
            javaFileObjects.add(JavaFileObjects.forResource(s));
        }
        return javaFileObjects;
    }

    public JavaFileObject[] getJavaObjectsArray(String given) {
        String[] splitted = given.split(";");
        JavaFileObject[] javaFileObjects = new JavaFileObject[splitted.length];
        int i = 0;
        for (String s : splitted) {
            javaFileObjects[i++] = JavaFileObjects.forResource(s);
        }
        return javaFileObjects;
    }
}
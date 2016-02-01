package fr.xebia.android.freezer;

import com.google.common.truth.Truth;
import com.google.testing.compile.JavaFileObjects;
import com.google.testing.compile.JavaSourcesSubjectFactory;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.tools.JavaFileObject;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

@RunWith(JUnitParamsRunner.class)
public class FreezerProcessorTest {

    Processor processor = new Processor();

    public static List<JavaFileObject> getResources(String names) {
        List<JavaFileObject> javaFileObjects = new ArrayList<>();
        for (String s : names.split(";"))
            javaFileObjects.add(JavaFileObjects.forResource(s));
        return javaFileObjects;
    }

    public static JavaFileObject[] getResourcesArray(String names) {
        String[] strings = names.split(";");
        JavaFileObject[] javaFileObjects = new JavaFileObject[strings.length];
        for (int i = 0; i < strings.length; ++i)
            javaFileObjects[i] = JavaFileObjects.forResource(strings[i]);
        return javaFileObjects;
    }

    @Test
    @Parameters({
                        "cat/Cat.java, cat/CatColumns.java;cat/catCursorHelper.java;cat/CatEntity.java;cat/CatEntityManager.java:cat/CatQueryBuilder.java",
                })
    public void shouldSucceedAtGeneratingBuilder(String given, String expected) throws Exception {
        List<JavaFileObject> givenJavaFileObjects = getResources(given);
        JavaFileObject[] expectedJavaFileObjects = getResourcesArray(expected);

        Truth.assertAbout(JavaSourcesSubjectFactory.javaSources())
                .that(givenJavaFileObjects)
                .processedWith(processor)
                .compilesWithoutError()
                .and()
                .generatesSources(expectedJavaFileObjects[0], Arrays.copyOfRange(expectedJavaFileObjects, 1, expectedJavaFileObjects.length));
    }
}
package com.github.florent37.rxandroidorm;

import com.github.florent37.rxandroidorm.annotations.DatabaseName;
import com.github.florent37.rxandroidorm.annotations.Migration;
import com.github.florent37.rxandroidorm.annotations.Model;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

import com.github.florent37.rxandroidorm.generator.CursorHelperGenerator;
import com.github.florent37.rxandroidorm.generator.DatabaseHelperGenerator;
import com.github.florent37.rxandroidorm.generator.EnumColumnGenerator;
import com.github.florent37.rxandroidorm.generator.ModelEntityProxyGenerator;
import com.github.florent37.rxandroidorm.generator.ModelORMGenerator;
import com.github.florent37.rxandroidorm.generator.PrimitiveCursorHelperGenerator;
import com.github.florent37.rxandroidorm.generator.QueryBuilderGenerator;
import com.github.florent37.rxandroidorm.generator.QueryLoggerGenerator;

/**
 * Created by florentchampigny on 07/01/2016.
 */
@SupportedSourceVersion(SourceVersion.RELEASE_7)
@SupportedAnnotationTypes(
    {
        "com.github.florent37.rxandroidorm.annotations.Model",
        "com.github.florent37.rxandroidorm.annotations.Migration",
        "com.github.florent37.rxandroidorm.annotations.DatabaseName",
        "com.github.florent37.rxandroidorm.annotations.Ignore"
    })
@AutoService(javax.annotation.processing.Processor.class)
public class Processor extends AbstractProcessor {

    List<Element> models = new ArrayList<>();
    List<ClassName> daosList = new ArrayList<>();

    List<CursorHelper> cursorHelpers = new ArrayList<>();

    Map<Integer, Element> migrators = new HashMap<>();
    String dbFile = "database.db";
    int version = 1;

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        writeStaticJavaFiles();

        getMigrators(roundEnv);
        getDatabaseName(roundEnv);

        for (Element element : roundEnv.getElementsAnnotatedWith(Model.class)) {
            models.add(element);
            generateColumnEnums(element);
            generateEntityProxies(element);
            generateCursorHelperFiles(element);
            generateModelDaoFiles(element);
        }
        resolveDependencies();
        writeJavaFiles();
        return true;
    }

    protected void writeStaticJavaFiles() {
        //writeFile(JavaFile.builder(Constants.DAO_PACKAGE, new DAOGenerator().generate()).build());
        writeFile(JavaFile.builder(Constants.DAO_PACKAGE, new QueryLoggerGenerator().generate()).build());
        writeFile(JavaFile.builder(Constants.DAO_PACKAGE, ModelEntityProxyGenerator.generateModelProxyInterface()).build());
        writeFile(JavaFile.builder(Constants.DAO_PACKAGE, new PrimitiveCursorHelperGenerator().generate()).build());
        writeFile(JavaFile.builder(Constants.DAO_PACKAGE, new QueryBuilderGenerator().generate()).build());
    }

    protected void writeJavaFiles() {
        for (CursorHelper cursorHelper : cursorHelpers) {
            writeFile(JavaFile.builder(cursorHelper.getPackage(), cursorHelper.getTypeSpec()).build());
        }

        writeFile(JavaFile.builder(Constants.DAO_PACKAGE, new DatabaseHelperGenerator(dbFile, version, daosList, migrators).generate()).build());
    }

    protected void writeFile(JavaFile javaFile) {
        //try {
        //    javaFile.writeTo(System.out);
        //} catch (IOException e) {
        //    //e.printStackTrace();
        //}

        try {
            javaFile.writeTo(this.processingEnv.getFiler());
        } catch (IOException e) {
            //e.printStackTrace();
        }
    }

    private void getMigrators(RoundEnvironment roundEnv) {
        int max = 1;
        for (Element element : roundEnv.getElementsAnnotatedWith(Migration.class)) {
            int v = element.getAnnotation(Migration.class).value();
            if (max < v) {
                max = v;
            }
            migrators.put(v, element);
        }
        version = max;
    }

    private void getDatabaseName(RoundEnvironment roundEnv) {
        for (Element element : roundEnv.getElementsAnnotatedWith(DatabaseName.class)) {
            String name = element.getAnnotation(DatabaseName.class).value();
            if (name != null && name.trim().length() > 0) {
                if (!name.endsWith(".db")) {
                    name = name + ".db";
                }
                dbFile = name;
                return;
            }
        }
    }

    private void generateEntityProxies(Element element) {
        ModelEntityProxyGenerator entityProxyGenerator = new ModelEntityProxyGenerator(element);

        writeFile(JavaFile.builder(ProcessUtils.getObjectPackage(element), entityProxyGenerator.generate()).build());
    }

    private void generateColumnEnums(Element element) {
        EnumColumnGenerator columnGenerator = new EnumColumnGenerator(element);

        writeFile(JavaFile.builder(ProcessUtils.getObjectPackage(element), columnGenerator.generate()).build());
    }

    private void resolveDependencies() {
        for (CursorHelper from : cursorHelpers) {
            for (Dependency dependency : from.dependencies) {
                for (CursorHelper to : cursorHelpers) {
                    if (dependency.getTypeName().equals(ProcessUtils.getFieldClass(to.element))) {
                        HashSet<String> methodsNames = new HashSet<>(ProcessUtils.getMethodsNames(to.getTypeSpec()));

                        TypeSpec.Builder builder = to.getTypeSpec().toBuilder();

                        for (MethodSpec methodSpec : dependency.getMethodsToAdd()) {
                            if (!methodsNames.contains(ProcessUtils.getMethodId(methodSpec))) {
                                builder.addMethod(methodSpec);
                                methodsNames.add(ProcessUtils.getMethodId(methodSpec));
                            }
                        }
                        to.setTypeSpec(builder.build());
                    }
                }
            }
        }
    }

    private void generateCursorHelperFiles(Element element) {
        CursorHelperGenerator cursorHelperGenerator = new CursorHelperGenerator(element);
        cursorHelpers.add(new CursorHelper(element, cursorHelperGenerator.generate(), cursorHelperGenerator.getDependencies()));
    }

    private void generateModelDaoFiles(Element element) {
        ModelORMGenerator modelORMGenerator = new ModelORMGenerator(element).generate();

        writeFile(JavaFile.builder(ProcessUtils.getObjectPackage(element), modelORMGenerator.getDao()).build());
        writeFile(JavaFile.builder(ProcessUtils.getObjectPackage(element), modelORMGenerator.getQueryBuilder()).build());

        daosList.add(ProcessUtils.getModelDao(element));
    }
}

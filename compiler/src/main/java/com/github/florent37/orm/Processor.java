package com.github.florent37.orm;

import com.github.florent37.dao.annotations.Model;
import com.github.florent37.orm.generator.DAOGenerator;
import com.github.florent37.orm.generator.DatabaseHelperGenerator;
import com.github.florent37.orm.generator.EnumColumnGenerator;
import com.github.florent37.orm.generator.ModelDaoGenerator;
import com.github.florent37.orm.generator.QueryLoggerGenerator;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

import static com.github.florent37.orm.ProcessUtils.getMethodId;

/**
 * Created by florentchampigny on 07/01/2016.
 */
@SupportedSourceVersion(SourceVersion.RELEASE_7)
@SupportedAnnotationTypes("com.github.florent37.dao.annotations.Model")
@AutoService(javax.annotation.processing.Processor.class)
public class Processor extends AbstractProcessor {

    List<Element> models = new ArrayList<>();
    List<ClassName> daosList = new ArrayList<>();

    List<com.github.florent37.orm.CursorHelper> cursorHelpers = new ArrayList<>();

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (Element element : roundEnv.getElementsAnnotatedWith(Model.class)) {
            models.add(element);
            generateColumnEnums(element);
            generateCursorHelperFiles(element);
            generateModelDaoFiles(element);
        }
        resolveDependencies();
        writeJavaFiles();
        return true;
    }

    private void generateColumnEnums(Element element) {
        EnumColumnGenerator columnGenerator = new EnumColumnGenerator(element);

        writeFile(JavaFile.builder(com.github.florent37.orm.ProcessUtils.getObjectPackage(element), columnGenerator.generate()).build());
    }

    private void resolveDependencies() {
        for (com.github.florent37.orm.CursorHelper from : cursorHelpers) {
            for (Dependency dependency : from.dependencies) {
                for (com.github.florent37.orm.CursorHelper to : cursorHelpers) {
                    if (dependency.getTypeName().equals(com.github.florent37.orm.ProcessUtils.getFieldClass(to.element))) {
                        HashSet<String> methodsNames = new HashSet<>(com.github.florent37.orm.ProcessUtils.getMethodsNames(to.getTypeSpec()));

                        TypeSpec.Builder builder = to.getTypeSpec().toBuilder();

                        for (MethodSpec methodSpec : dependency.getMethodsToAdd()) {
                            if (!methodsNames.contains(getMethodId(methodSpec))) {
                                builder.addMethod(methodSpec);
                                methodsNames.add(getMethodId(methodSpec));
                            }
                        }
                        to.setTypeSpec(builder.build());
                    }
                }
            }
        }
    }

    private void generateCursorHelperFiles(Element element) {
        com.github.florent37.orm.generator.CursorHelperGenerator cursorHelperGenerator = new com.github.florent37.orm.generator.CursorHelperGenerator(element);
        cursorHelpers.add(new com.github.florent37.orm.CursorHelper(element, cursorHelperGenerator.generate(), cursorHelperGenerator.getDependencies()));
    }

    private void generateModelDaoFiles(Element element) {
        ModelDaoGenerator modelDaoGenerator = new ModelDaoGenerator(element).generate();

        writeFile(JavaFile.builder(com.github.florent37.orm.ProcessUtils.getObjectPackage(element), modelDaoGenerator.getDao()).build());
        writeFile(JavaFile.builder(com.github.florent37.orm.ProcessUtils.getObjectPackage(element), modelDaoGenerator.getQueryBuilder()).build());

        daosList.add(com.github.florent37.orm.ProcessUtils.getModelDao(element));
    }

    protected void writeJavaFiles() {
        String dbFile = "database.db";
        int version = 1;

        for (com.github.florent37.orm.CursorHelper cursorHelper : cursorHelpers)
            writeFile(JavaFile.builder(cursorHelper.getPackage(), cursorHelper.getTypeSpec()).build());

        writeFile(JavaFile.builder(com.github.florent37.orm.Constants.DAO_PACKAGE, new DatabaseHelperGenerator(dbFile, version, daosList).generate()).build());
        writeFile(JavaFile.builder(com.github.florent37.orm.Constants.DAO_PACKAGE, new DAOGenerator().generate()).build());
        writeFile(JavaFile.builder(com.github.florent37.orm.Constants.DAO_PACKAGE, new QueryLoggerGenerator().generate()).build());
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
}

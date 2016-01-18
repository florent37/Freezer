package com.github.florent37.dao;

import com.github.florent37.dao.annotations.Model;
import com.github.florent37.dao.generator.CursorHelperGenerator;
import com.github.florent37.dao.generator.DAOGenerator;
import com.github.florent37.dao.generator.DatabaseHelperGenerator;
import com.github.florent37.dao.generator.ModelDaoGenerator;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;

/**
 * Created by florentchampigny on 07/01/2016.
 */
@SupportedSourceVersion(SourceVersion.RELEASE_7)
@SupportedAnnotationTypes("com.github.florent37.dao.annotations.Model")
@AutoService(Processor.class)
public class FreezerProcessor extends AbstractProcessor {

    List<Element> models = new ArrayList<>();
    List<ClassName> daosList = new ArrayList<>();

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (Element element : roundEnv.getElementsAnnotatedWith(Model.class)) {
            models.add(element);
            generateCursorHelperFiles(element);
            generateModelDaoFiles(element);
        }
        writeJavaFiles();
        return true;
    }

    private void generateCursorHelperFiles(Element element) {
        CursorHelperGenerator cursorHelperGenerator = new CursorHelperGenerator(element);
        writeFile(JavaFile.builder( FreezerUtils.getObjectPackage(element), cursorHelperGenerator.generate()).build());
    }

    private void generateModelDaoFiles(Element element) {
        ModelDaoGenerator modelDaoGenerator = new ModelDaoGenerator(element).generate();

        writeFile(JavaFile.builder(FreezerUtils.getObjectPackage(element), modelDaoGenerator.getDao()).build());
        writeFile(JavaFile.builder(FreezerUtils.getObjectPackage(element), modelDaoGenerator.getQueryBuilder()).build());

        daosList.add(FreezerUtils.getModelDao(element));
    }

    protected void writeJavaFiles() {
        String dbFile = "database.db";
        int version = 1;

        writeFile(JavaFile.builder(Constants.DAO_PACKAGE, new DatabaseHelperGenerator(dbFile, version, daosList).generate()).build());
        writeFile(JavaFile.builder(Constants.DAO_PACKAGE, new DAOGenerator().generate()).build());
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

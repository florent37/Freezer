package com.github.florent37.dao;

import com.github.florent37.dao.annotations.Model;
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

    static final String DAO_PACKAGE = "com.github.florent37.dao";
    Filer filer;

    ClassName daoClassName = ClassName.get(DAO_PACKAGE, "DAO");
    ClassName dbHelperClassName = ClassName.get(DAO_PACKAGE, "DatabaseHelper");

    List<Element> models = new ArrayList<>();
    List<ClassName> daosList = new ArrayList<>();

    @Override
    public synchronized void init(ProcessingEnvironment env) {
        super.init(env);
        filer = env.getFiler();
    }

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



    protected void generateCursorHelperFiles(Element element) {
        String OBJECT_NAME = FreezerUtils.getObjectName(element);
        String MODEL_PACKAGE = FreezerUtils.getObjectPackage(element);

        TypeSpec cursorHelper = generateCursorHelper(MODEL_PACKAGE, OBJECT_NAME, TypeName.get(element.asType()), FreezerUtils.getFields(element));
        writeFile(JavaFile.builder(MODEL_PACKAGE, cursorHelper).build());
    }

    protected void generateModelDaoFiles(Element element) {
        String OBJECT_NAME = FreezerUtils.getObjectName(element);
        String MODEL_PACKAGE = FreezerUtils.getObjectPackage(element);

        ClassName cursorHelperClassType = ClassName.get(MODEL_PACKAGE, OBJECT_NAME + ModelDaoGenerator.CURSOR_HELPER_SUFFIX);
        ClassName queryBuilderClassType = ClassName.get(MODEL_PACKAGE, OBJECT_NAME + ModelDaoGenerator.QUERY_BUILDER_SUFFIX);
        ClassName modelType = ClassName.get(MODEL_PACKAGE, OBJECT_NAME);

        ModelDaoGenerator modelDaoGenerator = new ModelDaoGenerator(OBJECT_NAME, modelType, cursorHelperClassType, queryBuilderClassType, daoClassName, FreezerUtils.getFields(element));
        modelDaoGenerator.generate();
        writeFile(JavaFile.builder(DAO_PACKAGE, modelDaoGenerator.getDao()).build());
        writeFile(JavaFile.builder(MODEL_PACKAGE, modelDaoGenerator.getQueryBuilder()).build());

        daosList.add(ClassName.get(DAO_PACKAGE, OBJECT_NAME + ModelDaoGenerator.DAO_SUFFIX));
    }

    protected void writeJavaFiles() {
        TypeSpec dbHelper = generateDatabaseHelper("database.db", 1, daosList);
        writeFile(JavaFile.builder(DAO_PACKAGE, dbHelper).build());

        TypeSpec dao = generateDAO();
        writeFile(JavaFile.builder(DAO_PACKAGE, dao).build());
    }

    protected TypeSpec generateDatabaseHelper(String fileName, int version, List<ClassName> daos) {

        ClassName sqliteOpenHelperClassName = ClassName.get("android.database.sqlite", "SQLiteOpenHelper");
        ClassName contextClassName = ClassName.get("android.content", "Context");
        ClassName databaseClassName = ClassName.get("android.database.sqlite", "SQLiteDatabase");

        MethodSpec.Builder onCreate = MethodSpec.methodBuilder("onCreate")
                .addModifiers(Modifier.PUBLIC)
                .addParameter(databaseClassName, "database");
        for (ClassName dao : daos)
            onCreate.addStatement("database.execSQL($T.create())", dao);

        MethodSpec.Builder onUpgrade = MethodSpec.methodBuilder("onUpgrade")
                .addModifiers(Modifier.PUBLIC)
                .addParameter(databaseClassName, "database")
                .addParameter(TypeName.INT, "oldVersion")
                .addParameter(TypeName.INT, "newVersion");

        for (ClassName dao : daos)
            onUpgrade.addStatement("database.execSQL($T.update())", dao);

        onUpgrade.addStatement("onCreate(database)");

        return TypeSpec.classBuilder("DatabaseHelper")
                .superclass(sqliteOpenHelperClassName)
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)

                .addMethod(MethodSpec.constructorBuilder()
                        .addModifiers(Modifier.PUBLIC)
                        .addParameter(contextClassName, "context")
                        .addStatement("super(context, $S, null, $L)", fileName, version)
                        .build())

                .addMethod(onCreate.build())
                .addMethod(onUpgrade.build())
                .build();
    }

    protected TypeSpec generateDAO() {

        ClassName applicationClassName = ClassName.get("android.app", "Application");
        ClassName databaseClassName = ClassName.get("android.database.sqlite", "SQLiteDatabase");

        return TypeSpec.classBuilder("DAO")
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addField(daoClassName, "INSTANCE", Modifier.PRIVATE, Modifier.STATIC)
                .addField(databaseClassName, "database", Modifier.PRIVATE)
                .addField(dbHelperClassName, "helper", Modifier.PRIVATE)

                .addMethod(MethodSpec.constructorBuilder().addModifiers(Modifier.PRIVATE).build())

                .addMethod(MethodSpec.methodBuilder("onCreate")
                        .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                        .addParameter(applicationClassName, "application")
                        .addStatement("if(INSTANCE == null) INSTANCE = new $T()", daoClassName)
                        .addStatement("INSTANCE.helper = new $T(application)", dbHelperClassName)
                        .build())

                .addMethod(MethodSpec.methodBuilder("getInstance")
                        .returns(daoClassName)
                        .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                        .addStatement("return INSTANCE")
                        .build())

                .addMethod(MethodSpec.methodBuilder("onDestroy")
                        .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                        .build())

                .addMethod(MethodSpec.methodBuilder("getDatabase")
                        .returns(databaseClassName)
                        .addModifiers(Modifier.PUBLIC)
                        .addStatement("return database")
                        .build())

                .addMethod(MethodSpec.methodBuilder("open")
                        .returns(daoClassName)
                        .addException(ClassName.get("android.database", "SQLException"))
                        .addModifiers(Modifier.PUBLIC)
                        .addStatement("database = helper.getWritableDatabase()")
                        .addStatement("return this")
                        .build())

                .addMethod(MethodSpec.methodBuilder("close")
                        .returns(daoClassName)
                        .addModifiers(Modifier.PUBLIC)
                        .addStatement("helper.close()")
                        .addStatement("return this")
                        .build())

                .build();
    }

    protected TypeSpec generateCursorHelper(String PACKAGE, String OBJECT_NAME, TypeName modelType, List<VariableElement> fields) {
        ClassName cursorClassName = ClassName.get("android.database", "Cursor");
        ClassName contentValuesClassName = ClassName.get("android.content", "ContentValues");
        ParameterizedTypeName modelListClassName = ParameterizedTypeName.get(ClassName.get(List.class), modelType);
        ParameterizedTypeName modelArrayListClassName = ParameterizedTypeName.get(ClassName.get(ArrayList.class), modelType);

        MethodSpec.Builder fromCursorB = MethodSpec.methodBuilder("fromCursor")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(modelType)
                .addParameter(cursorClassName, "cursor")
                .addStatement("$T object = new $T()", modelType, modelType)
                .addStatement("//_id");

        //for
        for (int i = 0; i < fields.size(); ++i) {
            VariableElement variableElement = fields.get(i);
            String cursor = "cursor.get$L($L)";
            cursor = String.format(FreezerUtils.getFieldCast(variableElement),cursor);

            fromCursorB.addStatement("object.$L = " +cursor, variableElement.getSimpleName(), FreezerUtils.getFieldType(variableElement), i + 1);
        }

        fromCursorB.addStatement("return object");

        MethodSpec.Builder getValuesB = MethodSpec.methodBuilder("getValues")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(contentValuesClassName)
                .addParameter(modelType, "object")
                .addStatement("$T values = new $T()", contentValuesClassName, contentValuesClassName);

        //for
        for (int i = 0; i < fields.size(); ++i) {
            VariableElement variableElement = fields.get(i);
            getValuesB.addStatement("values.put($S,object.$L)", variableElement.getSimpleName(), variableElement.getSimpleName());
        }

        getValuesB.addStatement("return values");

        MethodSpec get = MethodSpec.methodBuilder("get")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(modelListClassName)
                .addParameter(cursorClassName, "cursor")
                .addStatement("$T objects = new $T()", modelListClassName, modelArrayListClassName)
                .addStatement("cursor.moveToFirst()")
                .addCode("while (!cursor.isAfterLast()) {\n")
                .addStatement("    $T object = fromCursor(cursor)", modelType)
                .addStatement("    objects.add(object)")
                .addStatement("    cursor.moveToNext()")
                .addCode("}\n")
                .addStatement("return objects")
                .build();

        return TypeSpec.classBuilder(OBJECT_NAME + "CursorHelper")
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addMethod(fromCursorB.build())
                .addMethod(getValuesB.build())
                .addMethod(get)
                .build();

    }

    protected void writeFile(JavaFile javaFile) {
        try {
            javaFile.writeTo(System.out);
        } catch (IOException e) {
            //e.printStackTrace();
        }

        try {
            javaFile.writeTo(filer);
        } catch (IOException e) {
            //e.printStackTrace();
        }
    }
}

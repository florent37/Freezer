package com.github.florent37.dao;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
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
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;

/**
 * Created by florentchampigny on 07/01/2016.
 */
@SupportedSourceVersion(SourceVersion.RELEASE_7)
@SupportedAnnotationTypes("com.github.florent37.dao.annotations.Model")
@AutoService(Processor.class)
public class DAOProcessor extends AbstractProcessor {

    static final String DAO_PACKAGE = "com.github.florent37.dao";
    Filer filer;

    @Override public synchronized void init(ProcessingEnvironment env) {
        super.init(env);
        filer = env.getFiler();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        writeJavaFiles();
        return true;
    }

    protected void writeJavaFiles() {
        String MODEL_PACKAGE = "com.github.florent37.dao.model";
        String OBJECT_NAME = "User";

        ClassName modelType = ClassName.get(MODEL_PACKAGE, OBJECT_NAME);
        ClassName daoClassType = ClassName.get(DAO_PACKAGE, OBJECT_NAME+"DAO");
        ClassName cursorHelperClassType = ClassName.get(MODEL_PACKAGE,OBJECT_NAME+"CursorHelper");


        TypeSpec dbHelper = generateDatabaseHelper("user.db", 1, Arrays.asList(
                daoClassType
        ));
        writeFile(JavaFile.builder(DAO_PACKAGE, dbHelper).build());

        TypeSpec cursorHelper = generateCursorHelper(MODEL_PACKAGE, OBJECT_NAME, modelType);
        writeFile(JavaFile.builder(MODEL_PACKAGE, cursorHelper).build());

        TypeSpec dao = generateDAO();
        writeFile(JavaFile.builder(DAO_PACKAGE, dao).build());

        DaoGenerator daoGenerator = new DaoGenerator(OBJECT_NAME, modelType, cursorHelperClassType);
        daoGenerator.generate();
        writeFile(JavaFile.builder(DAO_PACKAGE, daoGenerator.getDao()).build());
        writeFile(JavaFile.builder(DAO_PACKAGE, daoGenerator.getQueryBuilder()).build());
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

        ClassName daoClassName = ClassName.get(DAO_PACKAGE, "DAO");
        ClassName applicationClassName = ClassName.get("android.app", "Application");
        ClassName databaseClassName = ClassName.get("android.database.sqlite", "SQLiteDatabase");
        ClassName dbHelperClassName = ClassName.get(DAO_PACKAGE, "DatabaseHelper");

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

    protected TypeSpec generateCursorHelper(String PACKAGE, String OBJECT_NAME, TypeName modelType) {
        ClassName cursorClassName = ClassName.get("android.database", "Cursor");
        ClassName contentValuesClassName = ClassName.get("android.content", "ContentValues");
        ParameterizedTypeName modelListClassName = ParameterizedTypeName.get(ClassName.get(List.class), modelType);
        ParameterizedTypeName modelArrayListClassName = ParameterizedTypeName.get(ClassName.get(ArrayList.class), modelType);

        MethodSpec fromCursor = MethodSpec.methodBuilder("fromCursor")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(modelType)
                .addParameter(cursorClassName, "cursor")
                .addStatement("$T object = new $T()", modelType, modelType)
                .addStatement("//_id")

                        //for
                .addStatement("object.$L = cursor.get$L($L)", "age", "Int", 1)
                .addStatement("object.$L = cursor.get$L($L)", "name", "String", 2)

                .addStatement("return object")
                .build();

        MethodSpec getValues = MethodSpec.methodBuilder("getValues")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(contentValuesClassName)
                .addParameter(modelType, "object")
                .addStatement("$T values = new $T()", contentValuesClassName, contentValuesClassName)

                        //for
                .addStatement("values.put($S,object.$L)", "age", "age")
                .addStatement("values.put($S,object.$L)", "name", "name")

                .addStatement("return values")
                .build();

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
                .addMethod(fromCursor)
                .addMethod(getValues)
                .addMethod(get)
                .build();

    }

    protected void writeFile(JavaFile javaFile) {
        //try {
        //    javaFile.writeTo(System.out);
        //} catch (IOException e) {
        //    e.printStackTrace();
        //}

        try {
            javaFile.writeTo(filer);
        } catch (IOException e) {
            //e.printStackTrace();
        }
    }
}

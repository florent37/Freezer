package com.github.florent37.dao.generator;

import com.github.florent37.dao.Constants;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.util.List;

import javax.lang.model.element.Modifier;

/**
 * Created by florentchampigny on 18/01/2016.
 */
public class DatabaseHelperGenerator {

    String fileName;
    int version;

    List<ClassName> daos;

    public DatabaseHelperGenerator(String fileName, int version, List<ClassName> daos) {
        this.fileName = fileName;
        this.version = version;
        this.daos = daos;
    }

    public TypeSpec generate() {

        MethodSpec.Builder onCreate = MethodSpec.methodBuilder("onCreate")
                .addModifiers(Modifier.PUBLIC)
                .addParameter(Constants.databaseClassName, "database");
        for (ClassName dao : daos)
            onCreate.addStatement("for($T s : $T.create()) database.execSQL(s);", ClassName.get(String.class), dao);

        MethodSpec.Builder onUpgrade = MethodSpec.methodBuilder("onUpgrade")
                .addModifiers(Modifier.PUBLIC)
                .addParameter(Constants.databaseClassName, "database")
                .addParameter(TypeName.INT, "oldVersion")
                .addParameter(TypeName.INT, "newVersion");

        for (ClassName dao : daos)
            onUpgrade.addStatement("database.execSQL($T.update())", dao);

        onUpgrade.addStatement("onCreate(database)");

        return TypeSpec.classBuilder(Constants.DATABASE_HELPER_CLASS_NAME)
                .superclass(Constants.sqliteOpenHelperClassName)
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)

                .addMethod(MethodSpec.constructorBuilder()
                        .addModifiers(Modifier.PUBLIC)
                        .addParameter(Constants.contextClassName, "context")
                        .addStatement("super(context, $S, null, $L)", fileName, version)
                        .build())

                .addMethod(onCreate.build())
                .addMethod(onUpgrade.build())
                .build();
    }

}

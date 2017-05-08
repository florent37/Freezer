package com.github.florent37.rxandroidorm.generator;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.github.florent37.rxandroidorm.Constants;

import java.util.List;
import java.util.Map;

import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;

/**
 * Created by florentchampigny on 18/01/2016.
 */
public class DatabaseHelperGenerator {

    String fileName;
    int version;

    List<ClassName> daos;
    Map<Integer,Element> migrators;

    public DatabaseHelperGenerator(String fileName, int version, List<ClassName> daos, Map<Integer,Element> migrators) {
        this.fileName = fileName;
        this.version = version;
        this.daos = daos;
        this.migrators = migrators;
    }

    public TypeSpec generate() {

        MethodSpec.Builder onCreate = MethodSpec.methodBuilder("onCreate")
                .addModifiers(Modifier.PUBLIC)
                .addParameter(Constants.databaseClassName, "database");
        for (ClassName dao : daos)
            onCreate.addStatement("for($T s : $T.create()) database.execSQL(s)", ClassName.get(String.class), dao);

        onCreate.addStatement("for($T s : $T.create()) database.execSQL(s)", ClassName.get(String.class), ClassName.get(Constants.DAO_PACKAGE, Constants.PRIMITIVE_CURSOR_HELPER));

        MethodSpec.Builder onUpgrade = MethodSpec.methodBuilder("onUpgrade")
                .addModifiers(Modifier.PUBLIC)
                .addParameter(Constants.databaseClassName, "database")
                .addParameter(TypeName.INT, "oldVersion")
                .addParameter(TypeName.INT, "newVersion")

                .addStatement("int version = oldVersion")
                .addStatement("$T freezerMigrator = new $T(database)", Constants.migrator, Constants.migrator)
                ;

        for(int i=1;i<version;++i){
            int v = i+1;
            String objectName = migrators.get(v).getEnclosingElement().toString();
            String methodName = migrators.get(v).getSimpleName().toString();
            onUpgrade.beginControlFlow("if(version < $L)",v)
                    .addStatement("$T.$L(freezerMigrator)", ClassName.bestGuess(objectName), methodName)
                    .addStatement("version++")
                    .endControlFlow();
        }

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

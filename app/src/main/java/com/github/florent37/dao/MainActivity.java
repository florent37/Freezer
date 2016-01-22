package com.github.florent37.dao;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.github.florent37.dao.model.Cat;
import com.github.florent37.dao.model.CatORM;
import com.github.florent37.dao.model.Dog;
import com.github.florent37.dao.model.DogORM;
import com.github.florent37.dao.model.User;
import com.github.florent37.dao.model.UserColumns;
import com.github.florent37.dao.model.UserORM;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    UserORM userORM;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userORM = new UserORM();

        userORM.deleteAll();

        userORM.add(Arrays.asList(
                        new User(21, "florent", new Cat("Java"), Arrays.asList(new Dog("Loulou"))),
                        new User(30, "kevin", new Cat("Futé"), Arrays.asList(new Dog("Darty"))),
                        new User(10, "alex", new Cat("Yellow"), Arrays.asList(new Dog("Darty"), new Dog("Sasha")))
                )
        );

        Log.d("DAO", userORM.selectWhere()
                .cat(CatORM.where().shortNameEquals("Java"))
                .and()

                .beginGroup()
                .dogs(DogORM.where().nameEquals("Sasha"))
                .or()
                .dogs(DogORM.where().nameEquals("Florent"))
                .endGroup()

                .asList()
                .toString());

        Log.d("DAO", userORM.selectWhere()
                .sortAsc(UserColumns.name)
                .asList()
                .toString());

        float agesSum = userORM.selectWhere()
                .sum(UserColumns.age);

        float agesAverage = userORM.selectWhere()
                .average(UserColumns.age);

        float ageMin = userORM.selectWhere()
                .min(UserColumns.age);

        float ageMax = userORM.selectWhere()
                .max(UserColumns.age);
    }
}

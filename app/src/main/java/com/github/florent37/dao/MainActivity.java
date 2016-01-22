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
                        new User("florent", new Cat("Java"), Arrays.asList(new Dog("Loulou"))),
                        new User("kevin", new Cat("Futé"), Arrays.asList(new Dog("Darty"))),
                        new User("alex", new Cat("Yellow"), Arrays.asList(new Dog("Darty"), new Dog("Sasha")))
                )
        );

        Log.d("DAO", userORM.selectWhere()
                .cat(CatORM.where().shortNameEquals("Java"))
                .and()
                .beginGroup()
                .dogs(DogORM.where().nameEquals("Sasha"))
                .or()
                .dogs(DogORM.where().nameEquals("Florent"))
                        //.endGroup()       )

                .asList()
                .toString());

        Log.d("DAO", userORM.selectWhere()
                .sort(UserColumns.name)
                .asList()
                .toString());

        //Long agesSum = userORM.selectWhere()
        //        .sum(UserColumns.age).longValue();

        //Long agesAverage = userORM.selectWhere()
        //        .average(UserColumns.age).longValue();

        //Long ageMin = userORM.selectWhere()
        //        .min(UserColumns.age).longValue();

        //Long ageMax = userORM.selectWhere()
        //        .max(UserColumns.age).longValue();
    }
}

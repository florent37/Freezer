package com.github.florent37.dao;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.github.florent37.dao.model.Cat;
import com.github.florent37.dao.model.Dog;
import com.github.florent37.dao.model.User;
import com.github.florent37.dao.model.UserORM;
import com.github.florent37.orm.QueryLogger;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    UserORM userORM;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userORM = new UserORM();

        userORM.logQueries(new QueryLogger() {
            @Override public void onQuery(String query, String[] datas) {
                Log.d("QUERY", query);
            }
        });

        userORM.deleteAll();

        userORM.add(Arrays.asList(
                        new User(21, "florent", new Cat("Java"), Arrays.asList(new Dog("Loulou"))),
                        new User(30, "kevin", new Cat("Futé"), Arrays.asList(new Dog("Darty"))),
                        new User(10, "alex", new Cat("Yellow"), Arrays.asList(new Dog("Darty"), new Dog("Sasha")))
                )
        );

        Log.d("DAO", userORM.select()
                .age().equalsTo(3)
                .or()
                .name().equalsTo("florent")

                .asList()
                .toString());
    }
}

package com.github.florent37.orm;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.github.florent37.orm.model.Cat;
import com.github.florent37.orm.model.CatEntityManager;
import com.github.florent37.orm.model.Dog;
import com.github.florent37.orm.model.User;
import com.github.florent37.orm.model.UserEntityManager;
import com.xebia.android.freezer.QueryLogger;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    UserEntityManager userORM;
    CatEntityManager catORM;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userORM = new UserEntityManager();
        catORM = new CatEntityManager();

        userORM.logQueries(new QueryLogger() {
            @Override public void onQuery(String query, String[] datas) {
                Log.d("QUERY", query);
            }
        });

        userORM.deleteAll();
        userORM.add(Arrays.asList(
                        new User(21, "florent", new Cat("Java"), Arrays.asList(new Dog("Loulou")), true),
                        new User(30, "kevin", new Cat("Futé"), Arrays.asList(new Dog("Darty")), true),
                        new User(10, "alex", new Cat("Yellow"), Arrays.asList(new Dog("Darty"), new Dog("Sasha")), false)
                )
        );

        Log.d("DAO", userORM.select()
                .hacker().isTrue()
                .or()
                .age().equalsTo(4)

                .asList()
                .toString());

    }
}

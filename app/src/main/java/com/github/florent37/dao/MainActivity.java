package com.github.florent37.dao;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.github.florent37.dao.dao.UserDAO;
import com.github.florent37.dao.model.User;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        UserDAO.add(
                new User(3, "florent"),
                new User(20,"kévin"),
                new User(10,"alex")
        );

        Log.d("DAO", UserDAO.selectWhere()
                .asList()
                .toString());

        Log.d("DAO",UserDAO.selectWhere()
                .ageEquals(3)
                .or()
                .nameEquals("kévin")
                .asList()
                .toString());
    }
}

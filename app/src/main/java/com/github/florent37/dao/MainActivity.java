package com.github.florent37.dao;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.github.florent37.dao.model.User;

public class MainActivity extends AppCompatActivity {

    UserDAO userDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userDAO = new UserDAO();

        userDAO.add(new User(3, "florent"));
        userDAO.add(new User(20, "kévin"));
        userDAO.add(new User(10, "alex"));

        Log.d("DAO", userDAO.selectWhere()
                .asList()
                .toString());

        Log.d("DAO", userDAO.selectWhere()
                .ageEquals(3)
                .or()
                .nameEquals("kévin")
                .asList()
                .toString());
    }
}

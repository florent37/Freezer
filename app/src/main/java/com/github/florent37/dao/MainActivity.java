package com.github.florent37.dao;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.github.florent37.dao.model.Car;
import com.github.florent37.dao.model.Dog;
import com.github.florent37.dao.model.User;
import com.github.florent37.dao.model.UserDAO;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    UserDAO userDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userDAO = new UserDAO();

        userDAO.add(new User(3, Arrays.asList(new Car("florent"), new Car("kevin"))));

        Log.d("DAO all", userDAO.selectWhere()
                .asList()
                .toString());
    }
}

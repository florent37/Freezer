package com.github.florent37.dao;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.github.florent37.dao.model.Car;
import com.github.florent37.dao.model.User;
import com.github.florent37.dao.model.UserFridge;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    UserFridge userFridge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userFridge = new UserFridge();

        userFridge.add(
                new User("florent", Arrays.asList(new Car(Color.RED), new Car(Color.BLUE)))
        );

        Log.d("DAO all", userFridge.selectWhere()
                .nameEquals("florent")
                .asList()
                .toString());
    }
}

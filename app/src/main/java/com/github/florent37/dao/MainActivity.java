package com.github.florent37.dao;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.github.florent37.dao.model.Car;
import com.github.florent37.dao.model.CarFridge;
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

        userFridge.deleteAll();

        userFridge.add(Arrays.asList(
                        new User("florent", Arrays.asList(new Car(Color.RED), new Car(Color.BLUE))),
                        new User("florent", Arrays.asList(new Car(Color.YELLOW)))
                )
        );

        Log.d("DAO all", userFridge.selectWhere()
                .cars(CarFridge.where().colorEquals(Color.RED))
                .asList()
                .toString());
    }
}

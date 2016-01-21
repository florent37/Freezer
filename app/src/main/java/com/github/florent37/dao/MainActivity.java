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
                        new User("florent", Arrays.asList(new Car(Color.RED), new Car(Color.BLUE)), new Car(Color.WHITE)),
                        new User("kevin", Arrays.asList(new Car(Color.RED)), new Car(Color.RED)),
                        new User("alex", Arrays.asList(new Car(Color.YELLOW)), new Car(Color.BLACK))
                )
        );

        Log.d("DAO", userFridge.selectWhere()
                .cars(CarFridge.where().colorEquals(Color.RED))
                .or()
                .car(CarFridge.where().colorEquals(Color.BLACK))
                .asList()
                .toString());
    }
}

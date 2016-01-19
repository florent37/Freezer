package com.github.florent37.dao.model;

import android.content.ContentValues;
import android.database.Cursor;

import com.github.florent37.dao.annotations.Model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by florentchampigny on 07/01/2016.
 */
@Model
public class User {

    public long _id;

    int age;
    List<Car> cars;
    List<Car> bags;
    Dog dog;

    public User(){}

    public User(int age, List<Car> cars) {
        this.age = age;
        this.cars = cars;
    }

    @Override public String toString() {
        return "User{" +
                "_id=" + _id +
                ", age=" + age +
                ", cars=" + cars +
                ", dog=" + dog +
                '}';
    }
}



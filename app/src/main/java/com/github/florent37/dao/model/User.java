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
    Car car;
    Dog dog;

    public User(){}

    public User(int age, Car car, Dog dog) {
        this.age = age;
        this.car = car;
        this.dog = dog;
    }

    @Override public String toString() {
        return "User{" +
                "_id=" + _id +
                ", age=" + age +
                ", car=" + car +
                ", dog=" + dog +
                '}';
    }
}



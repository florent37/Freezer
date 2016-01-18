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

    public User(){}

    public User(int age, Car car) {
        this.age = age;
        this.car = car;
    }

    @Override public String toString() {
        return "User{" +
                "_id=" + _id +
                ", age=" + age +
                ", car=" + car +
                '}';
    }
}



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

    int age;
    String name;
    boolean ok;
    float note;

    public User() {
    }

    public User(int age, String name) {
        this.age = age;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    @Override public String toString() {
        return "User{" +
                "age=" + age +
                ", name='" + name + '\'' +
                '}';
    }
}



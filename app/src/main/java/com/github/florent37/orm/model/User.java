package com.github.florent37.orm.model;

import com.xebia.android.orm.annotations.Model;

import java.util.List;

/**
 * Created by florentchampigny on 19/01/2016.
 */
@Model
public class User {
    int age;
    String name;
    Cat cat;
    List<Dog> dogs;
    boolean hacker;

    public User(){}

    public User(int age, String name, Cat cat, List<Dog> dogs, boolean hacker) {
        this.age = age;
        this.name = name;
        this.cat = cat;
        this.dogs = dogs;
        this.hacker = hacker;
    }

    @Override public String toString() {
        return "User{" +
                " name='" + name + '\'' +
                '}';
    }
}

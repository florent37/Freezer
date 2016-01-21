package com.github.florent37.dao.model;

import com.github.florent37.dao.annotations.Model;

import java.util.List;

/**
 * Created by florentchampigny on 19/01/2016.
 */
@Model
public class User {
    public long _id;

    String name;
    Cat cat;
    List<Dog> dogs;

    public User(){}

    public User(String name, Cat cat, List<Dog> dogs) {
        this.name = name;
        this.cat = cat;
        this.dogs = dogs;
    }

    @Override public String toString() {
        return "User{" +
                " name='" + name + '\'' +
                '}';
    }
}

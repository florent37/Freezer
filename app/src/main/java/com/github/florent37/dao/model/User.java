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
    List<Car> cars;

    public User() {
    }

    public User(String name, List<Car> cars) {
        this.name = name;
        this.cars = cars;
    }
}

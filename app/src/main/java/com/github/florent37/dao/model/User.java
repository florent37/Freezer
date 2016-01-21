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
    Car car;

    public User() {
    }

    public User(String name, List<Car> cars, Car car) {
        this.name = name;
        this.cars = cars;
        this.car = car;
    }

    @Override public String toString() {
        return "User{" +
                " name='" + name + '\'' +
                '}';
    }
}

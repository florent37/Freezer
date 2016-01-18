package com.github.florent37.dao.model;

import com.github.florent37.dao.annotations.Model;

/**
 * Created by florentchampigny on 18/01/2016.
 */
@Model
public class Car {
    public long _id;
    String name;

    public Car() {
    }

    public Car(String name) {
        this.name = name;
    }

    @Override public String toString() {
        return "Car{" +
                "_id=" + _id +
                ", name='" + name + '\'' +
                '}';
    }
}

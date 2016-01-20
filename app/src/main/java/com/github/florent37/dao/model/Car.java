package com.github.florent37.dao.model;

import com.github.florent37.dao.annotations.Model;

/**
 * Created by florentchampigny on 19/01/2016.
 */
@Model
public class Car {

    public long _id;

    int color;

    public Car() {
    }

    public Car(int color) {
        this.color = color;
    }
}

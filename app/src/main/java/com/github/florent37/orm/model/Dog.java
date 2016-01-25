package com.github.florent37.orm.model;

import com.github.florent37.dao.annotations.Model;

/**
 * Created by florentchampigny on 21/01/2016.
 */
@Model
public class Dog {
    public long _id;

    String name;

    public Dog(){}

    public Dog(String name) {
        this.name = name;
    }
}

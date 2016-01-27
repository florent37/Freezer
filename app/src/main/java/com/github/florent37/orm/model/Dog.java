package com.github.florent37.orm.model;

import com.xebia.android.freezer.annotations.Model;

/**
 * Created by florentchampigny on 21/01/2016.
 */
@Model
public class Dog {
    String name;

    public Dog(){}

    public Dog(String name) {
        this.name = name;
    }
}

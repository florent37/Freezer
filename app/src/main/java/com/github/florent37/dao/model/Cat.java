package com.github.florent37.dao.model;

import com.github.florent37.dao.annotations.Model;

/**
 * Created by florentchampigny on 21/01/2016.
 */
@Model
public class Cat {
    public long _id;

    String shortName;

    public Cat() {
    }

    public Cat(String shortName) {
        this.shortName = shortName;
    }
}

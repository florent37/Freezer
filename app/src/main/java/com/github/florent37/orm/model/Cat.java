package com.github.florent37.orm.model;

import com.xebia.android.orm.annotations.Model;

/**
 * Created by florentchampigny on 21/01/2016.
 */
@Model
public class Cat {
    String shortName;

    public Cat() {
    }

    public Cat(String shortName) {
        this.shortName = shortName;
    }
}

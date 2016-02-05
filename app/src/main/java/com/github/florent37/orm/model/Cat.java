package com.github.florent37.orm.model;

import fr.xebia.android.freezer.annotations.Model;

import java.util.Date;
import java.util.List;

/**
 * Created by florentchampigny on 21/01/2016.
 */
@Model
public class Cat {

    String shortName;
    Date date;

    public Cat() {
    }

    public Cat(String shortName) {
        this.shortName = shortName;
    }

    public String getShortName() {
        return shortName;
    }

}

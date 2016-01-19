package com.github.florent37.dao.model;

import com.github.florent37.dao.annotations.Model;

/**
 * Created by florentchampigny on 19/01/2016.
 */
@Model
public class Leaf {

    public long _id;

    int color;

    public Leaf() {
    }

    public Leaf(int color) {
        this.color = color;
    }

    public int getColor() {
        return color;
    }
}

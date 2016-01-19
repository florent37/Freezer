package com.github.florent37.dao.model;

import com.github.florent37.dao.annotations.Model;

/**
 * Created by florentchampigny on 19/01/2016.
 */
@Model
public class Tree {

    public long _id;

    int age;
    String name;

    public Tree() {
    }

    public Tree(int age, String name) {
        this.age = age;
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public String getName() {
        return name;
    }

}

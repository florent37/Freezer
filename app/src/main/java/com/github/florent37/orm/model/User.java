package com.github.florent37.orm.model;

import fr.xebia.android.freezer.annotations.Model;

import java.util.List;

/**
 * Created by florentchampigny on 19/01/2016.
 */
@Model
public class User {
    int age;
    String name;
    Cat cat;
    List<Dog> dogs;
    boolean hacker;
    
    public User(){}

    public User(int age, String name, Cat cat, List<Dog> dogs, boolean hacker) {
        this.age = age;
        this.name = name;
        this.cat = cat;
        this.dogs = dogs;
        this.hacker = hacker;
    }

    public User(int age, String name) {
        this.age = age;
        this.name = name;
    }

    @Override public String toString() {
        return "User{" +
                " name='" + name + '\'' +
                '}';
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCat(Cat cat) {
        this.cat = cat;
    }

    public void setDogs(List<Dog> dogs) {
        this.dogs = dogs;
    }

    public void setHacker(boolean hacker) {
        this.hacker = hacker;
    }

    public int getAge() {
        return age;
    }

    public String getName() {
        return name;
    }

    public Cat getCat() {
        return cat;
    }

    public List<Dog> getDogs() {
        return dogs;
    }

    public boolean isHacker() {
        return hacker;
    }
}

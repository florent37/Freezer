package com.github.florent37.rxandroidorm.sample.model;

import com.github.florent37.rxandroidorm.annotations.Id;
import com.github.florent37.rxandroidorm.annotations.Model;

import java.util.List;

@Model
public class User {

    @Id
    long id;

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

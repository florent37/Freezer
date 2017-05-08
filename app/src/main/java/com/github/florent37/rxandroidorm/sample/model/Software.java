package com.github.florent37.rxandroidorm.sample.model;

import com.github.florent37.rxandroidorm.annotations.Id;
import com.github.florent37.rxandroidorm.annotations.Model;

/**
 * Created by florentchampigny on 08/05/2017.
 */

@Model
public class Software {

    @Id
    long id;

    String name;

    public Software() {
    }

    public Software(String name) {
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

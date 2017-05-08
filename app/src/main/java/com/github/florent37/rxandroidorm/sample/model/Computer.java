package com.github.florent37.rxandroidorm.sample.model;

import android.support.annotation.IntDef;

import com.github.florent37.rxandroidorm.annotations.Id;
import com.github.florent37.rxandroidorm.annotations.Model;

import java.util.List;

/**
 * Created by florentchampigny on 08/05/2017.
 */

@Model
public class Computer {

    @Id
    long id;

    @OS
    int os;

    String label;

    List<Software> softwares;

    public Computer() {
    }

    public Computer(@OS int os, String label) {
        this.os = os;
        this.label = label;
    }

    public Computer(int os, String name, List<Software> softwares) {
        this(os, name);
        this.softwares = softwares;
    }

    public static final int MAC = 1;
    public static final int WINDOWS = 2;
    public static final int LINUX = 3;

    @IntDef({MAC, WINDOWS, LINUX})
    public @interface OS {}

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public List<Software> getSoftwares() {
        return softwares;
    }

    public void setSoftwares(List<Software> softwares) {
        this.softwares = softwares;
    }

    @OS
    public int getOs() {
        return os;
    }

    public void setOs(@OS int os) {
        this.os = os;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}

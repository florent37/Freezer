package com.github.florent37.orm.model;

import fr.xebia.android.freezer.annotations.Model;

import java.util.List;

/**
 * Created by florentchampigny on 21/01/2016.
 */
@Model
public class Cat {

    int aInteger;
    Integer bInteger;
    int[] cInteger;
    Integer[] dInteger;
    List<Integer> eInteger;

    boolean aBoolean;
    Boolean bBoolean;
    boolean[] cBoolean;
    Boolean[] dBoolean;
    List<Boolean> eBoolean;

    float aFloat;
    Float bFloat;
    float[] cFloat;
    Float[] dFloat;
    List<Float> eFloat;

    double aDouble;
    Double bDouble;
    double[] cDouble;
    Double[] dDouble;
    List<Double> eDouble;

    String aString;
    String[] bString;
    List<String> cString;

    public Cat() {
    }

    public Cat(String shortName) {
        this.aString = shortName;
    }
}

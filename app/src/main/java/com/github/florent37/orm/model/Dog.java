package com.github.florent37.orm.model;

import android.os.Parcel;
import android.os.Parcelable;

import fr.xebia.android.freezer.annotations.Model;

/**
 * Created by florentchampigny on 21/01/2016.
 */
@Model
public class Dog implements Parcelable {
    public static final Creator<Dog> CREATOR = new Creator<Dog>() {
        @Override
        public Dog createFromParcel(Parcel in) {
            return new Dog(in);
        }

        @Override
        public Dog[] newArray(int size) {
            return new Dog[size];
        }
    };
    String name;

    public Dog(){}

    public Dog(String name) {
        this.name = name;
    }

    protected Dog(Parcel in) {
        name = in.readString();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
    }
}

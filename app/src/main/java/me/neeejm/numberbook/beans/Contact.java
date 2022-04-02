package me.neeejm.numberbook.beans;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class Contact implements Serializable {
    private int id;

    private String phoneNumber;

    private String name;

    public Contact(String name, String phoneNumber) {
        this.id = 0;
        this.name = name;
        this.phoneNumber = phoneNumber;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @NonNull
    @Override
    public String toString() {
        return getName() + " " + getPhoneNumber();
    }
}

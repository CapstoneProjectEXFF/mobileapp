package com.project.capstone.exchangesystem.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Category {

    @SerializedName("id")
    @Expose
    private int id;

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("suppercategoryId")
    @Expose
    private int suppercategoryId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSuppercategoryId() {
        return suppercategoryId;
    }

    public void setSuppercategoryId(int suppercategoryId) {
        this.suppercategoryId = suppercategoryId;
    }

    public Category(int id, String name, int suppercategoryId) {
        this.id = id;
        this.name = name;
        this.suppercategoryId = suppercategoryId;
    }
}

package com.project.capstone.exchangesystem.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Category  implements Serializable {

    @SerializedName("id")
    @Expose
    private int id;

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("supercategoryId")
    @Expose
    private int supercategoryId;

    private boolean checkSelectedCategory = false;

    private int numOfItem = 0;

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

    public int getSupercategoryId() {
        return supercategoryId;
    }

    public void setSupercategoryId(int supercategoryId) {
        this.supercategoryId = supercategoryId;
    }

    public Category(int id, String name, int supercategoryId) {
        this.id = id;
        this.name = name;
        this.supercategoryId = supercategoryId;
    }

    public boolean isCheckSelectedCategory() {
        return checkSelectedCategory;
    }

    public void setCheckSelectedCategory(boolean checkSelectedCategory) {
        this.checkSelectedCategory = checkSelectedCategory;
    }

    public int getNumOfItem() {
        return numOfItem;
    }

    public void setNumOfItem(int numOfItem) {
        this.numOfItem = numOfItem;
    }
}

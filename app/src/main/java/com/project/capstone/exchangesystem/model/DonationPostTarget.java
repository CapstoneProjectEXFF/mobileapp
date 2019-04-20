package com.project.capstone.exchangesystem.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class DonationPostTarget implements Serializable {

    @SerializedName("id")
    @Expose
    private int id;

    @SerializedName("donationPostId")
    @Expose
    private Integer donationPostId;

    @SerializedName("categoryId")
    @Expose
    private Integer categoryId;

    @SerializedName("target")
    @Expose
    private Integer target;

    @SerializedName("category")
    @Expose
    private Category category;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Integer getDonationPostId() {
        return donationPostId;
    }

    public void setDonationPostId(Integer donationPostId) {
        this.donationPostId = donationPostId;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public Integer getTarget() {
        return target;
    }

    public void setTarget(Integer target) {
        this.target = target;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public DonationPostTarget() {
    }
}

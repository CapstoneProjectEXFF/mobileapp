package com.project.capstone.exchangesystem.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Image {
    @SerializedName("id")
    @Expose
    private int id;

    @SerializedName("url")
    @Expose
    private String url;

    @SerializedName("itemId")
    @Expose
    private Integer itemId;

    @SerializedName("donationPostId")
    @Expose
    private Integer donationPostId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Integer getItemId() {
        return itemId;
    }

    public void setItemId(Integer itemId) {
        this.itemId = itemId;
    }

    public Integer getDonationPostId() {
        return donationPostId;
    }

    public void setDonationPostId(Integer donationPostId) {
        this.donationPostId = donationPostId;
    }

    public Image(int id, String url, Integer itemId, Integer donationPostId) {
        this.id = id;
        this.url = url;
        this.itemId = itemId;
        this.donationPostId = donationPostId;
    }
}

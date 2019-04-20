package com.project.capstone.exchangesystem.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

public class DonationPost implements Serializable {

    @SerializedName("id")
    @Expose
    private int id;

    @SerializedName("title")
    @Expose
    private String title;

    @SerializedName("content")
    @Expose
    private String content;

    @SerializedName("address")
    @Expose
    private String address;

    @SerializedName("status")
    @Expose
    private String status;

    @SerializedName("createTime")
    @Expose
    private Timestamp createTime;

    @SerializedName("modifyTime")
    @Expose
    private Timestamp modifyTime;

    @SerializedName("userId")
    @Expose
    private Integer userId;

    @SerializedName("user")
    @Expose
    private User user;

    @SerializedName("images")
    @Expose
    private List<Image> images;

    @SerializedName("targets")
    @Expose
    private List<DonationPostTarget> donationPostTargets;

    private List<Integer> imageIds;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

    public Timestamp getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(Timestamp modifyTime) {
        this.modifyTime = modifyTime;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<Image> getImages() {
        return images;
    }

    public void setImages(List<Image> images) {
        this.images = images;
    }

    public List<Integer> getImageIds() {
        return imageIds;
    }

    public void setImageIds(List<Integer> imageIds) {
        this.imageIds = imageIds;
    }

    public List<DonationPostTarget> getDonationPostTargets() {
        return donationPostTargets;
    }

    public void setDonationPostTargets(List<DonationPostTarget> donationPostTargets) {
        this.donationPostTargets = donationPostTargets;
    }
}


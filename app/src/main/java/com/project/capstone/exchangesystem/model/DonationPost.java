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

    @SerializedName("content")
    @Expose
    private String content;

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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
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
}

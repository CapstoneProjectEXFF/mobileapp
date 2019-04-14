package com.project.capstone.exchangesystem.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

public class Item implements Serializable {

    @SerializedName("id")
    @Expose
    private int id;

    @SerializedName("name")
    @Expose
    private String name;


    @SerializedName("description")
    @Expose
    private String description;

    @SerializedName("address")
    @Expose
    private String address;

    @SerializedName("status")
    @Expose
    private String status;

    @SerializedName("privacy")
    @Expose
    private String privacy;

    @SerializedName("createTime")
    @Expose
    private Timestamp createTime;

    @SerializedName("modifyTime")
    @Expose
    private Timestamp modifyTime;

    @SerializedName("category")
    @Expose
    private Category category;


    @SerializedName("user")
    @Expose
    private User user;

    @SerializedName("images")
    @Expose
    private List<Image> images;

    private List<Integer> imageIds;

    private boolean checkPrivacy = false;

    public String getPrivacy() {
        return privacy;
    }

    public void setPrivacy(String privacy) {
        this.privacy = privacy;
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setImage(List<Image> images) {
        this.images = images;
    }

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public List<Image> getImage() {
        return images;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
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

    public boolean isCheckPrivacy() {
        return checkPrivacy;
    }

    public void setCheckPrivacy(boolean checkPrivacy) {
        this.checkPrivacy = checkPrivacy;
    }

    public Item(int id, String name, String description, String address, String status, String privacy, Timestamp createTime, Timestamp modifyTime, Category category, User user, List<Image> images) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.address = address;
        this.status = status;
        this.privacy = privacy;
        this.createTime = createTime;
        this.modifyTime = modifyTime;
        this.category = category;
        this.user = user;
        this.images = images;
    }

    public Item(){}
}

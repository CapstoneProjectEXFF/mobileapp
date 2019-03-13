package com.project.capstone.exchangesystem.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

public class Transaction implements Serializable {

    @SerializedName("id")
    @Expose
    private int id;

    @SerializedName("senderId")
    @Expose
    private Integer senderId;


    @SerializedName("receiverId")
    @Expose
    private Integer receiverId;

    @SerializedName("donationPostId")
    @Expose
    private Integer donationPostId;

    @SerializedName("status")
    @Expose
    private String status;

    @SerializedName("createTime")
    @Expose
    private Timestamp createTime;


    @SerializedName("modifyTime")
    @Expose
    private Timestamp modifyTime;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Integer getSenderId() {
        return senderId;
    }

    public void setSenderId(Integer senderId) {
        this.senderId = senderId;
    }

    public Integer getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(Integer receiverId) {
        this.receiverId = receiverId;
    }

    public Integer getDonationPostId() {
        return donationPostId;
    }

    public void setDonationPostId(Integer donationPostId) {
        this.donationPostId = donationPostId;
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

    public Transaction(int id, int senderId, int receiverId, int donationPostId, String status, Timestamp createTime, Timestamp modifyTime) {
        this.id = id;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.donationPostId = donationPostId;
        this.status = status;
        this.createTime = createTime;
        this.modifyTime = modifyTime;
    }
}

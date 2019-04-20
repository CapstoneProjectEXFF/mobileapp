package com.project.capstone.exchangesystem.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Rate implements Serializable {

    @SerializedName("id")
    @Expose
    private int id;

    @SerializedName("senderId")
    @Expose
    private Integer senderId;

    @SerializedName("receiverId")
    @Expose
    private Integer receiverId;

    @SerializedName("content")
    @Expose
    private String content;

    @SerializedName("rate")
    @Expose
    private Integer rate;

    @SerializedName("sender")
    @Expose
    private User sender;

    @SerializedName("receiver")
    @Expose
    private User receiver;

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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getRate() {
        return rate;
    }

    public void setRate(Integer rate) {
        this.rate = rate;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public User getReceiver() {
        return receiver;
    }

    public void setReceiver(User receiver) {
        this.receiver = receiver;
    }

    public Rate(int id, Integer senderId, Integer receiverId, String content, Integer rate, User sender, User receiver) {
        this.id = id;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.content = content;
        this.rate = rate;
        this.sender = sender;
        this.receiver = receiver;
    }

    public Rate() {
    }
}

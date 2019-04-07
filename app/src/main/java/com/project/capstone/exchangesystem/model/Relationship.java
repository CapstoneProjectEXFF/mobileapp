package com.project.capstone.exchangesystem.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Relationship implements Serializable {

    @SerializedName("id")
    @Expose
    private int id;

    @SerializedName("senderId")
    @Expose
    private Integer senderId;


    @SerializedName("receiverId")
    @Expose
    private Integer receiverId;


    @SerializedName("status")
    @Expose
    private String status;

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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public Relationship(int id, Integer senderId, Integer receiverId, String status, User sender, User receiver) {
        this.id = id;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.status = status;
        this.sender = sender;
        this.receiver = receiver;
    }

    public Relationship() {

    }
}

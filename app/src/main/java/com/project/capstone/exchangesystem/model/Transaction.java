package com.project.capstone.exchangesystem.model;

import android.support.annotation.NonNull;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

public class Transaction implements Serializable, Comparable<Transaction> {

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

    @SerializedName("qrCode")
    @Expose
    private String qrCode;

    @SerializedName("sender")
    @Expose
    private User sender;

    @SerializedName("receiver")
    @Expose
    private User receiver;

    @SerializedName("senderReceipt")
    @Expose
    private String senderReceipt;

    @SerializedName("receiverReceipt")
    @Expose
    private String receiverReceipt;


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

    public String getQrCode() {
        return qrCode;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }

    public String getSenderReceipt() {
        return senderReceipt;
    }

    public void setSenderReceipt(String senderReceipt) {
        this.senderReceipt = senderReceipt;
    }

    public String getReceiverReceipt() {
        return receiverReceipt;
    }

    public void setReceiverReceipt(String receiverReceipt) {
        this.receiverReceipt = receiverReceipt;
    }

    public Transaction(int id, int senderId, int receiverId, int donationPostId, String status, Timestamp createTime, Timestamp modifyTime, String qrCode, String senderReceipt, String receiverReceipt) {
        this.id = id;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.donationPostId = donationPostId;
        this.status = status;
        this.createTime = createTime;
        this.modifyTime = modifyTime;
        this.qrCode = qrCode;
        this.senderReceipt = senderReceipt;
        this.receiverReceipt = receiverReceipt;
    }

    public Transaction() {

    }

    @Override
    public int compareTo(@NonNull Transaction o) {
        return this.getCreateTime().compareTo(getCreateTime());
    }
}

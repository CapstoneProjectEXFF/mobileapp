package com.project.capstone.exchangesystem.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class NotiTransactionItem implements Serializable {

    @SerializedName("_id")
    @Expose
    private String id;

    @SerializedName("receiverId")
    @Expose
    private List<String> receiverId;

    @SerializedName("msg")
    @Expose
    private String msg;

    @SerializedName("notiType")
    @Expose
    private int notiType;

    @SerializedName("status")
    @Expose
    private int status;

    public void setReceiverId(List<String> receiverId) {
        this.receiverId = receiverId;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public void setNotiType(int notiType) {
        this.notiType = notiType;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public List<String> getReceiverId() {
        return receiverId;
    }

    public String getMsg() {
        return msg;
    }

    public int getNotiType() {
        return notiType;
    }

    public int getStatus() {
        return status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}

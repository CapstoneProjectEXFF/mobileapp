package com.project.capstone.exchangesystem.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class NotiTransaction implements Serializable {

    @SerializedName("notification")
    @Expose
    private NotiTransactionItem notification;

    @SerializedName("user")
    @Expose
    private List<UserNoti> users;

    public NotiTransactionItem getNotification() {
        return notification;
    }

    public void setNotification(NotiTransactionItem notification) {
        this.notification = notification;
    }

    public List<UserNoti> getUsers() {
        return users;
    }

    public void setUsers(List<UserNoti> users) {
        this.users = users;
    }
}

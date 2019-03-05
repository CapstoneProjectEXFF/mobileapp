package com.project.capstone.exchangesystem.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class User {

    @SerializedName("id")
    @Expose
    private int id;

    @SerializedName("phoneNumber")
    @Expose
    private String phone;

//    @SerializedName("password")
//    @Expose
//    private String password;

    @SerializedName("fullName")
    @Expose
    private String fullName;

    @SerializedName("avatar")
    @Expose
    private String avatar;


    @SerializedName("status")
    @Expose
    private String status;

    @SerializedName("roleByRoleId")
    @Expose
    private Role roleByRoleId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

//    public String getPassword() {
//        return password;
//    }
//
//    public void setPassword(String password) {
//        this.password = password;
//    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Role getRoleByRoleId() {
        return roleByRoleId;
    }

    public void setRoleByRoleId(Role roleByRoleId) {
        this.roleByRoleId = roleByRoleId;
    }

    public User(int id, String phone,  String fullName, String avatar, String status, Role roleByRoleId) {
        this.id = id;
        this.phone = phone;
//        this.password = password;
        this.fullName = fullName;
        this.avatar = avatar;
        this.status = status;
        this.roleByRoleId = roleByRoleId;
    }

    public User() {
    }
}

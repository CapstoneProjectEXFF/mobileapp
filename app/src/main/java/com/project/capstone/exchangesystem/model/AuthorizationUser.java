package com.project.capstone.exchangesystem.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class AuthorizationUser implements Serializable {
    @SerializedName("Authorization")
    @Expose
    private String authorization;

    @SerializedName("User")
    @Expose
    private User user;

    public String getAuthorization() {
        return authorization;
    }

    public void setAuthorization(String authorization) {
        this.authorization = authorization;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public AuthorizationUser(String authorization, User user) {
        this.authorization = authorization;
        this.user = user;
    }
}

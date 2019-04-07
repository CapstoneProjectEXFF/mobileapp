package com.project.capstone.exchangesystem.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class Room implements Serializable {

//    @SerializedName("_id")
//    @Expose
//    private String _id;

    @SerializedName("users")
    @Expose
    private List<UserRoom> users;

    @SerializedName("messages")
    @Expose
    private List<Message> messages;

    @SerializedName("room")
    @Expose
    private String room;

    @SerializedName("status")
    @Expose
    private int status;

    public List<UserRoom> getUsers() {
        return users;
    }

    public void setUsers(List<UserRoom> users) {
        this.users = users;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}

package com.project.capstone.exchangesystem.model;

import android.support.annotation.Nullable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class TransactionDetail implements Serializable {

    @SerializedName("id")
    @Expose
    private int id;

    @SerializedName("transactionId")
    @Expose
    private Integer transactionId;


    @SerializedName("itemId")
    @Expose
    private Integer itemId;

    @SerializedName("userId")
    @Expose
    private Integer userId;

    @SerializedName("item")
    @Expose
    private Item item;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Integer getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(Integer transactionId) {
        this.transactionId = transactionId;
    }

    public Integer getItemId() {
        return itemId;
    }

    public void setItemId(Integer itemId) {
        this.itemId = itemId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public TransactionDetail(int id, Integer transactionId, Integer itemId, Integer userId, Item item) {
        this.id = id;
        this.transactionId = transactionId;
        this.itemId = itemId;
        this.userId = userId;
        this.item = item;
    }

    public TransactionDetail() {
    }
}

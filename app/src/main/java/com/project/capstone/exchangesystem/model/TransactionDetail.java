package com.project.capstone.exchangesystem.model;

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

    public TransactionDetail(int id, Integer transactionId, Integer itemId) {
        this.id = id;
        this.transactionId = transactionId;
        this.itemId = itemId;
    }
}

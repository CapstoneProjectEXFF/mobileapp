package com.project.capstone.exchangesystem.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DonationPostWrapper implements Serializable {

    @SerializedName("donationPost")
    @Expose
    private DonationPost donationPost;

    @SerializedName("urls")
    @Expose
    private List<String> urls;

    @SerializedName("newUrls")
    @Expose
    private List<String> newUrls;

    @SerializedName("removedUrlIds")
    @Expose
    private List<Integer> removedUrlIds;

    @SerializedName("targets")
    @Expose
    private ArrayList<DonationPostTarget> targets;

    @SerializedName("removeTargets")
    @Expose
    private ArrayList<Integer> removeTargets;

    public DonationPost getDonationPost() {
        return donationPost;
    }

    public void setDonationPost(DonationPost donationPost) {
        this.donationPost = donationPost;
    }

    public List<String> getUrls() {
        return urls;
    }

    public void setUrls(List<String> urls) {
        this.urls = urls;
    }

    public List<String> getNewUrls() {
        return newUrls;
    }

    public void setNewUrls(List<String> newUrls) {
        this.newUrls = newUrls;
    }

    public List<Integer> getRemovedUrlIds() {
        return removedUrlIds;
    }

    public void setRemovedUrlIds(List<Integer> removedUrlIds) {
        this.removedUrlIds = removedUrlIds;
    }

    public ArrayList<DonationPostTarget> getTargets() {
        return targets;
    }

    public void setTargets(ArrayList<DonationPostTarget> targets) {
        this.targets = targets;
    }

    public ArrayList<Integer> getRemoveTargets() {
        return removeTargets;
    }

    public void setRemoveTargets(ArrayList<Integer> removeTargets) {
        this.removeTargets = removeTargets;
    }
}

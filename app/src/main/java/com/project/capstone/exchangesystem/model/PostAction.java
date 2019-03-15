package com.project.capstone.exchangesystem.model;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;
import com.google.gson.internal.LinkedTreeMap;
import com.project.capstone.exchangesystem.Activity.MainActivity;
import com.project.capstone.exchangesystem.Activity.OwnInventory;
import com.project.capstone.exchangesystem.Activity.UpdateDonationPostActivity;
import com.project.capstone.exchangesystem.Activity.UpdateItemActivity;
import com.project.capstone.exchangesystem.Utils.RmaAPIUtils;
import com.project.capstone.exchangesystem.fragment.MainCharityPostFragment;
import com.project.capstone.exchangesystem.model.DonationPost;
import com.project.capstone.exchangesystem.model.Item;
import com.project.capstone.exchangesystem.remote.RmaAPIService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;
import static com.project.capstone.exchangesystem.constants.AppStatus.*;

public class PostAction {
    RmaAPIService rmaAPIService = RmaAPIUtils.getAPIService();
    ProgressDialog progressDialog;
    public PostAction() {
    }

    public void manageItem(Item item, List<String> urlList, String authorization, final Context context, int action) {

        setDialog(context);

        String itemName = item.getName();
        String itemDes = item.getDescription();
        String itemAddress = item.getAddress();
        String privacy = item.getPrivacy();
        int category = item.getCategory().getId();
        final Map<String, Object> jsonBody = new HashMap<String, Object>();

        jsonBody.put("name", itemName);
        jsonBody.put("description", itemDes);
        jsonBody.put("address", itemAddress);
        jsonBody.put("privacy", privacy);
        jsonBody.put("category", "" + (category + 1));
//        jsonBody.put("urls", urlList);

        if (authorization != null) {
            if (action == ITEM_CREATE_ACTION) {
                jsonBody.put("urls", urlList);
                rmaAPIService.createItem(jsonBody, authorization).enqueue(new Callback<Item>() {

                    @Override
                    public void onResponse(Call<Item> call, Response<Item> response) {
                        if (response.isSuccessful()) {
                            if (response.body() != null) {
                                Log.i("PostAction", "item added");
                                progressDialog.dismiss();
                                //go to update screen
                                Intent intent = new Intent(context, OwnInventory.class);
//                                Intent intent = new Intent(context, UpdateItemActivity.class);
//                                intent.putExtra("itemId", response.body().getId());
                                context.startActivity(intent);
                            } else {
                                Log.i("PostAction", "item create null");
                            }
                        } else {
                            Log.i("PostAction", "item create error " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(Call<Item> call, Throwable t) {
                        Log.i("PostAction", "item create failed");
                    }
                });
            } else if (action == ITEM_UPDATE_ACTION){
                rmaAPIService.updateItem(jsonBody, authorization, item.getId()).enqueue(new Callback<Object>() {

                    @Override
                    public void onResponse(Call<Object> call, Response<Object> response) {
                        if (response.isSuccessful()) {
                            if (response.body() != null) {
                                progressDialog.dismiss();
                                Log.i("PostAction", "item updated");
                                //go to main
//                                Intent intent = new Intent(context, MainActivity.class);
                                Intent intent = new Intent(context, OwnInventory.class);
                                context.startActivity(intent);
                            } else {
                                Log.i("PostAction", "item update null");
                            }
                        } else {
                            Log.i("PostAction", "item update error " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(Call<Object> call, Throwable t) {
                        Log.i("PostAction", "item update failed");
                    }
                });
            }
        }
    }

    public void manageDonation(DonationPost donationPost, List<String> urlList, String authorization, final Context context, int action) {

        setDialog(context);

        String content = donationPost.getContent();
        String address = donationPost.getAddress();
        final Map<String, Object> jsonBody = new HashMap<String, Object>();

        jsonBody.put("content", content);
        jsonBody.put("address", address);
//        jsonBody.put("urls", urlList);

        if (authorization != null) {
            if (action == DONATION_CREATE_ACTION) {
                jsonBody.put("urls", urlList);
                rmaAPIService.createDonationPost(jsonBody, authorization).enqueue(new Callback<DonationPost>() {

                    @Override
                    public void onResponse(Call<DonationPost> call, Response<DonationPost> response) {
                        if (response.isSuccessful()) {
                            if (response.body() != null) {
                                progressDialog.dismiss();
                                Log.i("PostAction", "donation added");
                                //go to update screen
                                Intent intent = new Intent(context, UpdateDonationPostActivity.class);
                                intent.putExtra("donationPostId", response.body().getId());
                                context.startActivity(intent);
                            } else {
                                Log.i("PostAction", "donation create null");
                            }
                        } else {
                            Log.i("PostAction", "donation create error " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(Call<DonationPost> call, Throwable t) {
                        Log.i("PostAction", "donation create failed");
                    }
                });
            } else if (action == DONATION_UPDATE_ACTION) {
                int donationPostId = donationPost.getId();
                rmaAPIService.updateDonationPost(jsonBody, authorization, donationPostId).enqueue(new Callback<Object>() {

                    @Override
                    public void onResponse(Call<Object> call, Response<Object> response) {
                        if (response.isSuccessful()) {
                            if (response.body() != null) {
                                Log.i("PostAction", "donation updated");
                                //go to main
                                Intent intent = new Intent(context, MainActivity.class);
                                context.startActivity(intent);
                            } else {
                                Log.i("PostAction", "donation update null");
                            }
                        } else {
                            Log.i("PostAction", "donation update error " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(Call<Object> call, Throwable t) {
                        Log.i("PostAction", "donation update failed");
                    }
                });
            }
        }
    }

    private void setDialog(Context context) {
        progressDialog = new ProgressDialog(context);
        progressDialog.setTitle("Đang tải dữ liệu...");
        progressDialog.setMessage("Vui lòng chờ...");
        progressDialog.show();
    }

    public void manageUser(final User user, final String url, String authorization, final Context context, int action, final SharedPreferences.Editor editor) {
        setDialog(context);

        int id = user.getId();
        String phoneNumber = user.getPhone();
        final String fullName = user.getFullName();
        String status = user.getStatus();

        final Map<String, String> jsonBody = new HashMap<String, String>();
        jsonBody.put("id", "" + id);
        jsonBody.put("phoneNumber", phoneNumber);
        jsonBody.put("fullName", fullName);
        jsonBody.put("avatar", url);
        jsonBody.put("status", status);

        if (action == USER_UPDATE_ACTION) {
            rmaAPIService.updateInfo(jsonBody, authorization).enqueue(new Callback<Object>() {

                @Override
                public void onResponse(Call<Object> call, Response<Object> response) {

                    System.out.println(response.isSuccessful());

                    if (response.isSuccessful()) {

                        LinkedTreeMap<String, Object> responeBody = (LinkedTreeMap<String, Object>) response.body();
                        if (responeBody.containsKey("User")) {
                            Toast.makeText(context, "Change UserProfile Succesfully", Toast.LENGTH_SHORT).show();
                            editor.putString("fullname", fullName);
                            editor.putString("avatar", url);
                            editor.commit();
                            progressDialog.dismiss();
                        }
                    } else {
                        System.out.println(response.body());
                        LinkedTreeMap<String, String> responeBody = (LinkedTreeMap<String, String>) response.body();
                        if (responeBody.containsKey("message")) {
                            Toast.makeText(context, responeBody.get("message").toString(), Toast.LENGTH_SHORT).show();
                            System.out.println(responeBody.get("message").toString());
                            System.out.println(responeBody.get("message").toString());
                        }
                        progressDialog.dismiss();
                    }
                }
                //TODO: updating...

                @Override
                public void onFailure(Call<Object> call, Throwable t) {
                    System.out.println("Fail rồi");
                    Toast.makeText(context, t.getMessage(), Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            });
        }
    }
}
package com.project.capstone.exchangesystem.remote;

import com.project.capstone.exchangesystem.model.*;
import retrofit2.Call;
import retrofit2.http.*;

import java.util.List;
import java.util.Map;

public interface RmaAPIService {

    @GET("/user/{id}")
    Call<User> getUserById(@Path("id") Integer userId);

    @POST("/login")
//    @FormUrlEncoded
//    Call<Object> login(@Field("phoneNumber") String phone, @Field("password") String password);
//    Call<User> login(Req)
    Call<Object> login(@Body Map<String, String> body);

    @POST("/register")
    Call<Object> register(@Body Map<String, String> body);

    @GET("/phone")
    Call<Object> checkValidationLogin(@Query("phone") String phone);

    @GET("/item")
    Call<List<Item>> getAllItems(@Header("Authorization") String authorization);

    @GET("/user/{id}/item")
    Call<List<Item>> getItemsByUserId(@Path("id") int userID);

    @GET("/itemSearch")
    Call<List<Item>> findItems(@Query("name") String name);

    @GET("/category")
    Call<List<Category>> getAllCategory();

    @POST("/item")
    Call<Item> createItem(@Body Map<String, Object> body, @Header("Authorization") String authorization);

    @POST("/user/changePassword")
    Call<Object> changePassword(@Body Map<String, String> body, @Header("Authorization") String authorization);

    @POST("/user/updateInfo")
    Call<Object> updateInfo(@Body Map<String, String> body, @Header("Authorization") String authorization);

    @PUT("/item/{id}")
    Call<Object> updateItem(@Body Map<String, String> body, @Header("Authorization") String authorization, @Path("id") int itemId);

    @GET("/item/{id}")
    Call<Item> getItemById(@Header("Authorization") String authorization, @Path("id") int itemId);


    @POST("/donationPost")
    Call<DonationPost> createDonationPost(@Body Map<String, Object> body, @Header("Authorization") String authorization);

    @PUT("/donationPost/{id}")
    Call<Object> updateDonationPost(@Body Map<String, Object> body, @Header("Authorization") String authorization, @Path("id") int donationPostId);

    @GET("/donationPost/{id}")
    Call<DonationPost> getDonationPostById(@Header("Authorization") String authorization, @Path("id") int donationPostId);

    @GET("/image/{itemId}")
    Call<List<Image>> getImagesByItemId(@Header("Authorization") String authorization, @Path("itemId") int itemId);

    @POST("/transaction")
    Call<Object> sendTradeRequest(@Header("Authorization") String authorization, @Body TransactionRequestWrapper body);

    @GET("/donationPost")
    Call<List<DonationPost>> getDonationPost(@Query("page") int page, @Query("size") int size);

}


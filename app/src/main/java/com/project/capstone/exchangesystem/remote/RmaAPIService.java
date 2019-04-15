package com.project.capstone.exchangesystem.remote;

import com.project.capstone.exchangesystem.model.*;
import retrofit2.Call;
import retrofit2.http.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public interface RmaAPIService {

    @GET("/user/{id}")
    Call<User> getUserById(@Path("id") Integer userId);

    @POST("/login")
//    @FormUrlEncoded
//    Call<Object> login(@Field("phoneNumber") String phone, @Field("password") String password);
//    Call<User> login(Req)
    Call<AuthorizationUser> login(@Body Map<String, String> body);

    @POST("/register")
    Call<Object> register(@Body Map<String, String> body);

    @GET("/phone")
    Call<Object> checkValidationLogin(@Query("phone") String phone);

//    @GET("/item")
//    Call<List<Item>> getAllItems(@Header("Authorization") String authorization);

    @GET("/item")
    Call<List<Item>> getAllItemsWithPrivacy(@Header("Authorization") String authorization);

    @GET("/user/{id}/item")
    Call<List<Item>> getItemsByUserId(@Path("id") int userID);

    @GET("/user/{id}/item")
    Call<List<Item>> getItemsByUserIdWithPrivacy(@Header("Authorization") String authorization, @Path("id") int userID);

    @GET("/user/my/item")
    Call<List<Item>> getMyItems(@Header("Authorization") String authorization);

    @GET("/itemSearch")
    Call<ArrayList<Item>> findItems(@Query("name") String name);

    @GET("/item/search")
    Call<ArrayList<Item>> findItemsByNameAndCategoryWithPrivacy(@Header("Authorization") String authorization, @Query("name") String name, @Query("categoryId") int categoryId);

    @GET("/category")
    Call<List<Category>> getAllCategory();


    @POST("/item")
    Call<Item> createItem(@Body Map<String, Object> body, @Header("Authorization") String authorization);

    @POST("/user/changePassword")
    Call<Object> changePassword(@Body Map<String, String> body, @Header("Authorization") String authorization);

    @POST("/user/updateInfo")
    Call<Object> updateInfo(@Body Map<String, String> body, @Header("Authorization") String authorization);

    @PUT("/item/{id}")
    Call<Object> updateItem(@Body Map<String, Object> body, @Header("Authorization") String authorization, @Path("id") int itemId);

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

    @GET("/item")
    Call<List<Item>> getAllItemsWithPrivacy(@Header("Authorization") String authorization, @Query("page") int page, @Query("size") int size);

    @GET("/item/{itemId}")
    Call<Item> getItemById(@Path("itemId") int id);

    @GET("/transaction")
    Call<List<Transaction>> getTransactionsByReceiverID(@Header("Authorization") String authorization);

    @GET("/transaction/confirm")
    Call<List<Transaction>> getTransactionsTradedBySenderId(@Header("Authorization") String authorization);

    @GET("/transaction/donation")
    Call<List<TransactionRequestWrapper>> getDonationnTransactionByAgentID(@Header("Authorization") String authorization);


    @GET("/transaction/{transID}")
    Call<TransactionRequestWrapper> getTransactionByTransID(@Header("Authorization") String authorization, @Path("transID") int transID);

    @PUT("/transaction/{transID}")
    Call<Object> confirmTransaction(@Header("Authorization") String authorization, @Path("transID") int transID);

    @PUT("/transaction")
    Call<Object> updateTransaction(@Header("Authorization") String authorization, @Body TransactionRequestWrapper body);

    @GET("/donators/{donationPostId}")
    Call<List<TransactionRequestWrapper>> getTransactionByDonationPostId(@Path("donationPostId") int donationPostId);

    @GET("/transaction/history")
    Call<List<Transaction>> getAllTransactionByUserID(@Header("Authorization") String authorization);

    @GET("/transaction/history/count")
    Call<Integer> countAllTransactionByUserId(@Header("Authorization") String authorization);

//    @HTTP(method = "DELETE", path = "/transaction/{transID}", hasBody = true)
//    Call<Object> cancelTransactionByID(@Header("Authorization") String authorization, @Path("transID") int transID);


    @DELETE("/transaction/{transID}")
    Call<Object> cancelTransactionByID(@Header("Authorization") String authorization, @Path("transID") int transID);

    @DELETE("/donationPost/{donationId}")
    Call<Object> removeDonationPost(@Header("Authorization") String authorization, @Path("donationId") int donationId);

    @GET("/trading")
    Call<List<Room>> loadRoomByUserId(@Query("userId") int userId);

    @GET("/room")
    Call<Room> loadRoom(@Query("room") String roomName);

    @DELETE("/relationship/{id}")
    Call<ExffMessage> cancelFriendRequest(@Header("Authorization") String authorization, @Path("id") int id);

    @HTTP(method = "DELETE", path = "/relationship", hasBody = true)
    Call<ExffMessage> unfriend(@Header("Authorization") String authorization, @Body Map<String, String> body);

    @GET("/user")
    Call<List<User>> getAllUser(@Header("Authorization") String authorization);

    @POST("/relationship")
    Call<Relationship> addFriend(@Header("Authorization") String authorization, @Body Map<String, String> body);

    @GET("/relationship")
    Call<List<Relationship>> getFriendRequest(@Header("Authorization") String authorization, @Query("page") int page, @Query("size") int size);

    @PUT("/relationship")
    Call<ExffMessage> acceptFriend(@Header("Authorization") String authorization, @Body Map<String, String> body);

    @GET("/relationship/friend")
    Call<List<Relationship>> getFriendListByUserId(@Header("Authorization") String authorization);

    @GET("/relationship/friend/count")
    Call<Integer> countFriendByUserId(@Header("Authorization") String authorization);

    @POST("/relationship/contact")
    Call<List<User>> getNotFriendFromContact(@Header("Authorization") String authorization, @Body ArrayList<String> body);

    @GET("/relationship/explore")
    Call<List<User>> getNewFriendToAdd(@Header("Authorization") String authorization);

    @GET("/getdonationPost/{userId}")
    Call<List<DonationPost>> getDonationPostByUserId(@Path("userId") int userId);

    @DELETE("/item/{itemId}")
    Call<Object> deleteItemWithId(@Header("Authorization") String authorization, @Path("itemId") int itemId);

    @POST("/rating")
    Call<Rate> createRating(@Header("Authorization") String authorization, @Body Rate rate);

    @GET("/rating/{userId}")
    Call<List<Rate>> getRating(@Path("userId") int userId);
}

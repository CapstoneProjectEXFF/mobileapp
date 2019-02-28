package com.project.capstone.exchangesystem.remote;

import com.project.capstone.exchangesystem.model.Item;
import com.project.capstone.exchangesystem.model.User;
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


    @GET("/itemSearch")
    Call<List<Item>> findItems(@Query("name") String name);
}


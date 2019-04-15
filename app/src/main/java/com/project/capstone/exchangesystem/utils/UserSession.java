package com.project.capstone.exchangesystem.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import com.project.capstone.exchangesystem.model.User;

public class UserSession {
    SharedPreferences sharedPreferences;
    Editor editor;
    Context context;

    int PRIVATE_MODE = 0;

    public static final String PREFER_NAME = "localData";
    public static final String IS_USER_LOGIN = "IsUserLoggedIn";
    public static final String KEY_NAME = "username";
    public static final String KEY_AUTHORIZATION = "authorization";
    public static final String KEY_PHONE = "phoneNumberSignIn";
    public static final String KEY_AVATAR = "avatar";
    public static final String KEY_ADDRESS = "address";
    public static final String KEY_ID = "userId";
    public static final String KEY_STATUS = "status";


    public UserSession(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(PREFER_NAME, PRIVATE_MODE);
        editor = sharedPreferences.edit();
    }

    public void createUserLoginSession(User user, String authorization) {
        editor.putBoolean(IS_USER_LOGIN, true);
        editor.putString(KEY_NAME, user.getFullName());
        editor.putString(KEY_ADDRESS, user.getAddress());
        editor.putInt(KEY_ID, user.getId());
        editor.putString(KEY_AVATAR, user.getAvatar());
        editor.putString(KEY_PHONE, user.getPhone());
        editor.putString(KEY_STATUS, user.getStatus());
        editor.putString(KEY_AUTHORIZATION, authorization);


        editor.commit();
    }


    public boolean checkLogin() {
        if (!this.isUserLoggedIn()) {
            return true;
        }
        return false;
    }

    public boolean isUserLoggedIn() {
        return sharedPreferences.getBoolean(IS_USER_LOGIN, false);
    }

}

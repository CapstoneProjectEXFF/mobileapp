package com.project.capstone.exchangesystem.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import com.project.capstone.exchangesystem.R;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class WelcomeActivity extends AppCompatActivity {

    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        context = this;

        SharedPreferences sharedPreferences = getSharedPreferences("localData", MODE_PRIVATE);
        String userPhoneNumber = sharedPreferences.getString("phoneNumberSignIn", "Non");
        if (!userPhoneNumber.equals("Non")) {
//            Intent intent = new Intent(context, )
        }
    }


    public void toSignUp(View view) {
        Intent intent = new Intent(this, SignUpAcitivity.class);
        startActivity(intent);
    }

    public void toSignIn(View view) {
        Intent intent = new Intent(this, SignInActivity.class);
        startActivity(intent);
    }


}
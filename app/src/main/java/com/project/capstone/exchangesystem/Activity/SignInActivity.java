package com.project.capstone.exchangesystem.Activity;

import com.project.capstone.exchangesystem.Utils.RmaAPIUtils;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.internal.LinkedTreeMap;
import com.project.capstone.exchangesystem.R;
import com.project.capstone.exchangesystem.remote.RmaAPIService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.project.capstone.exchangesystem.service.UserService;

import java.util.HashMap;
import java.util.Map;

public class SignInActivity extends AppCompatActivity {
    Context context;
    EditText txtPhone, txtPassword;
    ProgressDialog progressDialog;
    TextView lbl_toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        lbl_toolbar = findViewById(R.id.lbl_toolbar);
        lbl_toolbar.setText("Đăng Nhập");
        lbl_toolbar.setTypeface(null, Typeface.BOLD);

        context = this;
        txtPhone = findViewById(R.id.txtPhone);
        txtPassword = findViewById(R.id.txtPassword);
        progressDialog = UserService.setUpProcessDialog(context);


    }

    public void signIn(View view) {
        RmaAPIService rmaAPIService = RmaAPIUtils.getAPIService();
        final String phone = txtPhone.getText().toString();
        String password = txtPassword.getText().toString();
        final Map<String, String> jsonBody = new HashMap<String, String>();
        jsonBody.put("phoneNumber", phone);
        jsonBody.put("password", password);
        progressDialog.show();
        rmaAPIService.login(jsonBody).enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                progressDialog.cancel();
                Toast.makeText(getApplicationContext(), "Vào rồi", Toast.LENGTH_LONG).show();
                System.out.println("successfull: " + response.isSuccessful());
                System.out.println("body " + response.body());
                if (response.isSuccessful()) {
                    System.out.println(response.body().toString());
                    progressDialog.cancel();


                    if (response.body() != null) {

                        try {

                            LinkedTreeMap<String, Object> responeBody = (LinkedTreeMap<String, Object>) response.body();

                            String authorization = (String) responeBody.get("Authorization");
                            System.out.println(authorization);

                            LinkedTreeMap<String, Object> userInfo = (LinkedTreeMap<String, Object>) responeBody.get("User");

                            Double id = (Double) userInfo.get("id");
                            System.out.println(id);

                            String phoneNumber = (String) userInfo.get("phoneNumber");
                            System.out.println(phoneNumber);

                            String fullName = (String) userInfo.get("fullName");
                            System.out.println(fullName);

                            String status = (String) userInfo.get("status");
                            System.out.println(status);
//
//
//
                            if (status.equals("1")) {
                                SharedPreferences.Editor editor = getSharedPreferences("localData", MODE_PRIVATE).edit();
                                editor.putString("phoneNumberSignIn", phoneNumber);
                                editor.putString("userId", id.toString());
                                editor.putString("username", fullName);
                                editor.putString("authorization", authorization);
                                editor.commit();


                                // login thẳng vào Main
                                Intent intent = new Intent(context, MainActivity.class);
                                startActivity(intent);
                            } else {
                                Intent intent = new Intent(context, VerifyActivity.class);
                                intent.putExtra("phoneNumber", phoneNumber);
                                intent.putExtra("type", "create-account");
                                intent.putExtra("userId", id);
                                intent.putExtra("userName", fullName);
                                startActivity(intent);
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    } else {
                        Toast.makeText(getApplicationContext(), "Số điện thoại hoặc mật khẩu không đúng! Đăng Nhập Lại", Toast.LENGTH_LONG).show();
                        txtPhone.setText("");
                        txtPassword.setText("");
                    }

                }

            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                progressDialog.cancel();
                Toast toast = Toast.makeText(getApplicationContext(), "Fail", Toast.LENGTH_SHORT);
                System.out.println("message from failure: " + t.getMessage());
                toast.show();
            }
        });
    }

    public void toSignUp(View view) {
        Intent intent = new Intent(this, SignUpAcitivity.class);
        startActivity(intent);

    }

    public void toResetPassword(View view) {
        Intent intent = new Intent(this, ForgetPasswordActivity.class);
        startActivity(intent);
    }

    public void onBackButton(View view) {
        finish();
    }
}

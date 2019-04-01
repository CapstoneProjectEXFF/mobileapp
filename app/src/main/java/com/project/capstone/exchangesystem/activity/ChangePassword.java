package com.project.capstone.exchangesystem.activity;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
//import android.support.v7.appcompat.
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.google.gson.internal.LinkedTreeMap;
import com.project.capstone.exchangesystem.R;
import com.project.capstone.exchangesystem.utils.RmaAPIUtils;
import com.project.capstone.exchangesystem.remote.RmaAPIService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.HashMap;
import java.util.Map;

public class ChangePassword extends AppCompatActivity {
    Toolbar toolbar;
    EditText txtConfirmPass, txtOldPass, txtNewPass;
    boolean flag1 = true, flag2 = true, flag3 = true, flag4 = true, flag5 = true;
    String toastText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        direct();
        ActionToolbar();

    }

    private void ActionToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void direct() {
        toolbar = findViewById(R.id.changepasswordToolbar);
        txtNewPass = findViewById(R.id.txtNewPass);
        txtOldPass = findViewById(R.id.txtOldPass);
        txtConfirmPass = findViewById(R.id.txtConfirmPass);
    }

    public void ChangePassword(View view) {
        toastText = "";
        String oldPass = txtOldPass.getText().toString();
        String newPass = txtNewPass.getText().toString();
        String confirmNewPass = txtConfirmPass.getText().toString();

        SharedPreferences sharedPreferences = getSharedPreferences("localData", MODE_PRIVATE);
        String userPhoneNumber = sharedPreferences.getString("phoneNumberSignIn", "Non");
        String authorization = sharedPreferences.getString("authorization", null);


        if (oldPass.length() < 6) {
            flag1 = false;
            Toast.makeText(getApplicationContext(), "Old Password is not validate\n", Toast.LENGTH_LONG).show();
        } else {

        }


        if (newPass.length() < 6) {
            flag2 = false;
            Toast.makeText(getApplicationContext(), "New Password is not long enough\n", Toast.LENGTH_LONG).show();
        } else {

        }


        if (confirmNewPass.length() < 1) {
            flag3 = false;
            Toast.makeText(getApplicationContext(), "Need to confirm new password\n", Toast.LENGTH_LONG).show();
        } else {

        }


        if (!newPass.equals(confirmNewPass)) {
            flag4 = false;
            Toast.makeText(getApplicationContext(), "Your newpassword 's not to be confirmed\n", Toast.LENGTH_LONG).show();
        } else {

        }


        if (newPass.equals(oldPass)) {
            flag5 = false;
            Toast.makeText(getApplicationContext(), "Please use other passwords\n", Toast.LENGTH_LONG).show();
        } else {

        }

        if (flag1 && flag2 && flag3 && flag4 && flag5) {


            final Map<String, String> jsonBody = new HashMap<String, String>();
            jsonBody.put("phoneNumber", userPhoneNumber);
            jsonBody.put("oldPassword", oldPass);
            jsonBody.put("newPassword", newPass);

            RmaAPIService rmaAPIService = RmaAPIUtils.getAPIService();
            rmaAPIService.changePassword(jsonBody, authorization).enqueue(new Callback<Object>() {


                @Override
                public void onResponse(Call<Object> call, Response<Object> response) {

                    if (response.isSuccessful()) {

                        LinkedTreeMap<String, Object> responeBody = (LinkedTreeMap<String, Object>) response.body();
                        if (responeBody.containsKey("Authorization")) {
                            Toast.makeText(getApplicationContext(), "Change Password Succesfully", Toast.LENGTH_SHORT).show();
                            SharedPreferences.Editor editor = getSharedPreferences("localData", MODE_PRIVATE).edit();
                            editor.putString("avatar", responeBody.get("Authorization").toString());
                            editor.commit();
                        }
                    } else {
                        LinkedTreeMap<String, Object> responeBody = (LinkedTreeMap<String, Object>) response.body();
                        if (responeBody.containsKey("message")) {
                            Toast.makeText(getApplicationContext(), responeBody.get("message").toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<Object> call, Throwable t) {
                    System.out.println("Fail rồi");
                    Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(getApplicationContext(), toastText, Toast.LENGTH_LONG).show();
        }
    }
}

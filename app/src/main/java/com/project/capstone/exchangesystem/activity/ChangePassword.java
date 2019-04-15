package com.project.capstone.exchangesystem.activity;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
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

        System.out.println("old pass " + oldPass);
        System.out.println("new pass " + newPass);
        System.out.println("confirm pass " + confirmNewPass);

        SharedPreferences sharedPreferences = getSharedPreferences("localData", MODE_PRIVATE);
        String userPhoneNumber = sharedPreferences.getString("phoneNumberSignIn", "Non");
        String authorization = sharedPreferences.getString("authorization", null);
        System.out.println("old pass " + oldPass);
        System.out.println("new pass " + newPass);
        System.out.println("confirm pass " + confirmNewPass);
        System.out.println("authorization " + authorization);


        if (oldPass.length() < 6) {
            flag1 = false;
            Toast.makeText(getApplicationContext(), R.string.password_validation_alert, Toast.LENGTH_LONG).show();
        } else {

        }


        if (newPass.length() < 6) {
            flag2 = false;
            Toast.makeText(getApplicationContext(), R.string.string_password_alert, Toast.LENGTH_LONG).show();
        } else {

        }


        if (confirmNewPass.length() < 1) {
            flag3 = false;
            Toast.makeText(getApplicationContext(), R.string.confirm_password_alert, Toast.LENGTH_LONG).show();
        } else {

        }


        if (!newPass.equals(confirmNewPass)) {
            flag4 = false;
            Toast.makeText(getApplicationContext(), R.string.confirm_new_pass_alert, Toast.LENGTH_LONG).show();
        } else {

        }


        if (newPass.equals(oldPass)) {
            flag5 = false;
            Toast.makeText(getApplicationContext(), R.string.change_pass_alert, Toast.LENGTH_LONG).show();
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
//                    System.out.println("old pass " + oldPass);
//                    System.out.println("new pass " + newPass);
//                    System.out.println("confirm pass " + confirmNewPass);
//                    System.out.println("authorization " + authorization);
                    if (response.isSuccessful()) {

                        LinkedTreeMap<String, Object> responeBody = (LinkedTreeMap<String, Object>) response.body();
                        if (responeBody.containsKey("Authorization")) {
                            Toast.makeText(getApplicationContext(), R.string.change_pass_success, Toast.LENGTH_SHORT).show();
                            SharedPreferences.Editor editor = getSharedPreferences("localData", MODE_PRIVATE).edit();
                            editor.putString("authorization", responeBody.get("Authorization").toString());
                            editor.commit();
                        }
                    } else {
                        LinkedTreeMap<String, Object> responeBody = (LinkedTreeMap<String, Object>) response.body();
                        if (responeBody.containsKey("message")) {
                            Toast.makeText(getApplicationContext(), "Wrong Old Password", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<Object> call, Throwable t) {
                    System.out.println(getString(R.string.pass_change_fail_alert));
                    Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(getApplicationContext(), toastText, Toast.LENGTH_LONG).show();
        }
    }
}

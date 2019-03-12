package com.project.capstone.exchangesystem.Activity;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.google.gson.internal.LinkedTreeMap;
import com.project.capstone.exchangesystem.R;
import com.project.capstone.exchangesystem.Utils.RmaAPIUtils;
import com.project.capstone.exchangesystem.model.User;
import com.project.capstone.exchangesystem.remote.RmaAPIService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.HashMap;
import java.util.Map;

public class EditUserProfileActivity extends AppCompatActivity {

    Toolbar toolbar;
    EditText txtPhoneNumber, txtName, txtAddress;
    boolean flag1 = true, flag2 = true, flag3 = true, flag4 = true, flag5 = true;
    String toastText;
    String userPhoneNumber, authorization, name, address, avatar, status;
    int userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user_profile);
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
        SharedPreferences sharedPreferences = getSharedPreferences("localData", MODE_PRIVATE);
        int userID = sharedPreferences.getInt("userId", 0);
        String userPhoneNumber = sharedPreferences.getString("phoneNumberSignIn", "Non");
        String authorization = sharedPreferences.getString("authorization", null);
        String name = sharedPreferences.getString("username", null);
        String avatar = sharedPreferences.getString("avatar", null);
        String status = sharedPreferences.getString("status", null);


        txtPhoneNumber.setText(userPhoneNumber);
        txtName.setText(name);
        txtAddress.setText(address);

        toolbar = findViewById(R.id.edituserprofileToolbar);
        txtPhoneNumber = findViewById(R.id.txtPhoneNumberEditProfile);
        txtName = findViewById(R.id.txtNameEditUserProfile);
        txtAddress = findViewById(R.id.txtAdressEditUserProfile);
    }

    public void EditProfile(View view) {
        String edtName = txtName.getText().toString();
        String edtAddress = txtAddress.getText().toString();


        if (edtName.length() < 6) {
            flag1 = false;
            Toast.makeText(getApplicationContext(), "Your Name is not long enough\n", Toast.LENGTH_LONG).show();
        } else {

        }


        if (edtAddress.length() < 6) {
            flag2 = false;
            Toast.makeText(getApplicationContext(), "Your New Address is not long enough\n", Toast.LENGTH_LONG).show();
        } else {

        }

        if (flag1 && flag2) {


            final Map<String, String> jsonBody = new HashMap<String, String>();
            jsonBody.put("id", String.valueOf(userID));
            jsonBody.put("phoneNumber", userPhoneNumber);
            jsonBody.put("fullName", edtName);
            jsonBody.put("avatar", avatar);
            jsonBody.put("status", status);


            RmaAPIService rmaAPIService = RmaAPIUtils.getAPIService();
            rmaAPIService.updateInfo(jsonBody, authorization).enqueue(new Callback<Object>() {


                @Override
                public void onResponse(Call<Object> call, Response<Object> response) {

                    System.out.println(response.isSuccessful());

                    if (response.isSuccessful()) {

                        LinkedTreeMap<String, Object> responeBody = (LinkedTreeMap<String, Object>) response.body();
                        if (responeBody.containsKey("User")) {

                            User user = (User) responeBody.get("User");
                            System.out.println("Vào rồi");
                            Toast.makeText(getApplicationContext(), "Change UserProfile Succesfully", Toast.LENGTH_SHORT).show();
                            SharedPreferences.Editor editor = getSharedPreferences("localData", MODE_PRIVATE).edit();
                            editor.putString("fullname", user.getFullName());
                            editor.putString("avatar", user.getAvatar());
                            editor.commit();

                        }
                    } else {
                        LinkedTreeMap<String, Object> responeBody = (LinkedTreeMap<String, Object>) response.body();
                        if (responeBody.containsKey("message")) {
                            Toast.makeText(getApplicationContext(), responeBody.get("message").toString(), Toast.LENGTH_SHORT).show();
                            System.out.println(responeBody.get("message").toString());
                            System.out.println(responeBody.get("message").toString());
                        }
                    }
                }
                //TODO: updating...

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

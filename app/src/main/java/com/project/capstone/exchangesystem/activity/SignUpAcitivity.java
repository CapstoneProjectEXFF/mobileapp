package com.project.capstone.exchangesystem.activity;

import com.project.capstone.exchangesystem.utils.RmaAPIUtils;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.project.capstone.exchangesystem.R;
import com.project.capstone.exchangesystem.model.User;
import com.project.capstone.exchangesystem.remote.RmaAPIService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.HashMap;
import java.util.Map;

public class SignUpAcitivity extends AppCompatActivity {
    EditText txtFullname, txtPhone, txtPassword, txtAddress;
    private Context context;
    TextView lbl_toolbar;
    boolean flag = true;

    boolean flag2 = true;
    boolean flag3 = true;
    boolean flag4 = true;
    boolean flag5 = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_acitivity);

//        lbl_toolbar = findViewById(R.id.lbl_toolbar);
//        lbl_toolbar.setText("Đăng Kí Tài Khoản");
//        lbl_toolbar.setTypeface(null, Typeface.BOLD);

        context = this;
        txtFullname = findViewById(R.id.txtName);
        txtPhone = findViewById(R.id.txtPhone);
        txtPassword = findViewById(R.id.txtPassword);
        txtAddress = findViewById(R.id.txtAddress);

    }

    public void signUpUser(View view) {
        RmaAPIService rmaAPIService = RmaAPIUtils.getAPIService();

        User user = new User();

        String fullname = txtFullname.getText().toString();
        String phone = txtPhone.getText().toString();
        String password = txtPassword.getText().toString();
        String address = txtAddress.getText().toString();

        rmaAPIService.checkValidationLogin(phone).enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                if (response.isSuccessful()) {
                    flag4 = false;
                    txtPhone.setBackgroundResource(R.drawable.signuperror);
                } else {
                    txtPhone.setBackgroundResource(R.drawable.signupedt);
                }
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        if (fullname.length() < 1) {
            flag = false;
            txtFullname.setBackgroundResource(R.drawable.signuperror);
        } else {
            txtFullname.setBackgroundResource(R.drawable.signupedt);
        }


        if (!android.util.Patterns.PHONE.matcher(phone).matches()) {
            flag2 = false;
            txtPhone.setBackgroundResource(R.drawable.signuperror);
        } else {
            txtPhone.setBackgroundResource(R.drawable.signupedt);
        }

        if (password.length() < 6) {
            flag3 = false;
            txtPassword.setBackgroundResource(R.drawable.signuperror);
        } else {
            txtPassword.setBackgroundResource(R.drawable.signupedt);
        }

        if (address.length() < 1) {
            flag5 = false;
            txtAddress.setBackgroundResource(R.drawable.signuperror);
        } else {
            txtAddress.setBackgroundResource(R.drawable.signupedt);
        }


        if ((flag == true) && (flag2 == true) && (flag3 == true) && (flag4 == true)) {


            user.setFullName(fullname);
            user.setPhone(phone);
//            user.setPassword(password);

            final Map<String, String> jsonBody = new HashMap<String, String>();
            jsonBody.put("phoneNumber", phone);
            jsonBody.put("password", password);
            jsonBody.put("fullname", fullname);
            jsonBody.put("status", "1");


            rmaAPIService.register(jsonBody).enqueue(new Callback<Object>() {
                @Override
                public void onResponse(Call<Object> call, Response<Object> response) {
                    System.out.println(response.body());
                    if (true) {
                        Intent intent = new Intent(context, CreateSuccessActivity.class);
                        startActivity(intent);
                    }
                }

                @Override
                public void onFailure(Call<Object> call, Throwable t) {
                    Toast toast = Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT);
                    toast.show();
                }
            });
        }
    }

//    public void onBackButton(View view) {
//        finish();
//    }

    public void toSignUp(View view) {
        finish();
    }
}

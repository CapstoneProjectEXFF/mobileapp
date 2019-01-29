package com.project.capstone.exchangesystem;

import Utils.RmaAPIUtils;
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
import model.User;
import remote.RmaAPIService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import service.UserService;

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
        progressDialog.show();
        rmaAPIService.login(phone, password).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
//                Toast.makeText()
                progressDialog.cancel();
                System.out.println("Done first step");
                Toast.makeText(getApplicationContext(), "Vào rồi", Toast.LENGTH_LONG).show();
                if (response.isSuccessful()) {
                    progressDialog.cancel();
                    User userResult = response.body();
                    System.out.println(response.body());
                    if (userResult != null) {
                        System.out.println("user result không rỗng");
                        if (userResult.isActivated()) {
                            SharedPreferences.Editor editor = getSharedPreferences("localData", MODE_PRIVATE).edit();
                            editor.putString("phoneNumberSignIn", phone);
                            editor.putString("userId", userResult.getId());
                            editor.putString("username", userResult.getFirstName() + " " + userResult.getLastName());
                            editor.commit();


                            // login thẳng vào Main
                            Intent intent = new Intent(context, MainActivity.class);
                            startActivity(intent);
                        } else {
                            Intent intent = new Intent(context, MainActivity.class);
                            intent.putExtra("phoneNumber", phone);
                            intent.putExtra("type", "create-account");
                            intent.putExtra("userId", userResult.getId());
                            intent.putExtra("userName", userResult.getLastName() + " " + userResult.getFirstName());
                            startActivity(intent);
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "Số điện thoại hoặc mật khẩu không đúng", Toast.LENGTH_LONG).show();
                        txtPhone.setText("");
                        txtPassword.setText("");
                    }

                }

            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {

                progressDialog.cancel();
                Toast toast = Toast.makeText(getApplicationContext(), "Fail", Toast.LENGTH_SHORT);
                System.out.println("message from failure: " + t.getMessage());

                toast.show();

            }
        });

    }

    public void toSignUp(View view) {

    }

    public void toResetPassword(View view) {

    }

    public void onBackButton(View view) {
        finish();
    }
}

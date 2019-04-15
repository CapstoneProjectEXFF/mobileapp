package com.project.capstone.exchangesystem.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.project.capstone.exchangesystem.R;
import com.project.capstone.exchangesystem.model.AuthorizationUser;
import com.project.capstone.exchangesystem.remote.RmaAPIService;
import com.project.capstone.exchangesystem.service.UserService;
import com.project.capstone.exchangesystem.utils.RmaAPIUtils;
import com.project.capstone.exchangesystem.utils.UserSession;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.HashMap;
import java.util.Map;

import static com.project.capstone.exchangesystem.constants.AppStatus.USER_ENABLE;

public class SignInActivity extends AppCompatActivity {
    Context context;
    EditText txtPhone, txtPassword;
    ProgressDialog progressDialog;
    TextView lbl_toolbar;
    boolean flag = true;
    boolean flag1 = true;
    boolean flag2 = true;
    UserSession userSession;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

//        lbl_toolbar = findViewById(R.id.lbl_toolbar);
//        lbl_toolbar.setText("Đăng Nhập");
//        lbl_toolbar.setTypeface(null, Typeface.BOLD);


        direct();

    }

    private void direct() {
        context = this;
        userSession = new UserSession(getApplicationContext());
        txtPhone = findViewById(R.id.txtPhone);
        txtPassword = findViewById(R.id.txtPassword);
        progressDialog = UserService.setUpProcessDialog(context);
    }

    public void signIn(View view) {
        String phoneNumber = txtPhone.getText().toString();
        String txtpassword = txtPassword.getText().toString();

        if (!android.util.Patterns.PHONE.matcher(phoneNumber).matches()) {
            flag = false;
            txtPhone.setBackgroundResource(R.drawable.signuperror);
        } else {
            txtPhone.setBackgroundResource(R.drawable.signupedt);
        }

        if (phoneNumber.length() < 1) {
            flag1 = false;
            txtPhone.setBackgroundResource(R.drawable.signuperror);
        } else {
            txtPhone.setBackgroundResource(R.drawable.signupedt);
        }

        if (txtpassword.length() < 6) {
            flag2 = false;
            txtPassword.setBackgroundResource(R.drawable.signuperror);
        } else {
            txtPassword.setBackgroundResource(R.drawable.signupedt);
        }

        if (flag && flag1 && flag2) {

            RmaAPIService rmaAPIService = RmaAPIUtils.getAPIService();
            final String phone = txtPhone.getText().toString();
            String password = txtPassword.getText().toString();
            final Map<String, String> jsonBody = new HashMap<String, String>();
            jsonBody.put("phoneNumber", phone);
            jsonBody.put("password", password);
            progressDialog.show();
            rmaAPIService.login(jsonBody).enqueue(new Callback<AuthorizationUser>() {
                @Override
                public void onResponse(Call<AuthorizationUser> call, Response<AuthorizationUser> response) {
                    progressDialog.cancel();
                    if (response.isSuccessful()) {
                        progressDialog.cancel();
                        if (response.body() != null) {
                            try {
//
//                                LinkedTreeMap<String, Object> responeBody = (LinkedTreeMap<String, Object>) response.body();
//
//                                String authorization = (String) responeBody.get("Authorization");
//                                LinkedTreeMap<String, Object> userInfo = (LinkedTreeMap<String, Object>) responeBody.get("User");
                                AuthorizationUser authorizationUser = (AuthorizationUser) response.body();

//
//                                int id = (int) Math.round((Double) userInfo.get("id"));
//
//                                String phoneNumber = (String) userInfo.get("phoneNumber");
//
//                                String fullName = (String) userInfo.get("fullName");
//
//                                String status = (String) userInfo.get("status");
//
//                                String avatar = "";
//                                if (userInfo.containsKey("avatar")) {
//                                    avatar = avatar + (String) userInfo.get("avatar");
//                                }
//
//                                String address = (String) userInfo.get("address");

                                if (authorizationUser.getUser().getStatus().equals(USER_ENABLE)) {
                                    userSession.createUserLoginSession(authorizationUser.getUser(), authorizationUser.getAuthorization());


//                                    SharedPreferences.Editor editor = getSharedPreferences("localData", MODE_PRIVATE).edit();
//                                    editor.putString("avatar", avatar);
//                                    editor.putString("phoneNumberSignIn", phoneNumber);
//                                    editor.putInt("userId", id);
//                                    editor.putString("username", fullName);
//                                    editor.putString("authorization", authorization);
//                                    editor.putString("status", status);
//                                    editor.putString("address", address);
//                                    editor.commit();


                                    // login thẳng vào Main
                                    Intent intent = new Intent(context, MainActivity.class);
                                    startActivity(intent);
//                                    finish();


                                } else {
//                                    Intent intent = new Intent(context, VerifyActivity.class);
//                                    intent.putExtra("phoneNumber", phoneNumber);
//                                    intent.putExtra("type", "create-account");
//                                    intent.putExtra("userId", id);
//                                    intent.putExtra("userName", fullName);
//                                    startActivity(intent);
//                                    Toast.makeText()
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        } else {
                            Toast.makeText(getApplicationContext(), "Số điện thoại hoặc mật khẩu không đúng! Đăng Nhập Lại", Toast.LENGTH_LONG).show();
                            txtPhone.setText("");
                            txtPassword.setText("");
                        }

                    } else {
                        Toast.makeText(getApplicationContext(), "Số điện thoại hoặc mật khẩu không đúng! Đăng Nhập Lại", Toast.LENGTH_LONG).show();
                        txtPhone.setText("");
                        txtPassword.setText("");
                    }

                }

                @Override
                public void onFailure(Call<AuthorizationUser> call, Throwable t) {
                    progressDialog.cancel();
                    Toast toast = Toast.makeText(getApplicationContext(), R.string.error_server, Toast.LENGTH_SHORT);
                    System.out.println("message from failure: " + t.getMessage());
                    toast.show();
                }
            });
        }
    }

    public void toSignUp(View view) {
        Intent intent = new Intent(this, SignUpAcitivity.class);
        startActivity(intent);

    }

//    public void toResetPassword(View view) {
//        Intent intent = new Intent(this, ForgetPasswordActivity.class);
//        startActivity(intent);
//    }

    public void onBackButton(View view) {
        finish();
    }
}

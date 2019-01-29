package com.project.capstone.exchangesystem;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class SignUpAcitivity extends AppCompatActivity {
    EditText txtFirstname, txtLastname, txtPhone, txtPassword;
    private Context context;
    TextView lbl_toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_acitivity);

        lbl_toolbar = findViewById(R.id.lbl_toolbar);
        lbl_toolbar.setText("Đăng Kí Tài Khoản");
        lbl_toolbar.setTypeface(null, Typeface.BOLD);

        context = this;
        txtFirstname = findViewById(R.id.txtFirstname);
        txtLastname = findViewById(R.id.txtLastname);
        txtPhone = findViewById(R.id.txtPhone);
        txtPassword = findViewById(R.id.txtPassword);

    }

    public void signUpUser(View view) {

    }
}

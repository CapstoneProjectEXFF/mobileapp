package com.project.capstone.exchangesystem.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.project.capstone.exchangesystem.R;

public class QRCodeResultActivity extends AppCompatActivity {

    String qrResult;
    TextView txtResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode_result);
        txtResult = findViewById(R.id.txtResult);
        qrResult = getIntent().getStringExtra("result");
        txtResult.setText(qrResult);
        //TODO
    }
}

package com.project.capstone.exchangesystem.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.project.capstone.exchangesystem.R;

public class QRCodeResultActivity extends AppCompatActivity {

    String qrResult;
    TextView txtReslt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode_result);

        qrResult = getIntent().getStringExtra("result");
        txtReslt.setText(qrResult);
        //TODO
    }
}

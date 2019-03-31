package com.project.capstone.exchangesystem.activity;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.project.capstone.exchangesystem.R;
import com.project.capstone.exchangesystem.model.DonationPost;

import static com.project.capstone.exchangesystem.constants.AppStatus.*;

public class CreateSuccessActivity extends AppCompatActivity {

    TextView txtTitle, txtMessage, txtDirection;
    Button btnConfirm;
    String address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_success);

        address = (String) getIntent().getSerializableExtra("address");
        if (address != null) {
            setView();
        } else {
            btnConfirm = findViewById(R.id.btnXacNhan);
            btnConfirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
                    startActivity(intent);
                }
            });
        }
    }

    private void setView() {
        txtDirection = findViewById(R.id.txtDirection);
        txtDirection.setText(R.string.view_map);
        txtDirection.setTextColor(Color.BLUE);
        txtTitle = findViewById(R.id.lbl_toolbar);

        txtTitle.setText(R.string.confirm_information_success);
        txtMessage = findViewById(R.id.txtMessage);
        txtMessage.setText(R.string.confirm_information_message);
        txtDirection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + address);
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);
            }
        });

        btnConfirm = findViewById(R.id.btnXacNhan);
        btnConfirm.setText(R.string.continuous);

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });
    }
}

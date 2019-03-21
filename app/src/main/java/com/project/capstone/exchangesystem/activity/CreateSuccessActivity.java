package com.project.capstone.exchangesystem.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.project.capstone.exchangesystem.R;
import com.project.capstone.exchangesystem.model.DonationPost;

import static com.project.capstone.exchangesystem.constants.AppStatus.*;

public class CreateSuccessActivity extends AppCompatActivity {

    private final String TITLE = "Đóng góp thành công";
    private final String MSG1 = "Bạn thật đáng yêu ♥.♥";
    private final String MSG2 = "Xin cảm ơn!";
    private final String BTN_CONTENT = "Tiếp tục";

    DonationPost donationPost;
    TextView txtTitle, txtMessage1, txtMessage2;
    Button btnConfirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_success);

        donationPost = (DonationPost) getIntent().getSerializableExtra("donationPost");
        if (donationPost != null){
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
        txtTitle = findViewById(R.id.lbl_toolbar);
        txtTitle.setText(TITLE);
        txtMessage1 = findViewById(R.id.txtMessage1);
        txtMessage1.setText(MSG1);
        txtMessage2 = findViewById(R.id.txtMessage2);
        txtMessage2.setText(MSG2);
        btnConfirm = findViewById(R.id.btnXacNhan);
        btnConfirm.setText(BTN_CONTENT);

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });
    }

//    public void onConfirmClick(View view) {
//        Intent intent = new Intent(this, SignInActivity.class);
//        startActivity(intent);
//    }
}

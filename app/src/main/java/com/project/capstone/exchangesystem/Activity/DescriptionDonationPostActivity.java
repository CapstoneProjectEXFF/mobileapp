package com.project.capstone.exchangesystem.Activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.project.capstone.exchangesystem.R;
import com.project.capstone.exchangesystem.model.DonationPost;
import com.project.capstone.exchangesystem.model.Item;
import com.squareup.picasso.Picasso;

public class DescriptionDonationPostActivity extends AppCompatActivity {
    Toolbar toolbar;
    ImageView imgUserDonation, imgDescriptionDonationPost;
    TextView txtDescriptionDonationContent, txtAddressDonation, txtTimestampDonation, txtUserNameDonation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_description_donation_post);
        direct();
        ActionToolbar();
        GetInformation();
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
        imgUserDonation = findViewById(R.id.imgUserDonation);
        imgDescriptionDonationPost = findViewById(R.id.imgDescriptionDonationPost);
        txtDescriptionDonationContent = findViewById(R.id.txtDescriptionDonationContent);
        txtAddressDonation = findViewById(R.id.txtAddressDonation);
        txtTimestampDonation = findViewById(R.id.txtTimestampDonation);
        txtUserNameDonation = findViewById(R.id.txtUserNameDonation);
        toolbar = findViewById(R.id.descriptionDonationToolbar);
    }

    private void GetInformation() {


        DonationPost donationPost = (DonationPost) getIntent().getSerializableExtra("descriptionDonationPost");
        txtDescriptionDonationContent.setText(donationPost.getContent());
        txtAddressDonation.setText(donationPost.getAddress());
        txtTimestampDonation.setText(donationPost.getCreateTime().toString());
        txtUserNameDonation.setText(donationPost.getUser().getFullName());

        Picasso.with(getApplicationContext()).load(donationPost.getImages().get(0).getUrl())
                .placeholder(R.drawable.no)
                .error(R.drawable.loadingimage)
                .into(imgDescriptionDonationPost);

        Picasso.with(getApplicationContext()).load(donationPost.getUser().getAvatar())
                .placeholder(R.drawable.no)
                .error(R.drawable.loadingimage)
                .into(imgUserDonation);
    }
}

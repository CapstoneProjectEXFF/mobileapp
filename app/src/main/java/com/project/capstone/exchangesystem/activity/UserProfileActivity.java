package com.project.capstone.exchangesystem.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.project.capstone.exchangesystem.R;
import com.project.capstone.exchangesystem.model.User;
import com.project.capstone.exchangesystem.remote.RmaAPIService;
import com.project.capstone.exchangesystem.utils.RmaAPIUtils;
import com.squareup.picasso.Picasso;

public class UserProfileActivity extends AppCompatActivity {

    ImageView imageView, iconEdit;
    TextView txtNameUserProfile;
    TextView txtPhoneNumberProfile;
    LinearLayout linlay2;
    Toolbar toolbar;
    SharedPreferences sharedPreferences;
    RmaAPIService rmaAPIService;
    User userDetail;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_user_profile);
        direct();
        getData();
        actionToolbar();
    }

    private void direct() {
        toolbar = findViewById(R.id.userProfileToolbar);
        imageView = findViewById(R.id.imgUserProfile);
        iconEdit = findViewById(R.id.iconEdit);
        txtNameUserProfile = findViewById(R.id.txtNameUserProfile);
        txtPhoneNumberProfile = findViewById(R.id.txtPhoneNumberProfile);
        linlay2 = findViewById(R.id.linlay2);
        rmaAPIService = RmaAPIUtils.getAPIService();
        linlay2.setVisibility(View.GONE);
        iconEdit.setVisibility(View.GONE);
    }

    private void getData() {
        userDetail = (User) getIntent().getSerializableExtra("friendDetail");
        if (userDetail.getAvatar() != null) {
            Picasso.with(getApplicationContext()).load(userDetail.getAvatar())
                    .placeholder(R.drawable.ic_no_image)
                    .error(R.drawable.ic_no_image)
                    .into(imageView);
        }
        txtNameUserProfile.setText(userDetail.getFullName());
        txtPhoneNumberProfile.setText(userDetail.getPhone());
    }

    public void toOwnInventory(View view) {
        Intent intent = new Intent(this, FriendInventoryActivity.class);
        intent.putExtra("friendDetail", userDetail);
        startActivity(intent);
    }

    public void toOwnDonationPost(View view) {
        Intent iOwnFriendList = new Intent(getApplicationContext(), OwnDonationPost.class);
        iOwnFriendList.putExtra("userDetail", userDetail);
        startActivity(iOwnFriendList);
    }

    private void actionToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}

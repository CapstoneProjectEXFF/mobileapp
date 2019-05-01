package com.project.capstone.exchangesystem.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.project.capstone.exchangesystem.R;
import com.project.capstone.exchangesystem.adapter.ReviewerAdapter;
import com.project.capstone.exchangesystem.model.Rate;
import com.project.capstone.exchangesystem.model.User;
import com.project.capstone.exchangesystem.remote.RmaAPIService;
import com.project.capstone.exchangesystem.utils.RmaAPIUtils;
import com.squareup.picasso.Picasso;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.ArrayList;
import java.util.List;

public class UserProfileActivity extends AppCompatActivity {

    ImageView imageView, iconEdit;
    TextView txtNameUserProfile;
    TextView txtPhoneNumberProfile, txtNumberDonation, txtNumberInventory;
    LinearLayout linlay2;
    Toolbar toolbar;
    SharedPreferences sharedPreferences;
    RmaAPIService rmaAPIService;
    User userDetail;
    RecyclerView rvReviewers;
    ReviewerAdapter reviewerAdapter;
    ArrayList<Rate> ratingList;
    String authorization;
    int userId;
    String tempInventory, tempDonation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_user_profile);
        direct();
        getData();
        showReviewers();
        actionToolbar();
        getInventoryNumber();
        getDonationPostNumber();
    }

    private void showReviewers() {
        rvReviewers = findViewById(R.id.rvReviewers);
        ratingList = new ArrayList<>();
        reviewerAdapter = new ReviewerAdapter(getApplicationContext(), ratingList, new ReviewerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Rate rate) {
                //TODO VIEW USER'S PROFILE
                Toast.makeText(getApplicationContext(), "" + rate.getSender().getFullName(), Toast.LENGTH_LONG).show();
            }
        });
        rvReviewers.setHasFixedSize(true);
        rvReviewers.setLayoutManager(new GridLayoutManager(this, 1));
        rvReviewers.setAdapter(reviewerAdapter);
        loadReviewers();
    }

    private void loadReviewers() {
        if (authorization != null) {
            rmaAPIService.getRating(userDetail.getId()).enqueue(new Callback<List<Rate>>() {
                @Override
                public void onResponse(Call<List<Rate>> call, Response<List<Rate>> response) {
                    if (response.body() != null) {
                        ArrayList<Rate> tmpRatingList = new ArrayList<>();
                        for (int i = 0; i < response.body().size(); i++) {
                            tmpRatingList.add(response.body().get(i));
                        }
                        ratingList.clear();
                        ratingList.addAll(tmpRatingList);
                        reviewerAdapter.notifyDataSetChanged();
//                        if (donators.size() > 0) {
//                            rvDonators.setVisibility(View.VISIBLE);
//                            txtNoDonators.setVisibility(View.GONE);
//                        }
                    }
                }

                @Override
                public void onFailure(Call<List<Rate>> call, Throwable t) {

                }
            });
        }
    }

    private void direct() {
        tempDonation = "";
        tempInventory = "";
        toolbar = findViewById(R.id.userProfileToolbar);
        imageView = findViewById(R.id.imgUserProfile);
        iconEdit = findViewById(R.id.iconEdit);
        txtNameUserProfile = findViewById(R.id.txtNameUserProfile);
        txtPhoneNumberProfile = findViewById(R.id.txtPhoneNumberProfile);
        txtNumberInventory = findViewById(R.id.txtNumberInventory);
        txtNumberDonation = findViewById(R.id.txtNumberDonation);
        linlay2 = findViewById(R.id.linlay2);
        rmaAPIService = RmaAPIUtils.getAPIService();
        linlay2.setVisibility(View.GONE);
        iconEdit.setVisibility(View.GONE);
        sharedPreferences = getSharedPreferences("localData", MODE_PRIVATE);
        authorization = sharedPreferences.getString("authorization", null);
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
        userId = userDetail.getId();
    }

    private void getInventoryNumber() {
        rmaAPIService.countAllItemByUserId(authorization, userId).enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                if (response.isSuccessful()) {
                    tempInventory = response.body().toString();
                    txtNumberInventory.setText(tempInventory);
                }
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {

            }
        });

        txtNumberInventory.setText(tempInventory);
    }

    private void getDonationPostNumber() {
        rmaAPIService.countDonationPostByUserId(userId).enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                if (response.isSuccessful()) {
                    tempDonation = response.body().toString();
                    txtNumberDonation.setText(tempDonation);
                }
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {

            }
        });

        txtNumberDonation.setText(tempDonation);
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

package com.project.capstone.exchangesystem.activity;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.project.capstone.exchangesystem.R;
import com.project.capstone.exchangesystem.adapter.DonatorAdapter;
import com.project.capstone.exchangesystem.model.Donator;
import com.project.capstone.exchangesystem.model.TransactionRequestWrapper;
import com.project.capstone.exchangesystem.utils.RmaAPIUtils;
import com.project.capstone.exchangesystem.model.DonationPost;
import com.project.capstone.exchangesystem.remote.RmaAPIService;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DescriptionDonationPostActivity extends AppCompatActivity {
    Toolbar toolbar;
    ImageView imgUserDonation, imgDescriptionDonationPost;
    TextView txtDescriptionDonationContent, txtAddressDonation, txtTimestampDonation, txtUserNameDonation, btnDonators;
    ImageButton btnShare;
    Button btnDonate;
    Dialog donatorsDialog;

    //share facebook
    CallbackManager callbackManager;
    ShareDialog shareDialog;

    SharedPreferences sharedPreferences;
    RmaAPIService rmaAPIService;
    String authorization;

    DonationPost donationPost;
    int userId;
    ArrayList<Donator> donators;
    DonatorAdapter donatorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(this.getApplicationContext());
        setContentView(R.layout.activity_description_donation_post);
        direct();
        ActionToolbar();

        Intent appLinkIntent = getIntent();
        Uri appLinkData = appLinkIntent.getData();

        if (appLinkData == null) { //check if app was opened from link or not
            GetInformation(-1);
        } else {
            int uriDonationPostId = Integer.parseInt(appLinkData.toString().replace("https://exff-104b8.firebaseapp.com/donation-post.html?id=", ""));
            GetInformation(uriDonationPostId);
        }
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
        btnShare = findViewById(R.id.btnShare);
        btnDonate = findViewById(R.id.btnDonate);
        btnDonators = findViewById(R.id.btnDonators);
        donatorsDialog = new Dialog(this);

        //share facebook
        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(this);

        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShareLinkContent shareLinkContent = new ShareLinkContent.Builder()
                        .setQuote("Test").setContentUrl(Uri.parse("https://exff-104b8.firebaseapp.com/donation-post.html?id=" + donationPost.getId())).build();
                if (ShareDialog.canShow(ShareLinkContent.class)) {
                    shareDialog.show(shareLinkContent);
                }
            }
        });

        btnDonate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), DonateItemActivity.class);
                intent.putExtra("donationPost", donationPost);
                startActivity(intent);
            }
        });

        btnDonators.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDonators();
            }
        });

        rmaAPIService = RmaAPIUtils.getAPIService();
        sharedPreferences = getSharedPreferences("localData", MODE_PRIVATE);
        authorization = sharedPreferences.getString("authorization", null);
    }

    private void showDonators() {
        ImageButton btnPopUpClose;
        RecyclerView rvDonators;
        donatorsDialog.setContentView(R.layout.donators_popup);
        btnPopUpClose = donatorsDialog.findViewById(R.id.btnPopUpClose);
        rvDonators = donatorsDialog.findViewById(R.id.rvDonators);
        donators = new ArrayList<>();
        donatorAdapter = new DonatorAdapter(donatorsDialog.getContext(), donators, new DonatorAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Donator donator) {
                //TODO VIEW USER'S PROFILE
                Toast.makeText(donatorsDialog.getContext(), "" + donator.getDonatorName(), Toast.LENGTH_LONG).show();
            }
        });
        rvDonators.setHasFixedSize(true);
        rvDonators.setLayoutManager(new GridLayoutManager(this, 1));
        rvDonators.setAdapter(donatorAdapter);
        loadDonators();

        btnPopUpClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                donatorsDialog.dismiss();
            }
        });

        donatorsDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        donatorsDialog.show();
    }

    private void loadDonators() {
        if (authorization != null){
            rmaAPIService.getTransactionByDonationPostId(donationPost.getId()).enqueue(new Callback<List<TransactionRequestWrapper>>() {
                @Override
                public void onResponse(Call<List<TransactionRequestWrapper>> call, Response<List<TransactionRequestWrapper>> response) {
                    if (response != null){
                        List<Donator> tmpDonators = new ArrayList<>();
                        for (int i = 0; i < response.body().size(); i++){
                            List<TransactionRequestWrapper> transactionList = response.body();
                            Donator donator = new Donator();
                            donator.setId(transactionList.get(i).getTransaction().getSenderId());
                            donator.setDonatorName(transactionList.get(i).getTransaction().getSender().getFullName());
                            donator.setAvatarUrl(transactionList.get(i).getTransaction().getSender().getAvatar());
                            List<String> itemNames = new ArrayList<>();
                            for (int j = 0; j < transactionList.get(i).getDetails().size(); j++){
                                itemNames.add(transactionList.get(i).getDetails().get(j).getItem().getName());
                            }
                            donator.setItemNames(itemNames);
                            tmpDonators.add(donator);
                        }
                        donators.clear();
                        donators.addAll(tmpDonators);
                        donatorAdapter.notifyDataSetChanged();
                    }
                }

                @Override
                public void onFailure(Call<List<TransactionRequestWrapper>> call, Throwable t) {

                }
            });
        }
    }

    private void GetInformation(int uriDonationPostId) {
        if (uriDonationPostId == -1) {
            donationPost = (DonationPost) getIntent().getSerializableExtra("descriptionDonationPost");
            setDonationPostInf(donationPost);
        } else {
            if (authorization != null) {
                rmaAPIService.getDonationPostById(authorization, uriDonationPostId).enqueue(new Callback<DonationPost>() {
                    @Override
                    public void onResponse(Call<DonationPost> call, Response<DonationPost> response) {
                        if (response.isSuccessful()) {
                            if (response.body() != null) {
                                donationPost = response.body();
                                setDonationPostInf(donationPost);
                            } else {
                                Log.i("Donation", "null");
                            }
                        } else {
                            Log.i("Donation", "cannot load donation post");
                        }
                    }

                    @Override
                    public void onFailure(Call<DonationPost> call, Throwable t) {
                        Log.i("Donation", "failed");
                    }
                });
            }
        }
    }

    private void setDonationPostInf(DonationPost donationPost) {
        txtDescriptionDonationContent.setText(donationPost.getContent());
        txtAddressDonation.setText(donationPost.getAddress());
        txtTimestampDonation.setText(donationPost.getCreateTime().toString());
        txtUserNameDonation.setText(donationPost.getUser().getFullName());

        Picasso.with(getApplicationContext()).load(donationPost.getImages().get(0).getUrl())
                .placeholder(R.drawable.ic_no_image)
                .error(R.drawable.ic_no_image)
                .into(imgDescriptionDonationPost);

        Picasso.with(getApplicationContext()).load(donationPost.getUser().getAvatar())
                .placeholder(R.drawable.ic_no_image)
                .error(R.drawable.ic_no_image)
                .into(imgUserDonation);
        userId = sharedPreferences.getInt("userId", 0);
        if (userId != donationPost.getUser().getId()){
            btnDonate.setVisibility(View.VISIBLE);
        }
    }
}

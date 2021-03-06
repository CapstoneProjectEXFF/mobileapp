package com.project.capstone.exchangesystem.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.project.capstone.exchangesystem.R;
import com.project.capstone.exchangesystem.adapter.DonationTargetAdapter;
import com.project.capstone.exchangesystem.adapter.DonatorAdapter;
import com.project.capstone.exchangesystem.adapter.GalleryAdapter;
import com.project.capstone.exchangesystem.constants.AppStatus;
import com.project.capstone.exchangesystem.dialog.LoginDialogFragment;
import com.project.capstone.exchangesystem.model.DonationPost;
import com.project.capstone.exchangesystem.model.DonationPostTarget;
import com.project.capstone.exchangesystem.model.Donator;
import com.project.capstone.exchangesystem.model.Image;
import com.project.capstone.exchangesystem.model.TargetStatus;
import com.project.capstone.exchangesystem.model.TransactionRequestWrapper;
import com.project.capstone.exchangesystem.remote.RmaAPIService;
import com.project.capstone.exchangesystem.utils.RmaAPIUtils;
import com.project.capstone.exchangesystem.utils.UserSession;
import com.squareup.picasso.Picasso;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DescriptionDonationPostActivity extends AppCompatActivity implements LoginDialogFragment.LoginDialogListener {
    Toolbar toolbar;
    ImageView imgUserDonation, imgDescriptionDonationPost;
    TextView txtDescriptionDonationContent, txtAddressDonation, txtTimestampDonation, txtUserNameDonation, txtNoDonators, txtDonators, txtTargets, txtDonationName;
    ImageButton btnShare;
    Button btnDonate;
    UserSession userSession;

    //share facebook
    CallbackManager callbackManager;
    ShareDialog shareDialog;

    SharedPreferences sharedPreferences;
    RmaAPIService rmaAPIService;
    String authorization, url;

    DonationPost donationPost;
    int userId;
    ArrayList<Donator> donators;
    DonatorAdapter donatorAdapter;
    RecyclerView rvDonators, rvTargets, rvItemImages;
    private static final int UPDATE_CODE = 1;
    private static final int ADD_CODE = 2;
    private boolean reloadNeed = true, checkDonatorClick = false, checkTargetClick = false;
    List<DonationPostTarget> targets;
    ArrayList<TargetStatus> targetStatusList;
    DonationTargetAdapter donationTargetAdapter;
    List<String> urlList, tmpUrlList;
    GalleryAdapter galleryAdapter;
    Context context;
    Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(this.getApplicationContext());
        setContentView(R.layout.activity_description_donation_post);
        direct();

        Intent appLinkIntent = getIntent();
        Uri appLinkData = appLinkIntent.getData();

        if (appLinkData == null) { //check if app was opened from link or not
            GetInformation(-1);
        } else {
            int uriDonationPostId = Integer.parseInt(appLinkData.toString().replace(getString(R.string.donation_link), ""));
            reloadNeed = false;
            GetInformation(uriDonationPostId);
        }
    }

    private void ActionToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (reloadNeed) {
                    Intent returnIntent = new Intent();
                    setResult(Activity.RESULT_OK, returnIntent);
                    finish();
                } else {
                    Intent returnIntent = new Intent();
                    setResult(Activity.RESULT_CANCELED, returnIntent);
                    finish();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (userId == donationPost.getUser().getId() && !donationPost.getStatus().equals(AppStatus.DISABLED_DONATION_POST)) {
            getMenuInflater().inflate(R.menu.menu_edit_post_option, menu);
            menu.getItem(0).setTitle("Sửa bài viết");
            menu.getItem(1).setTitle("Khóa bài viết");
            return true;
        }
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (this.reloadNeed)
            reloadData();
        this.reloadNeed = false;
    }

    private void reloadData() {
        rmaAPIService.getDonationPostById(authorization, donationPost.getId()).enqueue(new Callback<DonationPost>() {
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == UPDATE_CODE) { // Ah! We are back from EditActivity, did we make any changes?
            if (resultCode == Activity.RESULT_OK) {
                // Yes we did! Let's allow onResume() to reload the data
                this.reloadNeed = true;
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.editpost) {
            Intent intent = new Intent(getApplicationContext(), UpdateDonationPostActivity.class);
            intent.putExtra("donationPost", donationPost);
            startActivityForResult(intent, UPDATE_CODE);
        } else if (item.getItemId() == R.id.deletepost) {
            deleteDonationPost();
        }
        return true;
    }

    private void deleteDonationPost() {

        rmaAPIService.removeDonationPost(authorization, donationPost.getId()).enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), R.string.lock_donation, Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), R.string.error_request, Toast.LENGTH_LONG).show();
                    System.out.println(response.body());
                }
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                Toast.makeText(getApplicationContext(), R.string.error_server, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void direct() {
        context = this;
        userSession = new UserSession(getApplicationContext());
        imgUserDonation = findViewById(R.id.imgUserDonation);
        imgDescriptionDonationPost = findViewById(R.id.imgDescriptionDonationPost);
        txtDescriptionDonationContent = findViewById(R.id.txtDescriptionDonationContent);
        txtAddressDonation = findViewById(R.id.txtAddressDonation);
        txtTimestampDonation = findViewById(R.id.txtTimestampDonation);
        txtUserNameDonation = findViewById(R.id.txtUserNameDonation);
        toolbar = findViewById(R.id.descriptionDonationToolbar);
        btnShare = findViewById(R.id.btnShare);
        btnDonate = findViewById(R.id.btnDonate);
        rmaAPIService = RmaAPIUtils.getAPIService();
        sharedPreferences = getSharedPreferences("localData", MODE_PRIVATE);
        authorization = sharedPreferences.getString("authorization", null);
        userId = sharedPreferences.getInt("userId", 0);
        txtNoDonators = findViewById(R.id.txtNoDonators);
        rvDonators = findViewById(R.id.rvDonators);
        txtDonators = findViewById(R.id.txtDonators);
        txtTargets = findViewById(R.id.txtTargets);
        rvTargets = findViewById(R.id.rvTargets);
        txtDonationName = findViewById(R.id.txtDonationName);
        rvItemImages = findViewById(R.id.rvItemImages);

        //share facebook
        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(this);

        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShareLinkContent shareLinkContent = new ShareLinkContent.Builder()
                        .setQuote(donationPost.getTitle()).setContentUrl(Uri.parse(getString(R.string.donation_link) + donationPost.getId())).build();
                if (ShareDialog.canShow(ShareLinkContent.class)) {
                    shareDialog.show(shareLinkContent);
                }
            }
        });

        btnDonate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userSession.isUserLoggedIn()) {
                    Intent intent = new Intent(getApplicationContext(), DonateItemActivity.class);
                    intent.putExtra("donationPost", donationPost);
                    startActivity(intent);
                } else {
                    showNoticeDialog();
                }
            }
        });

        txtDonators.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkDonatorClick){
                    checkDonatorClick = true;
                    txtDonators.setCompoundDrawablesWithIntrinsicBounds(0, 0 , R.drawable.round_keyboard_arrow_down_black_24, 0);
                    if (donators.size() > 0) {
                        rvDonators.setVisibility(View.VISIBLE);
                    } else {
                        txtNoDonators.setVisibility(View.VISIBLE);
                    }
                } else {
                    checkDonatorClick = false;
                    txtDonators.setCompoundDrawablesWithIntrinsicBounds(0, 0 , R.drawable.round_keyboard_arrow_right_black_24, 0);
                    rvDonators.setVisibility(View.GONE);
                    txtNoDonators.setVisibility(View.GONE);
                }
            }
        });

        txtTargets.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkDonatorClick){
                    checkDonatorClick = true;
                    txtTargets.setCompoundDrawablesWithIntrinsicBounds(0, 0 , R.drawable.round_keyboard_arrow_down_black_24, 0);
                    rvTargets.setVisibility(View.VISIBLE);
                } else {
                    checkDonatorClick = false;
                    txtTargets.setCompoundDrawablesWithIntrinsicBounds(0, 0 , R.drawable.round_keyboard_arrow_right_black_24, 0);
                    rvTargets.setVisibility(View.GONE);
                }
            }
        });

        imgDescriptionDonationPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFullImage();
            }
        });
    }

    private void showFullImage() {
        dialog = new Dialog(DescriptionDonationPostActivity.this);
        dialog.setContentView(R.layout.full_receipt_view);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        setReceiptDialogComponents(dialog);
    }

    private void setReceiptDialogComponents(final Dialog dialog) {
        ImageView ivFullReceipt;
        ImageButton btnCloseImage;

        ivFullReceipt = dialog.findViewById(R.id.ivFullReceipt);
        btnCloseImage = dialog.findViewById(R.id.btnCloseImage);

        Picasso.with(getApplicationContext()).load(url)
                .placeholder(R.drawable.ic_no_image)
                .error(R.drawable.ic_no_image)
                .into(ivFullReceipt);

        btnCloseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    private void showDonators() {
        donators = new ArrayList<>();
        donatorAdapter = new DonatorAdapter(getApplicationContext(), donators, new DonatorAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Donator donator) {
            }
        });
        rvDonators.setHasFixedSize(true);
        rvDonators.setLayoutManager(new GridLayoutManager(this, 1));
        rvDonators.setAdapter(donatorAdapter);
        loadDonators();
    }

    private void loadDonators() {
        if (authorization != null) {
            rmaAPIService.getTransactionByDonationPostId(donationPost.getId()).enqueue(new Callback<List<TransactionRequestWrapper>>() {
                @Override
                public void onResponse(Call<List<TransactionRequestWrapper>> call, Response<List<TransactionRequestWrapper>> response) {
                    if (response.body() != null) {
                        List<Donator> tmpDonators = new ArrayList<>();
                        for (int i = 0; i < response.body().size(); i++) {
                            List<TransactionRequestWrapper> transactionList = response.body();
                            Donator donator = new Donator();
                            donator.setId(transactionList.get(i).getTransaction().getSenderId());
                            donator.setDonatorName(transactionList.get(i).getTransaction().getSender().getFullName());
                            donator.setAvatarUrl(transactionList.get(i).getTransaction().getSender().getAvatar());
                            List<String> itemNames = new ArrayList<>();
                            for (int j = 0; j < transactionList.get(i).getDetails().size(); j++) {
                                itemNames.add(transactionList.get(i).getDetails().get(j).getItem().getName());
                                donator.setItem(transactionList.get(i).getDetails().get(j).getItem());
                            }
                            donator.setItemNames(itemNames);
                            tmpDonators.add(donator);
                        }
                        donators.clear();
                        donators.addAll(tmpDonators);
                        donatorAdapter.notifyDataSetChanged();
                        showTargets();
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

        Date date = new Date();
        date.setTime(donationPost.getCreateTime().getTime());
        String dateStr = new SimpleDateFormat("dd.MM.yyyy").format(date);
        String timeStr = new SimpleDateFormat("HH:mm").format(date);

        txtTimestampDonation.setText(dateStr + " " + getString(R.string.at) + " " + timeStr);
        txtUserNameDonation.setText(donationPost.getUser().getFullName());

        if (donationPost.getTitle() != null){
            txtDonationName.setText(donationPost.getTitle());
        } else {
            txtDonationName.setText(getString(R.string.donation_default_title));
        }

        if (donationPost.getImages().size() > 0) {
            url = donationPost.getImages().get(0).getUrl();
            Picasso.with(getApplicationContext()).load(url)
                    .placeholder(R.drawable.ic_no_image)
                    .error(R.drawable.ic_no_image)
                    .into(imgDescriptionDonationPost);
            setImageList();
        }

        Picasso.with(getApplicationContext()).load(donationPost.getUser().getAvatar())
                .placeholder(R.drawable.ic_no_image)
                .error(R.drawable.ic_no_image)
                .into(imgUserDonation);

        if (userId == donationPost.getUser().getId() || donationPost.getStatus().equals(AppStatus.DISABLED_DONATION_POST)) {
            btnDonate.setVisibility(View.GONE);
        }

        if (donationPost.getStatus().equals(AppStatus.DISABLED_DONATION_POST)) {
            btnShare.setVisibility(View.GONE);
        }

        showDonators();

        ActionToolbar();
    }

    private void setImageList() {
        urlList = new ArrayList<>();
        galleryAdapter = new GalleryAdapter(context, urlList, new GalleryAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(String selectedUrl) {
                Picasso.with(getApplicationContext()).load(selectedUrl)
                        .placeholder(R.drawable.ic_no_image)
                        .error(R.drawable.ic_no_image)
                        .into(imgDescriptionDonationPost);
                url = selectedUrl;
            }
        });

        rvItemImages.setHasFixedSize(true);
        rvItemImages.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvItemImages.setAdapter(galleryAdapter);

        tmpUrlList = new ArrayList<>();

        for (int i = 0; i < donationPost.getImages().size(); i++){
            Image tmpImage = donationPost.getImages().get(i);
            tmpUrlList.add(tmpImage.getUrl());
        }

        galleryAdapter.setfilter(tmpUrlList);
    }

    private void showTargets() {
        getTargetStatusList();
        targets = donationPost.getDonationPostTargets();
        donationTargetAdapter = new DonationTargetAdapter(getApplicationContext(), targets, targetStatusList);
        rvTargets.setHasFixedSize(true);
        rvTargets.setLayoutManager(new GridLayoutManager(this, 1));
        rvTargets.setAdapter(donationTargetAdapter);
    }

    private void getTargetStatusList() {
        targetStatusList = new ArrayList<>();
        if (donationPost.getDonationPostTargets() != null){
            for (int i = 0; i < donationPost.getDonationPostTargets().size(); i++){
                DonationPostTarget tmpTarget = donationPost.getDonationPostTargets().get(i);
                TargetStatus targetStatus = new TargetStatus();
                targetStatus.setCategoryId(tmpTarget.getCategoryId());
                targetStatus.setCount(0);
                for (int j = 0; j < donators.size(); j++){
                    if (donators.get(j).getItem().getCategory().getId() == tmpTarget.getCategoryId()){
                        int tmpCount = targetStatus.getCount();
                        tmpCount++;
                        targetStatus.setCount(tmpCount);
                    }
                }
                targetStatusList.add(targetStatus);
            }
        }
    }

    public void showNoticeDialog() {
        // Create an instance of the dialog fragment and show it
        DialogFragment dialog = new LoginDialogFragment();
        dialog.show(getSupportFragmentManager(), "LoginDialogFragment");
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        Intent signInActivity = new Intent(getApplicationContext(), SignInActivity.class);
        startActivity(signInActivity);
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        return;
    }
}
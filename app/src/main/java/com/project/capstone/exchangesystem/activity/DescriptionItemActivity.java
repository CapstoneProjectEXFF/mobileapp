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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.project.capstone.exchangesystem.adapter.GalleryAdapter;
import com.project.capstone.exchangesystem.dialog.LoginDialogFragment;
import com.project.capstone.exchangesystem.dialog.LoginOptionDialog;
import com.project.capstone.exchangesystem.model.Image;
import com.project.capstone.exchangesystem.model.Item;
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

import static com.project.capstone.exchangesystem.constants.AppStatus.CANCEL_IMAGE_OPTION;
import static com.project.capstone.exchangesystem.constants.AppStatus.LOGIN_REMINDER;

public class DescriptionItemActivity extends AppCompatActivity implements LoginOptionDialog.LoginOptionListener, LoginDialogFragment.LoginDialogListener {
    android.support.v7.widget.Toolbar toolbarDescriptionItem;
    ImageView imgDescriptionItem, imgAvatar;
    TextView txtDateDescriptionItem, txtNameUserDescriotionItem, txtViewDescriptionItem, txtItemName, txtAddress;
    Button btnTrade;
    ImageButton btnShare;
    UserSession userSession;
    RecyclerView rvItemImages;
    GalleryAdapter galleryAdapter;
    Dialog dialog;

    //share facebook
    CallbackManager callbackManager;
    ShareDialog shareDialog;

    SharedPreferences sharedPreferences;
    RmaAPIService rmaAPIService;
    String authorization, url;
    int idMe;

    Item item;
    Context context;
    List<String> urlList, tmpUrlList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(this.getApplicationContext());
        setContentView(R.layout.activity_description_item);
        direct();

        Intent appLinkIntent = getIntent();
        Uri appLinkData = appLinkIntent.getData();

        if (appLinkData == null) { //check if app was opened from link or not
            GetInformation(-1);
        } else {
            int uriItemId = Integer.parseInt(appLinkData.toString().replace(getString(R.string.item_link), ""));
            GetInformation(uriItemId);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (userSession.isUserLoggedIn() && idMe == item.getUser().getId()) {
            getMenuInflater().inflate(R.menu.menu_edit_post_option, menu);
            menu.getItem(0).setTitle(R.string.update_item);
            menu.getItem(1).setTitle(R.string.delete_item);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == R.id.editpost) {
            Intent intent = new Intent(getApplicationContext(), UpdateItemActivity.class);
            intent.putExtra("itemId", item.getId());
            startActivity(intent);
        } else if (menuItem.getItemId() == R.id.deletepost) {
            deleteItem();
        }
        return true;
    }

    private void deleteItem() {

        rmaAPIService.deleteItemWithId(authorization, item.getId()).enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), R.string.delete_item_noti, Toast.LENGTH_LONG).show();
                    Intent returnIntent = new Intent();
                    setResult(Activity.RESULT_OK, returnIntent);
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

    private void GetInformation(int uriItemId) {
        if (uriItemId == -1) {
            item = (Item) getIntent().getSerializableExtra("descriptionItem");
            setItemInf();
        } else {
            sharedPreferences = getSharedPreferences("localData", MODE_PRIVATE);
            authorization = sharedPreferences.getString("authorization", null);
            idMe = sharedPreferences.getInt("userId", 0);
            if (authorization != null) {
                txtDateDescriptionItem.setText("");
                rmaAPIService.getItemById(authorization, uriItemId).enqueue(new Callback<Item>() {
                    @Override
                    public void onResponse(Call<Item> call, Response<Item> response) {
                        if (response.isSuccessful()) {
                            if (response.body() != null) {
                                item = response.body();
                                setItemInf();
                            } else {
                                Log.i("Item", "null");
                            }
                        } else {
                            Log.i("Item", "cannot load item");
                        }
                    }

                    @Override
                    public void onFailure(Call<Item> call, Throwable t) {
                        Log.i("Item", "failed");
                    }
                });
            }
        }
    }

    private void setItemInf() {
        txtNameUserDescriotionItem.setText(item.getUser().getFullName());

        Date date = new Date();
        date.setTime(item.getCreateTime().getTime());
        String dateStr = new SimpleDateFormat("dd.MM.yyyy").format(date);
        String timeStr = new SimpleDateFormat("HH:mm").format(date);

        txtDateDescriptionItem.setText(dateStr + " " + getString(R.string.at) + " " + timeStr);
        txtViewDescriptionItem.setText(item.getDescription());
        txtItemName.setText(item.getName());
        txtAddress.setText(item.getUser().getAddress());

        if (item.getImage().size() > 0) {
            url = item.getImage().get(0).getUrl();
            Picasso.with(getApplicationContext()).load(url)
                    .placeholder(R.drawable.ic_no_image)
                    .error(R.drawable.ic_no_image)
                    .into(imgDescriptionItem);
            setImageList();
        } else {
            imgDescriptionItem.setImageResource(R.drawable.ic_no_image);
        }
        if (item.getUser() != null && item.getUser().getAvatar() != null) {
            url = item.getUser().getAvatar();
            Picasso.with(getApplicationContext()).load(url)
                    .placeholder(R.drawable.ic_no_image)
                    .error(R.drawable.ic_no_image)
                    .into(imgAvatar);
        } else {
            imgDescriptionItem.setImageResource(R.drawable.ic_no_image);
        }

        if (item.getUser().getId() == idMe) {
            btnTrade.setVisibility(View.GONE);
        }

        actionToolbar();
    }

    private void setImageList() {
        urlList = new ArrayList<>();
        galleryAdapter = new GalleryAdapter(context, urlList, new GalleryAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(String selectedUrl) {
                Picasso.with(getApplicationContext()).load(selectedUrl)
                        .placeholder(R.drawable.ic_no_image)
                        .error(R.drawable.ic_no_image)
                        .into(imgDescriptionItem);
                url = selectedUrl;
            }
        });

        rvItemImages.setHasFixedSize(true);
        rvItemImages.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvItemImages.setAdapter(galleryAdapter);

        tmpUrlList = new ArrayList<>();

        for (int i = 0; i < item.getImages().size(); i++){
            Image tmpImage = item.getImages().get(i);
            tmpUrlList.add(tmpImage.getUrl());
        }

        galleryAdapter.setfilter(tmpUrlList);
    }

    private void actionToolbar() {
        setSupportActionBar(toolbarDescriptionItem);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbarDescriptionItem.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void direct() {
        context = this;
        userSession = new UserSession(getApplicationContext());
        sharedPreferences = getSharedPreferences("localData", MODE_PRIVATE);
        idMe = sharedPreferences.getInt("userId", 0);
        authorization = sharedPreferences.getString("authorization", null);
        toolbarDescriptionItem = findViewById(R.id.toolbarDescriptionItem);
        imgDescriptionItem = findViewById(R.id.imgDescriptionItem);
        imgAvatar = findViewById(R.id.imgUserAvatar);
        txtDateDescriptionItem = findViewById(R.id.txtDateDescriptionItem);
        txtNameUserDescriotionItem = findViewById(R.id.txtNameUserDescriotionItem);
        txtViewDescriptionItem = findViewById(R.id.txtViewDescriptionItem);
        btnTrade = findViewById(R.id.btnTrade);
        btnShare = findViewById(R.id.btnShare);
        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(this);
        txtItemName = findViewById(R.id.txtItemName);
        txtAddress = findViewById(R.id.txtAddress);
        rvItemImages = findViewById(R.id.rvItemImages);

        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShareLinkContent shareLinkContent = new ShareLinkContent.Builder()
                        .setQuote(item.getName()).setContentUrl(Uri.parse(getString(R.string.item_link) + item.getId())).build();
                if (ShareDialog.canShow(ShareLinkContent.class)) {
                    shareDialog.show(shareLinkContent);
                }
            }
        });

        rmaAPIService = RmaAPIUtils.getAPIService();

        imgDescriptionItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFullImage();
            }
        });
    }

    public void toTradeActivity(View view) {
        if (userSession.isUserLoggedIn()) {
            Intent intent = new Intent(getApplicationContext(), TradeRealtimeActivity.class);
            intent.putExtra("descriptionItem", item);
            startActivity(intent);
        } else {
            showNoticeDialog();
        }
    }

    @Override
    public void onButtonClicked(int choice) {
        switch (choice) {
            case LOGIN_REMINDER:
                login();
                break;
            case CANCEL_IMAGE_OPTION:
                break;
            default:
                break;
        }
    }

    private void login() {
        Intent signInActivity = new Intent(getApplicationContext(), SignInActivity.class);
        startActivity(signInActivity);
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

    private void showFullImage() {
        dialog = new Dialog(DescriptionItemActivity.this);
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
}

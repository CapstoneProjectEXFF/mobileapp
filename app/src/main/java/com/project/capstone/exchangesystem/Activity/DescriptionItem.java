package com.project.capstone.exchangesystem.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;
import android.support.v7.widget.Toolbar;
import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.project.capstone.exchangesystem.R;
import com.project.capstone.exchangesystem.Utils.RmaAPIUtils;
import com.project.capstone.exchangesystem.remote.RmaAPIService;
import com.squareup.picasso.Picasso;
import com.project.capstone.exchangesystem.model.Item;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DescriptionItem extends AppCompatActivity {
    android.support.v7.widget.Toolbar toolbarDescriptionItem;
    ImageView imgDescriptionItem;
    TextView txtNameDescriptionItem, txtNameUserDescriotionItem, txtViewDescriptionItem;
    Button btnTrade, btnShare;

    //share facebook
    CallbackManager callbackManager;
    ShareDialog shareDialog;

    SharedPreferences sharedPreferences;
    RmaAPIService rmaAPIService;
    String authorization;

    Item item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(this.getApplicationContext());
        setContentView(R.layout.activity_description_item);
        direct();
        ActionToolbar();
//        EventButton();

        Intent appLinkIntent = getIntent();
        Uri appLinkData = appLinkIntent.getData();

        if (appLinkData == null) { //check if app was opened from link or not
            GetInformation(-1);
        } else {
            int uriItemId = Integer.parseInt(appLinkData.toString().replace("https://exff-104b8.firebaseapp.com/item.html?id=", ""));
            GetInformation(uriItemId);
        }
    }

    private void GetInformation(int uriItemId) {
        if (uriItemId == -1) {
            item = (Item) getIntent().getSerializableExtra("descriptionItem");
            setItemInf(item);
        } else {
            sharedPreferences = getSharedPreferences("localData", MODE_PRIVATE);
            authorization = sharedPreferences.getString("authorization", null);
            if (authorization != null) {
                txtNameDescriptionItem.setText("test");
                rmaAPIService.getItemById(authorization, uriItemId).enqueue(new Callback<Item>() {
                    @Override
                    public void onResponse(Call<Item> call, Response<Item> response) {
                        if (response.isSuccessful()) {
                            if (response.body() != null) {
                                item = response.body();
                                setItemInf(item);
                            } else {
                                Log.i("Item", "null");
                                Toast.makeText(getApplicationContext(), "exe", Toast.LENGTH_LONG).show();
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

    private void setItemInf(Item item) {
        txtNameDescriptionItem.setText(item.getName());
        txtViewDescriptionItem.setText(item.getDescription());
        Picasso.with(getApplicationContext()).load(item.getImages().get(0).getUrl())
                .placeholder(R.drawable.no)
                .error(R.drawable.loadingimage)
                .into(imgDescriptionItem);
    }

    private void ActionToolbar() {
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
        toolbarDescriptionItem = (Toolbar) findViewById(R.id.toolbarDescriptionItem);
        imgDescriptionItem = (ImageView) findViewById(R.id.imgDescriptionItem);
        txtNameDescriptionItem = (TextView) findViewById(R.id.txtNameDescriptionItem);
        txtNameUserDescriotionItem = (TextView) findViewById(R.id.txtNameUserDescriotionItem);
        txtViewDescriptionItem = (TextView) findViewById(R.id.txtViewDescriptionItem);
        btnTrade = (Button) findViewById(R.id.btnTrade);
        btnShare = (Button) findViewById(R.id.btnShare);

        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(this);

        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShareLinkContent shareLinkContent = new ShareLinkContent.Builder()
                        .setQuote("Test").setContentUrl(Uri.parse("https://exff-104b8.firebaseapp.com/item.html?id=" + item.getId())).build();
                if (ShareDialog.canShow(ShareLinkContent.class)) {
                    shareDialog.show(shareLinkContent);
                }
            }
        });

        rmaAPIService = RmaAPIUtils.getAPIService();
    }

    public void toTradeActivity(View view) {
        Intent intent = new Intent(getApplicationContext(), TradeActivity.class);
        Item item = (Item) getIntent().getSerializableExtra("descriptionItem");
        intent.putExtra("descriptionItem", item);
        startActivity(intent);

    }
}

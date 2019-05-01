package com.project.capstone.exchangesystem.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.project.capstone.exchangesystem.R;
import com.project.capstone.exchangesystem.adapter.SelectedItemAdapter;
import com.project.capstone.exchangesystem.fragment.ImageOptionDialog;
import com.project.capstone.exchangesystem.model.*;
import com.project.capstone.exchangesystem.remote.RmaAPIService;
import com.project.capstone.exchangesystem.utils.RmaAPIUtils;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.project.capstone.exchangesystem.constants.AppStatus.*;

public class DonateItemActivity extends AppCompatActivity implements ImageOptionDialog.ImageOptionListener {

    TextView txtReceiverName;
    ArrayList<Item> itemList, tmpItemList, availableItems;

    DonationPost donationPost;
    SharedPreferences sharedPreferences;
    String authorization, userPhoneNumber, userFullName;
    RmaAPIService rmaAPIService, rmaAPIRealtime;
    Integer userId;

    Context context;

    //new view
    Toolbar toolbar;
    RecyclerView rvSelectedImages;
    SelectedItemAdapter itemAdapter;
    ImageButton btnAddItems;
    Item tmpItem;
    TextView txtNoti;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donate_item);
        itemList = new ArrayList<>();
        tmpItemList = new ArrayList<>();
        availableItems = new ArrayList<>();
        context = this;
        getComponent();
        setToolbar();
        setItemAdapter();
        loadAvailableItems();

        btnAddItems.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ChooseItemActivity.class);
                intent.putExtra("availableItems", availableItems);
                startActivityForResult(intent, 2);
            }
        });
    }

    private void setItemAdapter() {
        itemAdapter = new SelectedItemAdapter(getApplicationContext(), itemList, new SelectedItemAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Item item) {
                tmpItem = item;
                ImageOptionDialog optionDialog = new ImageOptionDialog();
                optionDialog.setActivityFlag(DONATE_ACTIVITY_IMAGE_FLAG);
                optionDialog.show(getSupportFragmentManager(), "optionDialog");
            }
        });
        rvSelectedImages.setHasFixedSize(true);
        rvSelectedImages.setLayoutManager(new GridLayoutManager(this, 2));
        rvSelectedImages.setAdapter(itemAdapter);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        getComponent();

        // check if the request code is same as what is passed  here it is 2
        if (requestCode == 2) {
            if (data != null){
                Bundle bundle = data.getExtras();
                ArrayList<Item> selectedItems = (ArrayList<Item>) bundle.getSerializable("LISTCHOOSE");
                if (selectedItems.size() > 0){
                    for (int i = 0; i < selectedItems.size(); i++){
                        for (int j = 0; j < availableItems.size(); j++){
                            if (availableItems.get(j).getId() == selectedItems.get(i).getId()){
                                availableItems.remove(j);
                                break;
                            }
                        }
                    }
                    tmpItemList.addAll(selectedItems);
                }
                itemAdapter.setfilter(tmpItemList);
                setNoti();
            }
        }
    }

    private void createDonateTransaction() {
        loadProgressDialog();
        List<TransactionDetail> transactionDetailList = new ArrayList<>();
        for (int i = 0; i < itemList.size(); i++) { //create transaction detail
            TransactionDetail transactionDetail = new TransactionDetail();
            transactionDetail.setUserId(itemList.get(i).getUser().getId());
            transactionDetail.setItemId(itemList.get(i).getId());
            transactionDetailList.add(transactionDetail);
        }

        final Transaction transaction = new Transaction();
        transaction.setSenderId(userId);
        transaction.setReceiverId(donationPost.getUser().getId());
        transaction.setDonationPostId(donationPost.getId());
        TransactionRequestWrapper transactionRequestWrapper = new TransactionRequestWrapper(transaction, transactionDetailList);

        if (authorization != null) {
            Map<String, Object> jsonBody = new HashMap<String, Object>();
            jsonBody.put("transactionWrapper", transactionRequestWrapper);
            jsonBody.put("token", authorization);
            Log.i("jsonBody", jsonBody.toString());
            rmaAPIRealtime.sendTradeRequest(jsonBody).enqueue(new Callback<Object>() {
                @Override
                public void onResponse(Call<Object> call, Response<Object> response) {
                    if (response.body() != null){
                        Double tmpTransactionId = (Double) response.body();
                        Log.i("donatedTransaction", "" + tmpTransactionId);
                        final int transactionId = Integer.valueOf(tmpTransactionId.intValue());
                        Log.i("donatedTransaction", "" + transactionId);

                        rmaAPIService.getTransactionByTransID(authorization, transactionId).enqueue(new Callback<TransactionRequestWrapper>() {
                            @Override
                            public void onResponse(Call<TransactionRequestWrapper> call, Response<TransactionRequestWrapper> response) {
                                if (response.body() != null) {
                                    String qrCode = response.body().getTransaction().getQrCode();
                                    Intent intent = new Intent(context, TransactionDetailActivity.class);
                                    intent.putExtra("qrCode", qrCode);
                                    intent.putExtra("transactionId", transactionId);
                                    progressDialog.dismiss();
                                    startActivity(intent);
                                } else {
                                    progressDialog.dismiss();
                                    Toast.makeText(getApplicationContext(), R.string.error_loading, Toast.LENGTH_LONG).show();
                                    Log.i("donatedTransaction", "null");
                                }
                            }

                            @Override
                            public void onFailure(Call<TransactionRequestWrapper> call, Throwable t) {
                                Log.i("donatedTransaction", t.getMessage());
                                Toast.makeText(getApplicationContext(), R.string.error_loading, Toast.LENGTH_LONG).show();
                                progressDialog.dismiss();
                            }
                        });
                    }
                }

                @Override
                public void onFailure(Call<Object> call, Throwable t) {
                    Toast.makeText(getApplicationContext(), R.string.error_loading, Toast.LENGTH_LONG).show();
                    progressDialog.dismiss();
                }
            });
        }
    }

    private void getComponent() {
        donationPost = (DonationPost) getIntent().getSerializableExtra("donationPost");
        rmaAPIService = RmaAPIUtils.getAPIService();
        txtReceiverName = findViewById(R.id.txtReceiverName);
        txtReceiverName.setText(donationPost.getUser().getFullName());
        toolbar = findViewById(R.id.tbToolbar);
        rvSelectedImages = findViewById(R.id.rvSelectedImages);
        btnAddItems = findViewById(R.id.btnAddItems);
        txtNoti = findViewById(R.id.txtNoti);
        setNoti();

        sharedPreferences = getSharedPreferences("localData", MODE_PRIVATE);
        authorization = sharedPreferences.getString("authorization", null);
        userId = sharedPreferences.getInt("userId", 0);
        userPhoneNumber = sharedPreferences.getString("phoneNumber", null);
        userFullName = sharedPreferences.getString("username", null);
        rmaAPIRealtime = RmaAPIUtils.getRealtimeService();
    }

    private void setNoti() {
        if (itemList.size() == 0) {
            txtNoti.setVisibility(View.VISIBLE);
            rvSelectedImages.setVisibility(View.GONE);
        } else {
            txtNoti.setVisibility(View.GONE);
            rvSelectedImages.setVisibility(View.VISIBLE);
        }
    }

    private void setToolbar() {
        toolbar.setTitle(donationPost.getTitle());
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_confirm, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.btnConfirm) {
            if (itemList.size() == 0) {
                Toast.makeText(getApplicationContext(), R.string.select_item_noti, Toast.LENGTH_LONG).show();
            } else {
                createDonateTransaction();
            }
        }
        return true;
    }

    //when click on item of option dialog
    @Override
    public void onButtonClicked(int choice) {
        if (choice == DELETE_IMAGE_OPTION) {
            for (int i = 0; i < tmpItemList.size(); i++){
                if (tmpItemList.get(i).getId() == tmpItem.getId()){
                    tmpItemList.remove(i);
                    break;
                }
            }

            availableItems.add(tmpItem);
            itemAdapter.notifyDataSetChanged();
            itemAdapter.setfilter(tmpItemList);
            setNoti();
        }
    }

    private void loadAvailableItems() {

        if (authorization != null) {
            RmaAPIService rmaAPIService = RmaAPIUtils.getAPIService();
            rmaAPIService.getMyItems(authorization).enqueue(new Callback<List<Item>>() {
                @Override
                public void onResponse(Call<List<Item>> call, Response<List<Item>> response) {
                    Log.i("loadAvailableItems", "" + response.body().size());
                    List<Item> result = new ArrayList<>();
                    result = response.body();
                    if (result.size() > 0) {
                        List<Item> tmpAvailableItems = new ArrayList<>();
                        for (int i = 0; i < result.size(); i++) {
                            if (result.get(i).getStatus().equals(ITEM_ENABLE)) {
                                for (int j = 0; j < donationPost.getDonationPostTargets().size(); j++){
                                    DonationPostTarget tmpTarget = donationPost.getDonationPostTargets().get(j);
                                    if (result.get(i).getCategory().getId() == tmpTarget.getCategoryId()){
                                        tmpAvailableItems.add(result.get(i));
                                    }
                                }
                            }
                        }
                        if (tmpAvailableItems.size() > 0) {
                            availableItems.addAll(tmpAvailableItems);
                        }
                    }
                }

                @Override
                public void onFailure(Call<List<Item>> call, Throwable t) {
                    Log.i("loadAvailableItems", "" + t.getMessage());
                }
            });
        } else {
            Log.i("loadAvailableItems", "load failed");
        }
    }

    private void loadProgressDialog() {
        progressDialog = new ProgressDialog(context);
        progressDialog.setTitle(R.string.data_loading_noti);
        progressDialog.setMessage(String.valueOf(R.string.waiting_noti));
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
    }
}

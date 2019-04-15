package com.project.capstone.exchangesystem.activity;

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
import com.project.capstone.exchangesystem.R;
import com.project.capstone.exchangesystem.adapter.SelectedItemAdapter;
import com.project.capstone.exchangesystem.fragment.ImageOptionDialog;
import com.project.capstone.exchangesystem.model.*;
import com.project.capstone.exchangesystem.remote.RmaAPIService;
import com.project.capstone.exchangesystem.utils.RmaAPIUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.ArrayList;
import java.util.List;

import static com.project.capstone.exchangesystem.constants.AppStatus.*;

public class DonateItemActivity extends AppCompatActivity implements ImageOptionDialog.ImageOptionListener {

    TextView txtReceiverName;
    ArrayList<Item> itemList, tmpItemList, availableItems;

    DonationPost donationPost;
    SharedPreferences sharedPreferences;
    String authorization, userPhoneNumber, userFullName;
    RmaAPIService rmaAPIService;
    Integer userId;

    Context context;

    //new view
    Toolbar toolbar;
    RecyclerView rvSelectedImages;
    SelectedItemAdapter itemAdapter;
    ImageButton btnAddItems;
    Item tmpItem;
    TextView txtNoti;

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
        List<TransactionDetail> transactionDetailList = new ArrayList<>();
        for (int i = 0; i < itemList.size(); i++) { //create transaction detail
            TransactionDetail transactionDetail = new TransactionDetail();
            transactionDetail.setUserId(itemList.get(i).getUser().getId());
            transactionDetail.setItemId(itemList.get(i).getId());
            transactionDetailList.add(transactionDetail);
        }

        Transaction transaction = new Transaction();
        transaction.setSenderId(userId);
        transaction.setReceiverId(donationPost.getUser().getId());
        transaction.setDonationPostId(donationPost.getId());
        TransactionRequestWrapper transactionRequestWrapper = new TransactionRequestWrapper(transaction, transactionDetailList);

        Intent intent = new Intent(getApplicationContext(), InformationConfirmActivity.class);

        intent.putExtra("transaction", transactionRequestWrapper);
        intent.putExtra("senderAddress", itemList.get(0).getAddress());
        intent.putExtra("donationPost", donationPost);
        intent.putExtra("itemList", itemList);
        intent.putExtra("userPhoneNumber", userPhoneNumber);
        intent.putExtra("userFullName", userFullName);
        startActivity(intent);
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
                                tmpAvailableItems.add(result.get(i));
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
}

package com.project.capstone.exchangesystem.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.project.capstone.exchangesystem.R;
import com.project.capstone.exchangesystem.model.DonationPost;
import com.project.capstone.exchangesystem.model.Item;
import com.project.capstone.exchangesystem.model.Transaction;
import com.project.capstone.exchangesystem.model.TransactionDetail;
import com.project.capstone.exchangesystem.model.TransactionRequestWrapper;
import com.project.capstone.exchangesystem.remote.RmaAPIService;
import com.project.capstone.exchangesystem.utils.RmaAPIUtils;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InformationConfirmActivity extends AppCompatActivity {

    TransactionRequestWrapper transactionRequestWrapper;
    Transaction transaction;
    List<String> itemName;
    ArrayList<Item> itemList;
    TextView txtReceiverName, txtReceiverPhone, txtReceiverAddress, txtSenderName, txtSenderPhone, txtSenderAddress;
    ListView lvItemListOfReceiver, lvItemListOfSender;
    int userId;
    Toolbar toolbar;
    RmaAPIService rmaAPIService;
    SharedPreferences sharedPreferences;
    String authorization, senderAddress, userPhoneNumber, userFullName;
    DonationPost donationPost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information_confirm);

        getComponents();
        getDonationPostInf();
        getSenderInf();

        setToolbar();
    }

    private void getComponents() {
        txtReceiverName = findViewById(R.id.txtReceiverName);
        txtReceiverPhone = findViewById(R.id.txtReceiverPhone);
        txtReceiverAddress = findViewById(R.id.txtReceiverAddress);
        txtSenderName = findViewById(R.id.txtSenderName);
        txtSenderPhone = findViewById(R.id.txtSenderPhone);
        txtSenderAddress = findViewById(R.id.txtSenderAddress);
        lvItemListOfSender = findViewById(R.id.lvItemListOfSender);
        toolbar = findViewById(R.id.tbToolbar);

        sharedPreferences = getSharedPreferences("localData", MODE_PRIVATE);
        userId = sharedPreferences.getInt("userId", 0);
        authorization = sharedPreferences.getString("authorization", null);
        rmaAPIService = RmaAPIUtils.getAPIService();

        itemList = new ArrayList<>();
        itemList = (ArrayList<Item>) getIntent().getSerializableExtra("itemList");

        transactionRequestWrapper = (TransactionRequestWrapper) getIntent().getSerializableExtra("transaction");
        transaction = transactionRequestWrapper.getTransaction();

        senderAddress = (String) getIntent().getSerializableExtra("senderAddress");
        donationPost = (DonationPost) getIntent().getSerializableExtra("donationPost");
        itemList = (ArrayList<Item>) getIntent().getSerializableExtra("itemList");
        userPhoneNumber = (String) getIntent().getSerializableExtra("userPhoneNumber");
        userFullName = (String) getIntent().getSerializableExtra("userFullName");
    }

    private void getSenderInf() {
        itemName = new ArrayList<>();

        for (int i = 0; i < itemList.size(); i++){
            itemName.add(itemList.get(i).getName());
        }

        txtSenderName.setText(userFullName);
        txtSenderPhone.setText(userPhoneNumber);
        txtSenderAddress.setText(senderAddress);

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(getApplication(), R.layout.item_names, itemName);
        lvItemListOfSender.setAdapter(dataAdapter);
    }

    private void getDonationPostInf() {
        txtReceiverName.setText(donationPost.getUser().getFullName());
        txtReceiverPhone.setText(donationPost.getUser().getPhone());
        txtReceiverAddress.setText(donationPost.getAddress());
    }

    private void setToolbar() {
        toolbar.setTitle(R.string.confirm_information_title);
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
        if (transactionRequestWrapper.getTransaction().getDonationPostId() != null) {
            if (authorization != null) {
//                rmaAPIService.sendTradeRequest(authorization, transactionRequestWrapper).enqueue(new Callback<Object>() {
//                    @Override
//                    public void onResponse(Call<Object> call, Response<Object> response) {
//                        Toast.makeText(getApplicationContext(), "postID: " + response.message(), Toast.LENGTH_SHORT).show();
//                        goToSuccessPage();
//                    }
//
//                    @Override
//                    public void onFailure(Call<Object> call, Throwable t) {
//                        Toast.makeText(getApplicationContext(), R.string.error_loading, Toast.LENGTH_LONG).show();
//                    }
//                });
            }
        } else {
            if (userId == transactionRequestWrapper.getTransaction().getReceiverId()) {
                RmaAPIService rmaAPIService = RmaAPIUtils.getAPIService();
                rmaAPIService.confirmTransaction(authorization, transactionRequestWrapper.getTransaction().getId()).enqueue(new Callback<Object>() {
                    @Override
                    public void onResponse(Call<Object> call, Response<Object> response) {
                        if (response.isSuccessful()) {
                            goToSuccessPage();
                        }
                    }

                    @Override
                    public void onFailure(Call<Object> call, Throwable t) {
                        System.out.println("Vẫn chưa được, thử lại nào");
                    }
                });
            } else {
                goToSuccessPage();
            }
        }
        return true;
    }

    private void goToSuccessPage() {
        Intent intent = new Intent(getApplicationContext(), CreateSuccessActivity.class);
        intent.putExtra("address", txtReceiverAddress.getText().toString());
        startActivity(intent);
    }
}

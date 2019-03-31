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
    List<String> itemName;
    TextView txtReceiverName, txtReceiverPhone, txtReceiverAddress, txtSenderName, txtSenderPhone, txtSenderAddress;
    ListView lvItemListOfReceiver, lvItemListOfSender;
    int userId;
    Toolbar toolbar;
    RmaAPIService rmaAPIService;
    SharedPreferences sharedPreferences;
    String authorization;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information_confirm);
        getComponents();


        transactionRequestWrapper = (TransactionRequestWrapper) getIntent().getSerializableExtra("transaction");
        Transaction transaction = transactionRequestWrapper.getTransaction();
        getRecieverInf(transactionRequestWrapper, transaction);
        getSenderInf(transactionRequestWrapper, transaction);

        setToolbar();
//        userConfirmInformations.add(recieverInf);
//        userConfirmInformations.add(senderInf);
    }

    private void getComponents() {
        txtReceiverName = findViewById(R.id.txtReceiverName);
        txtReceiverPhone = findViewById(R.id.txtReceiverPhone);
        txtReceiverAddress = findViewById(R.id.txtReceiverAddress);
        txtSenderName = findViewById(R.id.txtSenderName);
        txtSenderPhone = findViewById(R.id.txtSenderPhone);
        txtSenderAddress = findViewById(R.id.txtSenderAddress);
        lvItemListOfReceiver = findViewById(R.id.lvItemListOfReceiver);
        lvItemListOfSender = findViewById(R.id.lvItemListOfSender);
        toolbar = findViewById(R.id.tbToolbar);

        sharedPreferences = getSharedPreferences("localData", MODE_PRIVATE);
        userId = sharedPreferences.getInt("userId", 0);
        authorization = sharedPreferences.getString("authorization", null);
        rmaAPIService = RmaAPIUtils.getAPIService();
    }

    private void getSenderInf(TransactionRequestWrapper transactionRequestWrapper, Transaction transaction) {
        String userName, phoneNumber, address = "";
        itemName = new ArrayList<>();
        if (transactionRequestWrapper.getTransaction().getDonationPostId() != null){
            userName = sharedPreferences.getString("username", null);
            phoneNumber = sharedPreferences.getString("phoneNumberSignIn", "Non");
            address = (String) getIntent().getSerializableExtra("senderAddress");
        } else {
            userName = transaction.getSender().getFullName();
            phoneNumber = transaction.getSender().getPhone();

            for (int i = 0; i < transactionRequestWrapper.getDetails().size(); i++) {
                TransactionDetail tmpTransactionDetail = transactionRequestWrapper.getDetails().get(i);
                if (tmpTransactionDetail.getUserId() == transaction.getSenderId()) {
                    if (address.equals("")) {
                        address = tmpTransactionDetail.getItem().getAddress();
                    }
                    itemName.add(tmpTransactionDetail.getItem().getName());
                }
            }
            //TODO get adress when sender doesn't have any item to exchange
        }

        if (userId == transaction.getSenderId()) {
            setSenderInfView(userName, phoneNumber, address);
        } else {
            setReceiverInfView(userName, phoneNumber, address);
        }

    }

    private void setReceiverInfView(String userName, String phoneNumber, String address) {
        txtReceiverName.setText(userName);
        txtReceiverPhone.setText(phoneNumber);
        txtReceiverAddress.setText(address);

        if (itemName.size() != 0){
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(getApplication(), R.layout.item_names, itemName);
            lvItemListOfReceiver.setAdapter(dataAdapter);
        }
    }

    private void setSenderInfView(String userName, String phoneNumber, String address) {
        txtSenderName.setText(userName);
        txtSenderPhone.setText(phoneNumber);
        txtSenderAddress.setText(address);

        if (itemName.size() != 0){
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(getApplication(), R.layout.item_names, itemName);
            lvItemListOfSender.setAdapter(dataAdapter);
        }
    }

    private void getRecieverInf(TransactionRequestWrapper transactionRequestWrapper, Transaction transaction) {
//        String userName = transaction.getReceiver().getFullName();
//        String address = "";
        String userName, phoneNumber, address = "";
        itemName = new ArrayList<>();
        if (transactionRequestWrapper.getTransaction().getDonationPostId() != null){
            DonationPost donationPost = (DonationPost) getIntent().getSerializableExtra("donationPost");
            userName = donationPost.getUser().getFullName();
            phoneNumber = donationPost.getUser().getPhone();
            address = donationPost.getAddress();
        } else {
            userName = transaction.getReceiver().getFullName();
            phoneNumber = transaction.getReceiver().getPhone();

            for (int i = 0; i < transactionRequestWrapper.getDetails().size(); i++) {
                TransactionDetail tmpTransactionDetail = transactionRequestWrapper.getDetails().get(i);
                if (tmpTransactionDetail.getUserId() == transaction.getReceiverId()) {
                    if (address.equals("")) {
                        address = tmpTransactionDetail.getItem().getAddress();
                    }
                    itemName.add(tmpTransactionDetail.getItem().getName());
                }
            }
        }

        if (userId == transaction.getReceiverId()) {
            setSenderInfView(userName, phoneNumber, address);
        } else {
            setReceiverInfView(userName, phoneNumber, address);
        }
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
        if (transactionRequestWrapper.getTransaction().getDonationPostId() != null){
            if (authorization != null) {
                rmaAPIService.sendTradeRequest(authorization, transactionRequestWrapper).enqueue(new Callback<Object>() {
                    @Override
                    public void onResponse(Call<Object> call, Response<Object> response) {
                        Toast.makeText(getApplicationContext(), "postID: " + response.message(), Toast.LENGTH_SHORT).show();
                        goToSuccessPage();
                    }

                    @Override
                    public void onFailure(Call<Object> call, Throwable t) {
                        Toast.makeText(getApplicationContext(), R.string.error_loading, Toast.LENGTH_LONG).show();
                    }
                });
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
//        Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + txtReceiverAddress.getText().toString());
////        Uri gmmIntentUri = Uri.parse("google.navigation:q=" + txtReceiverAddress.getText().toString());
//        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
//        mapIntent.setPackage("com.google.android.apps.maps");
//        startActivity(mapIntent);
        return true;
    }

    private void goToSuccessPage() {
        Intent intent = new Intent(getApplicationContext(), CreateSuccessActivity.class);
        intent.putExtra("address", txtReceiverAddress.getText().toString());
        startActivity(intent);
    }
}

package com.project.capstone.exchangesystem.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import com.project.capstone.exchangesystem.R;
import com.project.capstone.exchangesystem.adapter.ItemAdapter;
import com.project.capstone.exchangesystem.model.Item;
import com.project.capstone.exchangesystem.model.Transaction;
import com.project.capstone.exchangesystem.model.TransactionDetail;
import com.project.capstone.exchangesystem.model.TransactionRequestWrapper;

import java.util.ArrayList;
import java.util.List;

public class TransactionConfirmActivity extends AppCompatActivity {
    RecyclerView itemsMeConfirm, itemsYouConfirm;
    ArrayList<Item> inventoryMe, inventoryYou;
    ItemAdapter itemMeAdapter, itemYouAdapter;
    Button btnConfirmTradeRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_confirm);
        direct();
        getData();

    }


    private void direct() {

        itemsMeConfirm = findViewById(R.id.itemsMeConfirm);
        itemsYouConfirm = findViewById(R.id.itemsYouConfirm);

        inventoryYou = new ArrayList<>();
        inventoryMe = new ArrayList<>();

        btnConfirmTradeRequest = findViewById(R.id.btnConfirmTradeRequest);


        itemMeAdapter = new ItemAdapter(getApplicationContext(), inventoryMe, new ItemAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Item item) {

            }
        });

        itemsMeConfirm.setHasFixedSize(true);
        itemsMeConfirm.setLayoutManager(new GridLayoutManager(getApplicationContext(), 2));
        itemsMeConfirm.setAdapter(itemMeAdapter);


        itemYouAdapter = new ItemAdapter(getApplicationContext(), inventoryYou, new ItemAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Item item) {

            }
        });

        itemsYouConfirm.setHasFixedSize(true);
        itemsYouConfirm.setLayoutManager(new GridLayoutManager(getApplicationContext(), 2));
        itemsYouConfirm.setAdapter(itemYouAdapter);
    }


    private void getData() {
        SharedPreferences sharedPreferences = getSharedPreferences("localData", MODE_PRIVATE);
        Intent intent = this.getIntent();
        TransactionRequestWrapper transactionRequestWrapper = (TransactionRequestWrapper) intent.getSerializableExtra("transactionDetail");
        Transaction informationTransaction = transactionRequestWrapper.getTransaction();
        final int youID = informationTransaction.getSenderId();
        int meID = sharedPreferences.getInt("userId", 0);

        if (informationTransaction.getStatus().equals("1")) {

            btnConfirmTradeRequest.setText("Confirm");
            btnConfirmTradeRequest.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        } else if (informationTransaction.getStatus().equals("2")) {
            btnConfirmTradeRequest.setText("Traded");
            btnConfirmTradeRequest.setClickable(false);

        }


        List<TransactionDetail> itemTrade = transactionRequestWrapper.getDetails();
        for (int i = 0; i < itemTrade.size(); i++) {
            if (itemTrade.get(i).getUserId() == youID) {
                inventoryYou.add(itemTrade.get(i).getItem());
                itemYouAdapter.notifyDataSetChanged();
            } else {
                inventoryMe.add(itemTrade.get(i).getItem());
                itemMeAdapter.notifyDataSetChanged();
            }


        }


    }
}

package com.project.capstone.exchangesystem.Activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.GridView;
import android.widget.Toast;
import com.project.capstone.exchangesystem.R;
import com.project.capstone.exchangesystem.Utils.RmaAPIUtils;
import com.project.capstone.exchangesystem.adapter.ItemAdapter;
import com.project.capstone.exchangesystem.model.Item;
import com.project.capstone.exchangesystem.model.Transaction;
import com.project.capstone.exchangesystem.model.TransactionDetail;
import com.project.capstone.exchangesystem.model.TransactionRequestWrapper;
import com.project.capstone.exchangesystem.remote.RmaAPIService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.ArrayList;
import java.util.List;

public class TransactionDetailActivity extends AppCompatActivity {
    RecyclerView itemsMe, itemsYou;
    ArrayList<Item> inventoryMe, inventoryYou;
    ItemAdapter itemMeAdapter, itemYouAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_detail);
        direct();
        getData();
    }


    private void direct() {
        itemsMe = findViewById(R.id.itemsMe);
        itemsYou = findViewById(R.id.itemsYou);

        inventoryYou = new ArrayList<>();
        inventoryMe = new ArrayList<>();


        itemMeAdapter = new ItemAdapter(getApplicationContext(), inventoryMe, new ItemAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Item item) {

            }
        });

        itemsMe.setHasFixedSize(true);
        itemsMe.setLayoutManager(new GridLayoutManager(getApplicationContext(), 2));
        itemsMe.setAdapter(itemMeAdapter);



        itemYouAdapter = new ItemAdapter(getApplicationContext(), inventoryYou, new ItemAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Item item) {

            }
        });

        itemsYou.setHasFixedSize(true);
        itemsYou.setLayoutManager(new GridLayoutManager(getApplicationContext(), 2));
        itemsYou.setAdapter(itemYouAdapter);

    }

    private void getData() {

        SharedPreferences sharedPreferences = getSharedPreferences("localData", MODE_PRIVATE);
        Intent intent = this.getIntent();
        TransactionRequestWrapper transactionRequestWrapper = (TransactionRequestWrapper) intent.getSerializableExtra("transactionDetail");
        Transaction informationTransaction = transactionRequestWrapper.getTransaction();
        final int youID = informationTransaction.getReceiverId();
        int meID = sharedPreferences.getInt("userId", 0);


        List<TransactionDetail> itemTrade = transactionRequestWrapper.getDetails();

        for (int i = 0; i < itemTrade.size(); i++) {
            int tempTradeItemID = itemTrade.get(i).getItemId();
            RmaAPIService rmaAPIService = RmaAPIUtils.getAPIService();
            rmaAPIService.getItemById(tempTradeItemID).enqueue(new Callback<Item>() {
                @Override
                public void onResponse(Call<Item> call, Response<Item> response) {
                    System.out.println("vào response rồi");
                    if (response.isSuccessful()) {
                        System.out.println("vào suscess rồi");
                        Item item = response.body();

                        if (item.getUser().getId() == youID) {
                            System.out.println("đã được add vào You");
                            inventoryYou.add(item);
                            itemYouAdapter.notifyDataSetChanged();
                        } else {
                            inventoryMe.add(item);
                            itemMeAdapter.notifyDataSetChanged();
                            System.out.println("đã được add vào Me");
                        }
                    }
                }

                @Override
                public void onFailure(Call<Item> call, Throwable t) {
                    System.out.println("Fail rồi");
                }
            });
        }
    }
}

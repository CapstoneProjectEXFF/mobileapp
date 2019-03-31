package com.project.capstone.exchangesystem.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.view.View;
import com.project.capstone.exchangesystem.R;
import com.project.capstone.exchangesystem.utils.RmaAPIUtils;
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

import static android.content.Context.MODE_PRIVATE;
import static com.facebook.FacebookSdk.getApplicationContext;

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

                    if (response.isSuccessful()) {

                        Item item = response.body();

                        if (item.getUser().getId() == youID) {

                            inventoryYou.add(item);
                            itemYouAdapter.notifyDataSetChanged();
                        } else {
                            inventoryMe.add(item);
                            itemMeAdapter.notifyDataSetChanged();
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

    public void toMain(View view) {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }

    private void toUpdateTransaction(View view) {
        SharedPreferences sharedPreferences = getSharedPreferences("localData", MODE_PRIVATE);
        int idMeUpdate = sharedPreferences.getInt("userId", 0);
        int idYouUpdate = 0;

        Intent intent = this.getIntent();
        final TransactionRequestWrapper transactionRequestWrapper = (TransactionRequestWrapper) intent.getSerializableExtra("transactionDetail");
        Transaction informationTransaction = transactionRequestWrapper.getTransaction();
        if (informationTransaction.getSender().getId() == idMeUpdate) {
            idYouUpdate = informationTransaction.getReceiver().getId();
        } else {
            idYouUpdate = informationTransaction.getSender().getId();
        }

        ArrayList<Item> itemsMe = itemMeAdapter.getfilter();
        ArrayList<Item> itemsYou = itemYouAdapter.getfilter();

        intent = new Intent(this, UpdateTransactionActivity.class);
        intent.putExtra("itemsMeUpdate", itemsMe);
        intent.putExtra("itemsYouUpdate", itemsYou);
        intent.putExtra("idMeUpdate", idMeUpdate);
        intent.putExtra("idYouUpdate", idYouUpdate);

        startActivity(intent);

    }
}

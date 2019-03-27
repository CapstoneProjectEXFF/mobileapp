package com.project.capstone.exchangesystem.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.project.capstone.exchangesystem.R;
import com.project.capstone.exchangesystem.adapter.ItemAdapter;
import com.project.capstone.exchangesystem.model.Item;
import com.project.capstone.exchangesystem.model.Transaction;
import com.project.capstone.exchangesystem.model.TransactionDetail;
import com.project.capstone.exchangesystem.model.TransactionRequestWrapper;
import com.project.capstone.exchangesystem.remote.RmaAPIService;
import com.project.capstone.exchangesystem.utils.RmaAPIUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class TransactionConfirmActivity extends AppCompatActivity {
    RecyclerView itemsMeConfirm, itemsYouConfirm;
    ArrayList<Item> inventoryMe, inventoryYou;
    ItemAdapter itemMeAdapter, itemYouAdapter;
    Button btnConfirmTradeRequest;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_confirm);
        direct();
        getData();
        ActionToolbar();

    }


    private void direct() {
        toolbar = findViewById(R.id.transactionconfirmToolbar);

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
        final String authorization = sharedPreferences.getString("authorization", null);

        Intent intent = this.getIntent();
        final TransactionRequestWrapper transactionRequestWrapper = (TransactionRequestWrapper) intent.getSerializableExtra("transactionDetail");
        Transaction informationTransaction = transactionRequestWrapper.getTransaction();
        final int youID = informationTransaction.getSenderId();
        int meID = sharedPreferences.getInt("userId", 0);

        if (informationTransaction.getStatus().equals("1")) {

            btnConfirmTradeRequest.setText("Confirm");
            btnConfirmTradeRequest.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    RmaAPIService rmaAPIService = RmaAPIUtils.getAPIService();
                    rmaAPIService.confirmTransaction(authorization, transactionRequestWrapper.getTransaction().getId()).enqueue(new Callback<Object>() {
                        @Override
                        public void onResponse(Call<Object> call, Response<Object> response) {
                            if (response.isSuccessful()) {
                                System.out.println("Thành công rồi nghỉ thôi");
                                btnConfirmTradeRequest.setText("Traded");
                                btnConfirmTradeRequest.setClickable(false);
                                Toast.makeText(getApplicationContext(), "Traded Successfully", Toast.LENGTH_SHORT).show();

                            }
                        }

                        @Override
                        public void onFailure(Call<Object> call, Throwable t) {
                            System.out.println("Vẫn chưa được, thử lại nào");
                        }
                    });

                }
            });
        } else if (informationTransaction.getStatus().equals("2")) {
            btnConfirmTradeRequest.setText("Traded");
            btnConfirmTradeRequest.setClickable(false);

        }


        List<TransactionDetail> itemTrade = transactionRequestWrapper.getDetails();
        for (int i = 0; i < itemTrade.size(); i++) {
//            Integer.valueOf()
            if (itemTrade.get(i).getUserId().toString().equals(Integer.valueOf(meID).toString())) {
                inventoryMe.add(itemTrade.get(i).getItem());
                itemMeAdapter.notifyDataSetChanged();
            } else {
                inventoryYou.add(itemTrade.get(i).getItem());
                itemYouAdapter.notifyDataSetChanged();
            }


        }


    }

    private void ActionToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void toUpdateTransaction(View view) {
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
        intent.putExtra("transactionDetail", transactionRequestWrapper);

        startActivity(intent);

    }
}

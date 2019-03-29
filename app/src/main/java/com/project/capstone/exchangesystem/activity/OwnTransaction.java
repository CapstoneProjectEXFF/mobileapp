package com.project.capstone.exchangesystem.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import com.project.capstone.exchangesystem.R;
import com.project.capstone.exchangesystem.adapter.TransactionHistoryAdapter;
import com.project.capstone.exchangesystem.adapter.TransactionNotificationAdapter;
import com.project.capstone.exchangesystem.model.Transaction;
import com.project.capstone.exchangesystem.model.TransactionRequestWrapper;
import com.project.capstone.exchangesystem.remote.RmaAPIService;
import com.project.capstone.exchangesystem.utils.RmaAPIUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.ArrayList;
import java.util.List;

public class OwnTransaction extends AppCompatActivity {
    Toolbar toolbar;
    ListView listView;
    TransactionHistoryAdapter transactionHistoryAdapter;
    ArrayList<Transaction> transactions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_own_transaction);

        direct();
        actionToolbar();
    }

    private void direct() {
        toolbar = findViewById(R.id.transactionToolbar);
        final RmaAPIService rmaAPIService = RmaAPIUtils.getAPIService();
        SharedPreferences sharedPreferences = getSharedPreferences("localData", MODE_PRIVATE);
        final String authorization = sharedPreferences.getString("authorization", null);
        listView = (ListView) findViewById(R.id.transactionHistoryListview);
        transactions = new ArrayList<>();
        transactionHistoryAdapter = new TransactionHistoryAdapter(this, transactions);
        listView.setAdapter(transactionHistoryAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                rmaAPIService.getAllTransactionByUserID(authorization).enqueue(new Callback<List<Transaction>>() {
                    @Override
                    public void onResponse(Call<List<Transaction>> call, Response<List<Transaction>> response) {

                    }

                    @Override
                    public void onFailure(Call<List<Transaction>> call, Throwable t) {

                    }
                });
            }
        });
    }

    private void actionToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}

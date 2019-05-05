package com.project.capstone.exchangesystem.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.project.capstone.exchangesystem.R;
import com.project.capstone.exchangesystem.adapter.TransactionHistoryAdapter;
import com.project.capstone.exchangesystem.adapter.TransactionNotificationAdapter;
import com.project.capstone.exchangesystem.model.Transaction;
import com.project.capstone.exchangesystem.model.TransactionDetail;
import com.project.capstone.exchangesystem.model.TransactionRequestWrapper;
import com.project.capstone.exchangesystem.remote.RmaAPIService;
import com.project.capstone.exchangesystem.utils.RmaAPIUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.ArrayList;
import java.util.List;

import static com.facebook.FacebookSdk.getApplicationContext;

public class OwnTransaction extends AppCompatActivity {

    Toolbar toolbar;
    ListView listView;
    TextView txtNoHistory;

    TransactionHistoryAdapter transactionHistoryAdapter;
    ArrayList<Transaction> transactions;
    RmaAPIService rmaAPIService;
    SharedPreferences sharedPreferences;
    String authorization;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_own_transaction);

        direct();
        getData();
        actionToolbar();
    }

    private void direct() {
        rmaAPIService = RmaAPIUtils.getAPIService();
        sharedPreferences = getSharedPreferences("localData", MODE_PRIVATE);
        authorization = sharedPreferences.getString("authorization", null);
        rmaAPIService = RmaAPIUtils.getAPIService();

        toolbar = findViewById(R.id.transactionToolbar);
        txtNoHistory = findViewById(R.id.txtNoHistory);
        listView = findViewById(R.id.transactionHistoryListview);
        transactions = new ArrayList<>();
        transactionHistoryAdapter = new TransactionHistoryAdapter(this, transactions);

        listView.setAdapter(transactionHistoryAdapter);
        context = this;
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

    private void getData() {

        if (authorization != null){
            rmaAPIService.getAllTransactionByUserID(authorization).enqueue(new Callback<List<Transaction>>() {
                @Override
                public void onResponse(Call<List<Transaction>> call, Response<List<Transaction>> response) {

                    if (response.isSuccessful()) {
                        List<Transaction> temp = response.body();

                        if (temp.size() == 0) {
                            listView.setVisibility(View.GONE);
                            txtNoHistory.setVisibility(View.VISIBLE);
                        } else {
                            transactions.addAll(temp);
                            transactionHistoryAdapter.notifyDataSetChanged();
                        }
                    }
                }

                @Override
                public void onFailure(Call<List<Transaction>> call, Throwable t) {
                    System.out.println("Fail rồi");
                    Toast.makeText(getApplicationContext(), "Error Server", Toast.LENGTH_LONG).show();
                }
            });
        }

    }
}

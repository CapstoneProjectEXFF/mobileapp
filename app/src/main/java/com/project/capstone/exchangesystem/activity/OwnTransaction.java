package com.project.capstone.exchangesystem.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
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
    TransactionHistoryAdapter transactionHistoryAdapter;
    ArrayList<Transaction> transactions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_own_transaction);

        direct();
        getData();
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
                Toast.makeText(getApplicationContext(), "vào được!! Try Again", Toast.LENGTH_LONG).show();
                rmaAPIService.getTransactionByTransID(authorization, transactions.get(position).getId()).enqueue(new Callback<TransactionRequestWrapper>() {
                    @Override
                    public void onResponse(Call<TransactionRequestWrapper> call, Response<TransactionRequestWrapper> response) {
                        if (response.isSuccessful()) {
                            TransactionRequestWrapper temp = response.body();
                            Intent intent = new Intent(OwnTransaction.this, TransactionDetailActivity.class);
                            intent.putExtra("transactionDetail", temp);
                            startActivity(intent);
                        } else {
                            Toast.makeText(getApplicationContext(), "Error in data!! Try Again", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<TransactionRequestWrapper> call, Throwable t) {
                        System.out.println("fail in daa");
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

    private void getData() {
        SharedPreferences sharedPreferences = getSharedPreferences("localData", MODE_PRIVATE);
        String authorization = sharedPreferences.getString("authorization", null);

        RmaAPIService rmaAPIService = RmaAPIUtils.getAPIService();
        rmaAPIService.getAllTransactionByUserID(authorization).enqueue(new Callback<List<Transaction>>() {
            @Override
            public void onResponse(Call<List<Transaction>> call, Response<List<Transaction>> response) {

                if (response.isSuccessful()) {
                    List<Transaction> temp = new ArrayList<>();
                    temp = response.body();
                    transactions.addAll(temp);
                    transactionHistoryAdapter.notifyDataSetChanged();
                    if (temp.size() == 0) {
                        Toast.makeText(getApplicationContext(), "Your Own Transaction is Empty", Toast.LENGTH_LONG).show();
                        finish();
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

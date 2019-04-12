package com.project.capstone.exchangesystem.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import com.project.capstone.exchangesystem.R;
import com.project.capstone.exchangesystem.activity.TransactionDetailActivity;
import com.project.capstone.exchangesystem.adapter.TransactionNotificationAdapter;
import com.project.capstone.exchangesystem.model.Relationship;
import com.project.capstone.exchangesystem.model.Transaction;
import com.project.capstone.exchangesystem.model.TransactionRequestWrapper;
import com.project.capstone.exchangesystem.remote.RmaAPIService;
import com.project.capstone.exchangesystem.utils.RmaAPIUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;
import static com.facebook.FacebookSdk.getApplicationContext;


public class NotificationFragment extends Fragment {
    ListView listView;
    TransactionNotificationAdapter transactionNotificationAdapter;
    ArrayList<Object> transactions;
    Toolbar toolbar;


    public NotificationFragment() {
    }


    public static NotificationFragment newInstance() {
        NotificationFragment fragment = new NotificationFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_notification, container, false);

        final RmaAPIService rmaAPIService = RmaAPIUtils.getAPIService();
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("localData", MODE_PRIVATE);
        String userPhoneNumber = sharedPreferences.getString("phoneNumberSignIn", "Non");
        final String authorization = sharedPreferences.getString("authorization", null);

        listView = (ListView) view.findViewById(R.id.notificationListview);
        transactions = new ArrayList<>();
        transactionNotificationAdapter = new TransactionNotificationAdapter(view.getContext(), transactions);
        toolbar = view.findViewById(R.id.notificationToolbar);
        listView.setAdapter(transactionNotificationAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                Intent intent = new Intent(getActivity(), TransactionDetailActivity.class);
                Transaction tempTrans = (Transaction) transactions.get(position);
                rmaAPIService.getTransactionByTransID(authorization, tempTrans.getId()).enqueue(new Callback<TransactionRequestWrapper>() {

                    @Override
                    public void onResponse(Call<TransactionRequestWrapper> call, Response<TransactionRequestWrapper> response) {
                        if (response.isSuccessful()) {

                        }
                    }

                    @Override
                    public void onFailure(Call<TransactionRequestWrapper> call, Throwable t) {

                    }
                });


            }
        });
        getDataFromTransaction();
        getDataFromRelationship();
        ActionToolbar();
        return view;

    }

    private void ActionToolbar() {
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(R.string.notification_title);
    }

    private void getDataFromTransaction() {

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("localData", MODE_PRIVATE);
        String userPhoneNumber = sharedPreferences.getString("phoneNumberSignIn", "Non");
        String authorization = sharedPreferences.getString("authorization", null);

        RmaAPIService rmaAPIService = RmaAPIUtils.getAPIService();
        rmaAPIService.getTransactionsByReceiverID(authorization).enqueue(new Callback<List<Transaction>>() {
            @Override
            public void onResponse(Call<List<Transaction>> call, Response<List<Transaction>> response) {

                if (response.isSuccessful()) {
                    List<Transaction> temp = new ArrayList<>();
                    temp = response.body();
                    transactions.addAll(temp);
                    transactionNotificationAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<List<Transaction>> call, Throwable t) {
                System.out.println("Fail rồi");
                Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_LONG).show();

            }
        });

        rmaAPIService.getTransactionsTradedBySenderId(authorization).enqueue(new Callback<List<Transaction>>() {
            @Override
            public void onResponse(Call<List<Transaction>> call, Response<List<Transaction>> response) {
                if (response.isSuccessful()) {
                    List<Transaction> temp = new ArrayList<>();
                    temp = response.body();
                    transactions.addAll(temp);
                    transactionNotificationAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<List<Transaction>> call, Throwable t) {
                System.out.println("Fail rồi");
                Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_LONG).show();
            }
        });

        transactionNotificationAdapter.notifyDataSetChanged();


    }

    private void getDataFromRelationship() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("localData", MODE_PRIVATE);
        String userPhoneNumber = sharedPreferences.getString("phoneNumberSignIn", "Non");
        String authorization = sharedPreferences.getString("authorization", null);
        RmaAPIService rmaAPIService = RmaAPIUtils.getAPIService();
        rmaAPIService.getFriendRequest(authorization, 0, 10).enqueue(new Callback<List<Relationship>>() {
            @Override
            public void onResponse(Call<List<Relationship>> call, Response<List<Relationship>> response) {
                if (response.isSuccessful()) {
                    List<Relationship> temp = new ArrayList<>();
                    temp = response.body();
                    transactions.addAll(temp);
                    transactionNotificationAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<List<Relationship>> call, Throwable t) {

            }
        });


    }


}

package com.project.capstone.exchangesystem.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import com.project.capstone.exchangesystem.R;
import com.project.capstone.exchangesystem.activity.TradeRealtimeActivity;
import com.project.capstone.exchangesystem.activity.TransactionDetailActivity;
import com.project.capstone.exchangesystem.adapter.TransactionNotificationAdapter;
import com.project.capstone.exchangesystem.model.Relationship;
import com.project.capstone.exchangesystem.model.Room;
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
    RmaAPIService rmaRealtimeService, rmaAPIService;
    SharedPreferences sharedPreferences;
    String authorization;
    int userId;


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
        rmaAPIService = RmaAPIUtils.getAPIService();
        rmaRealtimeService = RmaAPIUtils.getRealtimeService();
        sharedPreferences = getActivity().getSharedPreferences("localData", MODE_PRIVATE);
        authorization = sharedPreferences.getString("authorization", "");
        userId = sharedPreferences.getInt("userId", 0);
        listView = (ListView) view.findViewById(R.id.notificationListview);
        transactions = new ArrayList<>();
        transactionNotificationAdapter = new TransactionNotificationAdapter(view.getContext(), transactions);
        toolbar = view.findViewById(R.id.notificationToolbar);
        listView.setAdapter(transactionNotificationAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                Intent intent = new Intent(getActivity(), TransactionDetailActivity.class);
                if (transactions.get(position).getClass() == Transaction.class) {
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
                } else if (transactions.get(position).getClass() == Room.class) {
                    Room room = (Room) transactions.get(position);
                    Intent intent2 = new Intent(view.getContext(), TradeRealtimeActivity.class);
                    intent2.putExtra("room", room);
                    startActivity(intent2);
                }

            }
        });
        getDataFromTransaction();
        getDataFromRelationship();
        getDataFromRoom();
        ActionToolbar();
        return view;

    }

    private void ActionToolbar() {
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.notification_title);
    }

    private void getDataFromTransaction() {

//        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("localData", MODE_PRIVATE);
//        String userPhoneNumber = sharedPreferences.getString("phoneNumberSignIn", "Non");
//        String authorization = sharedPreferences.getString("authorization", null);
//        RmaAPIService rmaAPIService = RmaAPIUtils.getAPIService();

        rmaAPIService.getTransactionsByReceiverID(authorization).enqueue(new Callback<List<Transaction>>() {
            @Override
            public void onResponse(Call<List<Transaction>> call, Response<List<Transaction>> response) {

                if (response.isSuccessful()) {
                    List<Transaction> temp = new ArrayList<>();
                    temp = response.body();
                    transactions.addAll(temp);
                    transactionNotificationAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getApplicationContext(), R.string.error_request, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<List<Transaction>> call, Throwable t) {
                Toast.makeText(getApplicationContext(), R.string.error_server, Toast.LENGTH_LONG).show();
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

        rmaAPIService.getDonationnTransactionByAgentID(authorization).enqueue(new Callback<List<TransactionRequestWrapper>>() {
            @Override
            public void onResponse(Call<List<TransactionRequestWrapper>> call, Response<List<TransactionRequestWrapper>> response) {
                if (response.isSuccessful()) {
                    List<TransactionRequestWrapper> temp = new ArrayList<>();
                    temp = response.body();
                    for (int i = 0; i < temp.size(); i++) {
                        transactions.add(temp.get(i).getTransaction());
                        transactionNotificationAdapter.notifyDataSetChanged();

                    }
//                    transactions.addAll(temp);
                }
            }

            @Override
            public void onFailure(Call<List<TransactionRequestWrapper>> call, Throwable t) {
                System.out.println("Fail rồi");
                Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_LONG).show();
            }
        });

        transactionNotificationAdapter.notifyDataSetChanged();

    }

    private void getDataFromRelationship() {
//        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("localData", MODE_PRIVATE);
//        String userPhoneNumber = sharedPreferences.getString("phoneNumberSignIn", "Non");
//        String authorization = sharedPreferences.getString("authorization", null);
//        RmaAPIService rmaAPIService = RmaAPIUtils.getAPIService();
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

    private void getDataFromRoom() {
        rmaRealtimeService.loadRoomByUserId(userId).enqueue(new Callback<List<Room>>() {
            @Override
            public void onResponse(Call<List<Room>> call, Response<List<Room>> response) {
                if (response.body() != null) {
                    Log.i("room size: ", "" + response.body().size());
                    List<Room> tmpRooms = response.body();
                    transactions.addAll(tmpRooms);
                    transactionNotificationAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<List<Room>> call, Throwable t) {
                Log.i("messageTab", "failed");

            }
        });
    }


}

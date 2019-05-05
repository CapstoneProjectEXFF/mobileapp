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
import com.project.capstone.exchangesystem.activity.MainActivity;
import com.project.capstone.exchangesystem.activity.TradeRealtimeActivity;
import com.project.capstone.exchangesystem.activity.TransactionDetailActivity;
import com.project.capstone.exchangesystem.adapter.TransactionNotificationAdapter;
import com.project.capstone.exchangesystem.constants.AppStatus;
import com.project.capstone.exchangesystem.model.NotiTransaction;
import com.project.capstone.exchangesystem.model.Relationship;
import com.project.capstone.exchangesystem.model.Room;
import com.project.capstone.exchangesystem.model.Transaction;
import com.project.capstone.exchangesystem.model.TransactionRequestWrapper;
import com.project.capstone.exchangesystem.remote.RmaAPIService;
import com.project.capstone.exchangesystem.sockets.SocketServer;
import com.project.capstone.exchangesystem.utils.RmaAPIUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;
import static com.facebook.FacebookSdk.getApplicationContext;
import static com.project.capstone.exchangesystem.constants.AppStatus.TRADE_DONE_MESSAGE;
import static com.project.capstone.exchangesystem.constants.AppStatus.TRANSACTION_DONATED;


public class NotificationFragment extends Fragment {
    ListView listView;
    TransactionNotificationAdapter transactionNotificationAdapter;
    ArrayList<Object> transactions;
    Toolbar toolbar;
    RmaAPIService rmaRealtimeService, rmaAPIService;
    SharedPreferences sharedPreferences;
    String authorization;
    int userId;
    SocketServer socketServer;
    MainActivity mainActivity;

    public NotificationFragment() {
    }


    public static NotificationFragment newInstance() {
        NotificationFragment fragment = new NotificationFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        rmaAPIService = RmaAPIUtils.getAPIService();
        rmaRealtimeService = RmaAPIUtils.getRealtimeService();
        sharedPreferences = getActivity().getSharedPreferences("localData", MODE_PRIVATE);
        authorization = sharedPreferences.getString("authorization", "");
        userId = sharedPreferences.getInt("userId", 0);

        mainActivity = (MainActivity) getActivity();
        socketServer = mainActivity.getSocketServer();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_notification, container, false);
        getComponents(view);

        ActionToolbar();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        transactions.clear();
        getDataFromTransaction();
        getDataFromRelationship();
        getNotiTransaction();
    }

    private void getNotiTransaction() {
        rmaRealtimeService.getNotiTransaction(userId).enqueue(new Callback<List<NotiTransaction>>() {
            @Override
            public void onResponse(Call<List<NotiTransaction>> call, Response<List<NotiTransaction>> response) {
                if (response.body() != null) {
                    transactions.addAll(response.body());
                    transactionNotificationAdapter.notifyDataSetChanged();
                } else {
                    Log.i("getNotiTransaction", "null");
                }
            }

            @Override
            public void onFailure(Call<List<NotiTransaction>> call, Throwable t) {
                Log.i("getNotiTransaction", "" + t.getMessage());
            }
        });
    }

    private void getComponents(View view) {
        listView = (ListView) view.findViewById(R.id.notificationListview);
        transactions = new ArrayList<>();
        transactionNotificationAdapter = new TransactionNotificationAdapter(view.getContext(), transactions);
        toolbar = view.findViewById(R.id.notificationToolbar);
        listView.setAdapter(transactionNotificationAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

                if (transactions.get(position).getClass() == Transaction.class) {
                    Transaction transaction = (Transaction) transactions.get(position);
                    Intent intent = new Intent(view.getContext(), TransactionDetailActivity.class);
                    intent.putExtra("transactionId", transaction.getId());
                    startActivity(intent);
                }  else if (transactions.get(position).getClass() == NotiTransaction.class) {
                    NotiTransaction notiTransaction = (NotiTransaction) transactions.get(position);
                    socketServer.emitNotiRead(notiTransaction.getNotification().getId());

                    switch (notiTransaction.getNotification().getStatus()) {
                        case TRADE_DONE_MESSAGE:
                            Intent transDetail = new Intent(getActivity(), TransactionDetailActivity.class);
                            break;
                        default:
                            Intent tradeRoom = new Intent(view.getContext(), TradeRealtimeActivity.class);
                            tradeRoom.putExtra("userId", notiTransaction.getUsers().get(0).getUserId());
                            startActivity(tradeRoom);
                            break;
                    }
                }

            }
        });
    }

    private void ActionToolbar() {
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.notification_title);
    }

    private void getDataFromTransaction() {

        rmaAPIService.getAllTransactionByUserID(authorization).enqueue(new Callback<List<Transaction>>() {
            @Override
            public void onResponse(Call<List<Transaction>> call, Response<List<Transaction>> response) {

                if (response.isSuccessful()) {
                    List<Transaction> temp = new ArrayList<>();

                    for (int i = 0; i < response.body().size(); i++){
                        Transaction transaction = response.body().get(i);
                        if (transaction.getStatus().equals(AppStatus.TRANSACTION_DONE) || (transaction.getStatus().equals(TRANSACTION_DONATED) && transaction.getSenderId() != userId)){
                            temp.add(transaction);
                        }
                    }

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
    }

    private void getDataFromRelationship() {
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
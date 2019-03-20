package com.project.capstone.exchangesystem.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.*;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;
import com.project.capstone.exchangesystem.utils.RmaAPIUtils;
import com.project.capstone.exchangesystem.adapter.ItemAdapter;
import com.project.capstone.exchangesystem.R;
import com.project.capstone.exchangesystem.model.Item;
import com.project.capstone.exchangesystem.model.Transaction;
import com.project.capstone.exchangesystem.model.TransactionDetail;
import com.project.capstone.exchangesystem.model.TransactionRequestWrapper;
import com.project.capstone.exchangesystem.remote.RmaAPIService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class TradeActivity extends AppCompatActivity {

    RecyclerView recyclerviewMe, recyclerviewYou;
    Button addMe, addYou;
    int idMe, idYou;
    ArrayList<Item> choosedMe, choosedYou;
    ItemAdapter itemMeAdapter, itemYouAdapter;
    ArrayList<String> itemIdsMe, itemIdsYou;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trade);

        recyclerviewMe = findViewById(R.id.recyclerviewMe);
        recyclerviewYou = findViewById(R.id.recyclerviewYou);
        choosedMe = new ArrayList<>();
        choosedYou = new ArrayList<>();
        itemIdsMe = new ArrayList<>();
        itemIdsYou = new ArrayList<>();


        itemMeAdapter = new ItemAdapter(getApplicationContext(), choosedMe, new ItemAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Item item) {

            }
        });

        recyclerviewMe.setHasFixedSize(true);
        recyclerviewMe.setLayoutManager(new GridLayoutManager(getApplicationContext(), 2));
        recyclerviewMe.setAdapter(itemMeAdapter);

        itemYouAdapter = new ItemAdapter(getApplicationContext(), choosedYou, new ItemAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Item item) {

            }
        });

        recyclerviewYou.setHasFixedSize(true);
        recyclerviewYou.setLayoutManager(new GridLayoutManager(getApplicationContext(), 2));
        recyclerviewYou.setAdapter(itemYouAdapter);


        SharedPreferences sharedPreferences = getSharedPreferences("localData", MODE_PRIVATE);
        final int idMe = sharedPreferences.getInt("userId", 0);
        System.out.println("Test My Id " + idMe);

        Intent intent = this.getIntent();
        Item temp = (Item) intent.getSerializableExtra("descriptionItem");
        final int idYou = temp.getUser().getId();
        System.out.println("Test Your Id " + idYou);


        choosedYou.add(temp);
        itemYouAdapter.notifyDataSetChanged();
        itemIdsYou.add(String.valueOf(temp.getId()));
        saveArrayList(itemIdsYou, "itemYouIdList");


        addMe = findViewById(R.id.addMe);
        addYou = findViewById(R.id.addYou);

        addMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> itemList = getArrayList("itemMeIdList");
                Intent intent = new Intent(TradeActivity.this, ChooseItemActivity.class);
                intent.putExtra("id", idMe);
                intent.putStringArrayListExtra("itemMeIdList", itemList);
                startActivityForResult(intent, 2);
            }
        });

        addYou.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> itemList = getArrayList("itemYouIdList");
                Intent intent = new Intent(TradeActivity.this, ChooseItemActivity.class);
                intent.putExtra("id", idYou);
                intent.putStringArrayListExtra("itemYouIdList", itemList);
                startActivityForResult(intent, 2);
            }
        });


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        SharedPreferences sharedPreferences = getSharedPreferences("localData", MODE_PRIVATE);
        final int idMe = sharedPreferences.getInt("userId", 0);
        System.out.println("Test My Id " + idMe);

        Intent intent = this.getIntent();
        Item temp = (Item) intent.getSerializableExtra("descriptionItem");
        final int idYou = temp.getUser().getId();
        System.out.println("Test Your Id " + idYou);
        // check if the request code is same as what is passed  here it is 2
        if (requestCode == 2 && data.getExtras() != null) {
            try {
                Bundle bundle = data.getExtras();
                ArrayList<Item> idChoose = (ArrayList<Item>) bundle.getSerializable("LISTCHOOSE");
                int id = data.getIntExtra("tempID", 0);
                System.out.println("Test tempID " + id);
                if (id == idMe) {
                    System.out.println("test ID ME " + idMe);
                    itemMeAdapter.setfilter(idChoose);
                } else if (id == idYou) {
                    System.out.println("test ID YOU " + idYou);
                    itemYouAdapter.setfilter(idChoose);
                }
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }


    }

    public void sendTradeRequest(View view) {
        RmaAPIService rmaAPIService = RmaAPIUtils.getAPIService();

        SharedPreferences sharedPreferences = getSharedPreferences("localData", MODE_PRIVATE);
        int idMe = sharedPreferences.getInt("userId", 0);
        System.out.println("test id Me " + idMe);
        String authorization = sharedPreferences.getString("authorization", null);
        System.out.println(authorization);


        Item item = (Item) getIntent().getSerializableExtra("descriptionItem");
        int idYou = item.getUser().getId();
        System.out.println("test id You " + idYou);

        GridView gridViewMyInventory = (GridView) findViewById(R.id.gridViewMyInventory);

        List<TransactionDetail> transactionDetailList = new ArrayList<>();

        ArrayList<Item> idItemsMe = itemMeAdapter.getfilter();
        ArrayList<Item> idItemsYou = itemYouAdapter.getfilter();


        for (int i = 0; i < idItemsMe.size(); i++) {
            TransactionDetail temp = new TransactionDetail();
            temp.setItemId(idItemsMe.get(i).getId());
            temp.setUserId(idMe);
            transactionDetailList.add(temp);

        }

        for (int i = 0; i < idItemsYou.size(); i++) {
            TransactionDetail temp = new TransactionDetail();
            temp.setItemId(idItemsYou.get(i).getId());
            temp.setUserId(idYou);
            transactionDetailList.add(temp);
        }


        Transaction transaction = new Transaction();

        transaction.setReceiverId(idYou);
        transaction.setStatus("1");
//        transaction.setDonationPostId(-1);

        final TransactionRequestWrapper transactionRequestWrapper = new TransactionRequestWrapper(transaction, transactionDetailList);


        rmaAPIService.sendTradeRequest(authorization, transactionRequestWrapper).enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {

                System.out.println("test response " + response.isSuccessful());
                System.out.println(response.body());
                if (response.isSuccessful()) {
                    try {
                        LinkedTreeMap<String, Object> responeBody = (LinkedTreeMap<String, Object>) response.body();
                        if (responeBody.containsKey("message")) {
                            String mess = (String) responeBody.get("message");
                            if (mess.equals("Sended")) {
                                Toast.makeText(getApplicationContext(), "Send Trade Request Successfully", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(getApplicationContext(), TransactionDetailActivity.class);
                                intent.putExtra("transactionDetail", transactionRequestWrapper);
                                startActivity(intent);
                            }
                        }
                    } catch (Exception ex) {
                        Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
                    }

                }

            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                System.out.println("fail rooif");
                System.out.println(t.getMessage());
                System.out.println(t.getCause());
            }
        });
    }

    public ArrayList<String> getArrayList(String key) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        Gson gson = new Gson();
        String json = prefs.getString(key, null);
        Type type = new TypeToken<ArrayList<String>>() {
        }.getType();
        ArrayList<String> strings = gson.fromJson(json, type);
        return strings;
    }

    public void saveArrayList(ArrayList<String> list, String key) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(list);
        editor.putString(key, json);
        editor.apply();     // This line is IMPORTANT !!!
    }
}

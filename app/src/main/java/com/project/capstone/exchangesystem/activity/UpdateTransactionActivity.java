package com.project.capstone.exchangesystem.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.project.capstone.exchangesystem.R;
import com.project.capstone.exchangesystem.adapter.ItemAdapter;
import com.project.capstone.exchangesystem.model.Item;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class UpdateTransactionActivity extends AppCompatActivity {
    RecyclerView recyclerviewMe, recyclerviewYou;
    ArrayList<Item> choosedMe, choosedYou;
    ItemAdapter itemMeAdapter, itemYouAdapter;
    Button addMe, addYou;
    int idMe, idYou;
    ArrayList<String> itemIdsMe, itemIdsYou;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trade);
    }

    private void direct() {
        recyclerviewMe = findViewById(R.id.recyclerviewMe);
        recyclerviewYou = findViewById(R.id.recyclerviewYou);

        choosedMe = (ArrayList<Item>) getIntent().getSerializableExtra("itemsMeUpdate");
        for (int i = 0; i < choosedMe.size(); i++) {
            itemIdsMe.add(String.valueOf(choosedMe.get(i).getId()));
        }
//        saveArrayList(itemIdsMe, "itemMeIdList");


        choosedYou = (ArrayList<Item>) getIntent().getSerializableExtra("itemsYouUpdate");
        for (int i = 0; i < choosedYou.size(); i++) {
            itemIdsYou.add(String.valueOf(choosedYou.get(i).getId()));
        }
        saveArrayList(itemIdsYou, "itemYouIdList");

        idMe = getIntent().getIntExtra("idMeUpdate", 0);
        idYou = getIntent().getIntExtra("idYouUpdate", 0);


        itemMeAdapter = new ItemAdapter(getApplicationContext(), choosedMe, new ItemAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Item item) {

            }
        });

        recyclerviewMe.setHasFixedSize(true);
        recyclerviewMe.setLayoutManager(new GridLayoutManager(getApplicationContext(), 2));
        recyclerviewMe.setAdapter(itemMeAdapter);
        itemMeAdapter.notifyDataSetChanged();

        itemYouAdapter = new ItemAdapter(getApplicationContext(), choosedYou, new ItemAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Item item) {

            }
        });

        recyclerviewYou.setHasFixedSize(true);
        recyclerviewYou.setLayoutManager(new GridLayoutManager(getApplicationContext(), 2));
        recyclerviewYou.setAdapter(itemYouAdapter);
        itemYouAdapter.notifyDataSetChanged();


        addMe = findViewById(R.id.addMe);
        addYou = findViewById(R.id.addYou);

        addMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                ArrayList<String> itemList = getArrayList("itemMeIdList");
                Intent intent = new Intent(UpdateTransactionActivity.this, ChooseItemActivity.class);
                intent.putExtra("id", idMe);
                intent.putStringArrayListExtra("itemMeIdList", itemIdsMe);
                startActivityForResult(intent, 3);
            }
        });

        addYou.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UpdateTransactionActivity.this, ChooseItemActivity.class);
                intent.putExtra("id", idYou);
                intent.putStringArrayListExtra("itemYouIdList", itemIdsYou);
                startActivityForResult(intent, 3);
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
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

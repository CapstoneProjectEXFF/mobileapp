package com.project.capstone.exchangesystem.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;
import com.project.capstone.exchangesystem.R;
import com.project.capstone.exchangesystem.adapter.ItemAdapter;
import com.project.capstone.exchangesystem.model.Item;
import com.project.capstone.exchangesystem.remote.RmaAPIService;
import com.project.capstone.exchangesystem.utils.RmaAPIUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.ArrayList;
import java.util.List;

public class OwnInventory extends AppCompatActivity {
    Toolbar toolbar;
    RecyclerView recyclerView;
    ItemAdapter itemAdapter;
    ArrayList<Item> itemArrayList;
    private static final int DELETE_CODE = 1;
    private static final int UPDATE_CODE = 2;
    private boolean reloadNeed = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_own_inventory);

        direct();
        ActionToolbar();
        GetData();

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (this.reloadNeed)
            GetData();
        this.reloadNeed = false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == DELETE_CODE) { // Ah! We are back from EditActivity, did we make any changes?
            if (resultCode == Activity.RESULT_OK) {
                // Yes we did! Let's allow onResume() to reload the data
                this.reloadNeed = true;
            }
        } else if (requestCode == UPDATE_CODE) {
            this.reloadNeed = true;
        }
    }

    private void direct() {
        toolbar = findViewById(R.id.inventoryToolbar);
        recyclerView = findViewById(R.id.inventoryRecyclerView);
        itemArrayList = new ArrayList<>();
        itemAdapter = new ItemAdapter(getApplicationContext(), itemArrayList, new ItemAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Item item) {
                Toast.makeText(getApplicationContext(), item.getDescription(), Toast.LENGTH_LONG).show();
//                Intent intent = new Intent(getApplicationContext(), UpdateItemActivity.class);
//                intent.putExtra("itemId", item.getId());
//                startActivity(intent);

                Intent intent = new Intent(getApplicationContext(), DescriptionItemActivity.class);
                intent.putExtra("descriptionItem", item);
                startActivityForResult(intent, DELETE_CODE);
            }
        });
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(itemAdapter);

    }

//    private void reGetData() {
//        itemArrayList.set
//    }

    private void GetData() {
        SharedPreferences sharedPreferences = getSharedPreferences("localData", MODE_PRIVATE);
        String authorization = sharedPreferences.getString("authorization", null);

        if (authorization != null) {
            RmaAPIService rmaAPIService = RmaAPIUtils.getAPIService();
            rmaAPIService.getMyItems(authorization).enqueue(new Callback<List<Item>>() {
                @Override
                public void onResponse(Call<List<Item>> call, Response<List<Item>> response) {
                    if (response.isSuccessful()) {
                        List<Item> result = new ArrayList<>();
                        result = response.body();
//                        for (int i = 0; i < result.size(); i++) {
//                            itemArrayList.add(result.get(i));
//                            itemAdapter.notifyDataSetChanged();
//                        }
                        itemAdapter.setfilter((ArrayList<Item>) result);
                    }
                }

                @Override
                public void onFailure(Call<List<Item>> call, Throwable t) {
                    System.out.println(t.getMessage());
                }
            });
        } else {
            System.out.println("Fail Test Authorization");
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

    public void toAddItemActivity(View view) {
        Intent iTimKiem = new Intent(this, CreateItemActivity.class);
        startActivityForResult(iTimKiem, UPDATE_CODE);
//        startActivity(iTimKiem);
    }


}

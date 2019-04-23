package com.project.capstone.exchangesystem.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;
import com.project.capstone.exchangesystem.R;
import com.project.capstone.exchangesystem.adapter.ItemAdapter;
import com.project.capstone.exchangesystem.model.Item;
import com.project.capstone.exchangesystem.model.User;
import com.project.capstone.exchangesystem.remote.RmaAPIService;
import com.project.capstone.exchangesystem.utils.RmaAPIUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.ArrayList;
import java.util.List;

public class FriendInventoryActivity extends AppCompatActivity {
    RecyclerView inventoryRecyclerView;
    ArrayList<Item> itemArrayList;
    ItemAdapter itemAdapter;
    Toolbar inventoryFriendToolbar;
    User friendDetail;
    String authorization;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_inventory);
        direct();
        getInventoryFromFriend();
        ActionToolbar();
    }

    private void direct() {
        Intent intent = this.getIntent();
        friendDetail = (User) intent.getSerializableExtra("friendDetail");
        inventoryFriendToolbar = findViewById(R.id.inventoryFriendToolbar);
        inventoryRecyclerView = findViewById(R.id.inventoryRecyclerView);


        itemArrayList = new ArrayList<>();
        itemAdapter = new ItemAdapter(getApplicationContext(), itemArrayList, new ItemAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Item item) {
                Toast.makeText(getApplicationContext(), item.getDescription(), Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getApplicationContext(), DescriptionItemActivity.class);
                intent.putExtra("descriptionItem", item);
                startActivity(intent);
            }
        });
        inventoryRecyclerView.setHasFixedSize(true);
        inventoryRecyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(), 2));
        inventoryRecyclerView.setAdapter(itemAdapter);

        sharedPreferences = getSharedPreferences("localData", MODE_PRIVATE);
        authorization = sharedPreferences.getString("authorization", null);
    }

    private void getInventoryFromFriend() {
        RmaAPIService rmaAPIService = RmaAPIUtils.getAPIService();
        rmaAPIService.getItemsByUserIdWithPrivacy(authorization, friendDetail.getId()).enqueue(new Callback<List<Item>>() {
            @Override
            public void onResponse(Call<List<Item>> call, Response<List<Item>> response) {
                if (response.isSuccessful()) {
                    List<Item> result = response.body();
                    for (int i = 0; i < result.size(); i++) {
                        itemArrayList.add(result.get(i));
                        itemAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Item>> call, Throwable t) {

            }
        });

    }

    private void ActionToolbar() {
        setSupportActionBar(inventoryFriendToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        inventoryFriendToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        inventoryFriendToolbar.setTitle("Kho Của " + friendDetail.getFullName());
    }
}

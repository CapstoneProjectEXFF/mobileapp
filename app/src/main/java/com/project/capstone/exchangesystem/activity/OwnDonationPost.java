package com.project.capstone.exchangesystem.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import com.project.capstone.exchangesystem.R;
import com.project.capstone.exchangesystem.adapter.MainCharityPostAdapter;
import com.project.capstone.exchangesystem.fragment.MainCharityPostFragment;
import com.project.capstone.exchangesystem.model.DonationPost;
import com.project.capstone.exchangesystem.model.User;
import com.project.capstone.exchangesystem.remote.RmaAPIService;
import com.project.capstone.exchangesystem.utils.RmaAPIUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.ArrayList;
import java.util.List;

public class OwnDonationPost extends AppCompatActivity {

    public int idUser;
    Toolbar toolbar;
    ListView listView;
    TextView btnAdd;
    MainCharityPostAdapter mainCharityPostAdapter;
    ArrayList<DonationPost> donationPosts;
    View footerView;
    boolean isLoading;
    SharedPreferences sharedPreferences;
    String authorization;
    boolean limitData;
    int page;
    MainCharityPostFragment.mHandler mHandler;
    RmaAPIService rmaAPIService;
    User userDetail;
    TextView btnAddCharityPost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_main_charity_post);
        direct();
        getData();
        actionToolbar();
    }

    private void direct() {
        rmaAPIService = RmaAPIUtils.getAPIService();
        sharedPreferences = getSharedPreferences("localData", MODE_PRIVATE);
        authorization = sharedPreferences.getString("authorization", null);
        toolbar = findViewById(R.id.ownDonationToolbar);
        btnAddCharityPost = findViewById(R.id.btnAddCharityPost);
        btnAddCharityPost.setVisibility(View.GONE);
        Intent intent = this.getIntent();
        if (intent.hasExtra("userDetail")) {
            userDetail = (User) intent.getSerializableExtra("userDetail");
            idUser = userDetail.getId();
        } else {
            idUser = sharedPreferences.getInt("userId", 0);
        }


        listView = findViewById(R.id.charityPostListView);
        donationPosts = new ArrayList<>();
        mainCharityPostAdapter = new MainCharityPostAdapter(getApplicationContext(), donationPosts);
        listView.setAdapter(mainCharityPostAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), DescriptionDonationPostActivity.class);
                intent.putExtra("descriptionDonationPost", donationPosts.get(position));
                startActivity(intent);
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
        rmaAPIService.getDonationPostByUserId(idUser).enqueue(new Callback<List<DonationPost>>() {
            @Override
            public void onResponse(Call<List<DonationPost>> call, Response<List<DonationPost>> response) {
                if (response.isSuccessful()) {
                    List<DonationPost> result = response.body();
                    donationPosts.addAll(result);
                    mainCharityPostAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<List<DonationPost>> call, Throwable t) {

            }
        });
    }
}

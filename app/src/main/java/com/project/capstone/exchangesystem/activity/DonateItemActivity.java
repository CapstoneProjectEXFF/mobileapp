package com.project.capstone.exchangesystem.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.project.capstone.exchangesystem.R;
import com.project.capstone.exchangesystem.adapter.ItemAdapter;
import com.project.capstone.exchangesystem.fragment.ImageOptionDialog;
import com.project.capstone.exchangesystem.model.DonationPost;
import com.project.capstone.exchangesystem.model.Item;
import com.project.capstone.exchangesystem.model.Transaction;
import com.project.capstone.exchangesystem.model.TransactionDetail;
import com.project.capstone.exchangesystem.model.TransactionRequestWrapper;
import com.project.capstone.exchangesystem.remote.RmaAPIService;
import com.project.capstone.exchangesystem.utils.RmaAPIUtils;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.project.capstone.exchangesystem.constants.AppStatus.DELETE_IMAGE_OPTION;
import static com.project.capstone.exchangesystem.constants.AppStatus.DONATE_ACTIVITY_IMAGE_FLAG;

public class DonateItemActivity extends AppCompatActivity implements ImageOptionDialog.ImageOptionListener {

    TextView txtReceiverName;
    ArrayList<Item> itemList;

    DonationPost donationPost;
    SharedPreferences sharedPreferences;
    String authorization;
    RmaAPIService rmaAPIService;
    Integer userId;

    Context context;

    //new view
    Toolbar toolbar;
    RecyclerView rvSelectedImages;
    ItemAdapter itemAdapter;
    ImageButton btnAddImages;
    Item tmpItem;
    TextView txtNoti;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donate_item);
        itemList = new ArrayList<>();
        context = this;
        getComponent();
        setToolbar();

        itemAdapter = new ItemAdapter(getApplicationContext(), itemList, new ItemAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Item item) {
                tmpItem = item;
                ImageOptionDialog optionDialog = new ImageOptionDialog();
                optionDialog.setActivityFlag(DONATE_ACTIVITY_IMAGE_FLAG);
                optionDialog.show(getSupportFragmentManager(), "optionDialog");
            }
        });
        rvSelectedImages.setHasFixedSize(true);
        rvSelectedImages.setLayoutManager(new GridLayoutManager(this, 2));
        rvSelectedImages.setAdapter(itemAdapter);

        btnAddImages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> selectedItemIdsStr = new ArrayList<>();
                for (int i = 0; i < itemList.size(); i++) {
                   selectedItemIdsStr.add(String.valueOf(itemList.get(i).getId()));
                }
                Intent intent = new Intent(getApplicationContext(), ChooseItemActivity.class);
                intent.putExtra("id", userId);
                intent.putExtra("itemMeIdList", selectedItemIdsStr);
                startActivityForResult(intent, 2);
            }
        });
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        getComponent();

        // check if the request code is same as what is passed  here it is 2
        if (requestCode == 2) {
            Bundle bundle = data.getExtras();
            itemList = (ArrayList<Item>) bundle.getSerializable("LISTCHOOSE");
            itemAdapter.setfilter(itemList);
            setNoti();
        }
    }

    private void createDonateTransaction() {
        List<TransactionDetail> transactionDetailList = new ArrayList<>();
        for (int i = 0; i < itemList.size(); i++) { //create transaction detail
                TransactionDetail transactionDetail = new TransactionDetail();
                transactionDetail.setUserId(itemList.get(i).getUser().getId());
                transactionDetail.setItemId(itemList.get(i).getId());
                transactionDetailList.add(transactionDetail);
        }
        Transaction transaction = new Transaction();
        transaction.setSenderId(userId);
        transaction.setReceiverId(donationPost.getUser().getId());
        transaction.setDonationPostId(donationPost.getId());
        TransactionRequestWrapper transactionRequestWrapper = new TransactionRequestWrapper(transaction, transactionDetailList);

        if (authorization != null) {
            rmaAPIService.sendTradeRequest(authorization, transactionRequestWrapper).enqueue(new Callback<Object>() {
                @Override
                public void onResponse(Call<Object> call, Response<Object> response) {
                    Intent intent = new Intent(getApplicationContext(), CreateSuccessActivity.class);
                    intent.putExtra("donationPost", donationPost);
                    startActivity(intent);
                }

                @Override
                public void onFailure(Call<Object> call, Throwable t) {
                    Toast.makeText(getApplicationContext(), "Xảy ra lỗi! Vui lòng thử lại!", Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    private void getComponent() {
        donationPost = (DonationPost) getIntent().getSerializableExtra("donationPost");
        rmaAPIService = RmaAPIUtils.getAPIService();
        txtReceiverName = findViewById(R.id.txtReceiverName);
        txtReceiverName.setText(donationPost.getUser().getFullName());
        toolbar = findViewById(R.id.tbToolbar);
        rvSelectedImages = findViewById(R.id.rvSelectedImages);
        btnAddImages = findViewById(R.id.btnAddImages);
        txtNoti = findViewById(R.id.txtNoti);
        setNoti();

        sharedPreferences = getSharedPreferences("localData", MODE_PRIVATE);
        authorization = sharedPreferences.getString("authorization", null);
        userId = sharedPreferences.getInt("userId", 0);
    }

    private void setNoti() {
        if (itemList.size() == 0){
            txtNoti.setVisibility(View.VISIBLE);
        } else {
            txtNoti.setVisibility(View.GONE);
        }
    }

    private void setToolbar() {
        //TODO SET DONATION POST TITLE
        toolbar.setTitle("Quyên góp");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_confirm, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.btnConfirm) {
            if (itemList.size() == 0){
                Toast.makeText(getApplicationContext(), "Vui lòng chọn đồ dùng", Toast.LENGTH_LONG).show();
            } else {
                createDonateTransaction();
            }
        }
        return true;
    }

    //when click on item of option dialog
    @Override
    public void onButtonClicked(int choice) {
        if (choice == DELETE_IMAGE_OPTION){
            itemList.remove(tmpItem);
            itemAdapter.notifyDataSetChanged();
            itemAdapter.setfilter(itemList);
            setNoti();
        }
    }
}

package com.project.capstone.exchangesystem.activity;

import android.app.ActionBar;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.project.capstone.exchangesystem.R;
import com.project.capstone.exchangesystem.adapter.ItemAdapter;
import com.project.capstone.exchangesystem.model.DonationPost;
import com.project.capstone.exchangesystem.model.Item;
import com.project.capstone.exchangesystem.model.Transaction;
import com.project.capstone.exchangesystem.model.TransactionDetail;
import com.project.capstone.exchangesystem.model.TransactionRequestWrapper;
import com.project.capstone.exchangesystem.remote.RmaAPIService;
import com.project.capstone.exchangesystem.utils.RmaAPIUtils;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.project.capstone.exchangesystem.constants.AppStatus.DONATION_FRAGMENT_FLAG;
import static com.project.capstone.exchangesystem.constants.AppStatus.IMAGE_MARGIN_TOP_RIGHT;
import static com.project.capstone.exchangesystem.constants.AppStatus.IMAGE_SIZE;
import static com.project.capstone.exchangesystem.constants.AppStatus.ITEM_ENABLE;

public class DonateItemActivity extends AppCompatActivity {

    private final boolean IMAGE_UNCHECKED = false;
    private final boolean IMAGE_CHECKED = true;

    TextView btnCancel, btnConfirm, txtReceiverName, txtTitle;
//    List<Item> itemList;
    ArrayList<Item> itemList;
    List<Integer> selectedItemIds;
    List<ImageView> itemImages;
    List<Boolean> checkedSelectedImages;
    GridLayout gvItemList;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donate_item);
        context = this;
        getComponent();
//        getItemList();
        setToolbar();

//        btnConfirm.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                createDonateTransaction();
//            }
//        });
//
//        btnCancel.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(getApplicationContext(), DescriptionDonationPostActivity.class);
//                intent.putExtra("descriptionDonationPost", donationPost);
//                startActivity(intent);
//            }
//        });

        //new view
        itemList = new ArrayList<>();
        itemAdapter = new ItemAdapter(getApplicationContext(), itemList, new ItemAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Item item) {

                boolean check = itemList.get(itemList.indexOf(item)).isSeletedFlag();
                if (!check) {
                    itemList.get(itemList.indexOf(item)).setSeletedFlag(IMAGE_CHECKED);

                } else {
                    itemList.get(itemList.indexOf(item)).setSeletedFlag(IMAGE_UNCHECKED);

                }
                Toast.makeText(getApplicationContext(), "" + itemList.get(itemList.indexOf(item)).isSeletedFlag(), Toast.LENGTH_LONG).show();
            }
        });
        rvSelectedImages.setHasFixedSize(true);
        rvSelectedImages.setLayoutManager(new GridLayoutManager(this, 2));
//        rvSelectedImages.setHasFixedSize(true);
        rvSelectedImages.setAdapter(itemAdapter);
//        getItemList();

        btnAddImages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                ArrayList<String> itemList = getArrayList("itemMeIdList");
                Intent intent = new Intent(getApplicationContext(), ChooseItemActivity.class);
                intent.putExtra("id", userId);
                intent.putExtra("selectedItem", itemList);
//                intent.putStringArrayListExtra("itemMeIdList", itemList);
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
            ArrayList<Item> itemIds = (ArrayList<Item>) bundle.getSerializable("LISTCHOOSE");
            itemAdapter.setfilter(itemIds);
        }
    }

    private void createDonateTransaction() {
        List<TransactionDetail> transactionDetailList = new ArrayList<>();
        for (int i = 0; i < itemList.size(); i++) {
//            if (itemList.get(i).isSeletedFlag()) {
                TransactionDetail transactionDetail = new TransactionDetail();
                transactionDetail.setUserId(itemList.get(i).getUser().getId());
                transactionDetail.setItemId(itemList.get(i).getId());
                transactionDetailList.add(transactionDetail);
//            }
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

    private void getItemList() {

        if (authorization != null) {
            rmaAPIService.getItemsByUserId(userId).enqueue(new Callback<List<Item>>() {
                @Override
                public void onResponse(Call<List<Item>> call, Response<List<Item>> response) {
                    if (response.isSuccessful()) {
                        if (response.body() != null) {
//                            itemList = new ArrayList<>();
                            List<Item> tmpItemList = new ArrayList<>();
                            itemImages = new ArrayList<>();
                            checkedSelectedImages = new ArrayList<>();
                            selectedItemIds = new ArrayList<>();
                            for (int i = 0; i < response.body().size(); i++) {
                                if (response.body().get(i).getStatus().equals(ITEM_ENABLE)) {
                                    tmpItemList.add(response.body().get(i));
                                    tmpItemList.get(i).setSeletedFlag(false);
//                                    addItemToGridView(itemList.get(itemList.size() - 1));
                                }
                            }
                            if (!tmpItemList.isEmpty()){
                                itemList.addAll(tmpItemList);
                                itemAdapter.notifyDataSetChanged();
                            }
                        } else {
                            Log.i("Donation", "null");
                        }
                    } else {
                        Log.i("Donation", "cannot load donation post");
                    }
                }

                @Override
                public void onFailure(Call<List<Item>> call, Throwable t) {
                    Log.i("Donation", "failed");
                }
            });
        }
    }

//    private void addItemToGridView(Item item) {
//        gvItemList = findViewById(R.id.gvItemList);
//
//        //create LinearLayout
//        LinearLayout linearLayout = new LinearLayout(context);
//        linearLayout.setOrientation(LinearLayout.VERTICAL);
//
//        gvItemList.addView(linearLayout);
//
//        //create ImageView
//        final ImageView imageView = new ImageView(context);
//
//        Picasso.with(getApplicationContext()).load(item.getImages().get(0).getUrl())
//                .placeholder(R.drawable.ic_no_image)
//                .error(R.drawable.ic_no_image)
//                .into(imageView);
////
//        imageView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                int selectedPosition = itemImages.indexOf(imageView);
//                boolean check = itemList.get(selectedPosition).isSeletedFlag();
//                if (!check) {
//                    itemList.get(selectedPosition).setSeletedFlag(IMAGE_CHECKED);
//                    imageView.setColorFilter(Color.rgb(142, 30, 32), PorterDuff.Mode.MULTIPLY);
//                } else {
//                    itemList.get(selectedPosition).setSeletedFlag(IMAGE_UNCHECKED);
//                    imageView.clearColorFilter();
//                }
//            }
//        });
//        checkedSelectedImages.add(IMAGE_UNCHECKED);
//        itemImages.add(imageView);
//
//        //create TextView itemName
//        TextView txtItemName = new TextView(context);
//
//        linearLayout.addView(imageView);
//        linearLayout.addView(txtItemName);
//        txtItemName.setText(item.getName());
//
//        //setting LinearLayout
//        ViewGroup.LayoutParams layoutParams = linearLayout.getLayoutParams();
//        layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
//        linearLayout.setLayoutParams(layoutParams);
//
//        //image
//        ViewGroup.LayoutParams ivLayoutParams;
//        ivLayoutParams = imageView.getLayoutParams();
//        ivLayoutParams.height = IMAGE_SIZE;
//        ivLayoutParams.width = IMAGE_SIZE;
//        imageView.setLayoutParams(ivLayoutParams);
//
//        //text
//        txtItemName.setGravity(Gravity.CENTER);
//        txtItemName.setTextColor(Color.BLACK);
//    }

    private void getComponent() {
//        txtTitle = findViewById(R.id.txtTitle);
//        txtTitle.setText("Đóng góp đồ dùng");
//        btnConfirm = findViewById(R.);
//        btnConfirm.setText("Xác nhận");
//        btnCancel = findViewById(R.id.btnCancel);
        donationPost = (DonationPost) getIntent().getSerializableExtra("donationPost");
        rmaAPIService = RmaAPIUtils.getAPIService();
        txtReceiverName = findViewById(R.id.txtReceiverName);
        txtReceiverName.setText(donationPost.getUser().getFullName());
        toolbar = findViewById(R.id.tbToolbar);
        rvSelectedImages = findViewById(R.id.rvSelectedImages);
        btnAddImages = findViewById(R.id.btnAddImages);
        sharedPreferences = getSharedPreferences("localData", MODE_PRIVATE);
        authorization = sharedPreferences.getString("authorization", null);
        userId = sharedPreferences.getInt("userId", 0);
    }

    private void setToolbar() {
        toolbar.setTitle("Quyên góp");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setDisplayShowHomeEnabled(true);

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
}

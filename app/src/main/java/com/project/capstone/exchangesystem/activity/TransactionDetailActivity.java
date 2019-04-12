package com.project.capstone.exchangesystem.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.project.capstone.exchangesystem.R;
import com.project.capstone.exchangesystem.adapter.SelectedItemAdapter;
import com.project.capstone.exchangesystem.utils.RmaAPIUtils;
import com.project.capstone.exchangesystem.adapter.ItemAdapter;
import com.project.capstone.exchangesystem.model.Item;
import com.project.capstone.exchangesystem.model.Transaction;
import com.project.capstone.exchangesystem.model.TransactionDetail;
import com.project.capstone.exchangesystem.model.TransactionRequestWrapper;
import com.project.capstone.exchangesystem.remote.RmaAPIService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;
import static com.facebook.FacebookSdk.getApplicationContext;

public class TransactionDetailActivity extends AppCompatActivity {

    RecyclerView rvYourItems, rvMyItems;
    TextView txtReceiverName, txtReceiverPhone, txtReceiverAddress;
    ImageView ivQRCode;
    ImageButton btnMaps;
    ConstraintLayout myItemLayout;
    Toolbar toolbar;

    ArrayList<Item> myItems, yourItems;
    SelectedItemAdapter myItemAdapter, yourItemAdapter;
    Transaction transaction;
    List<TransactionDetail> transDetailList;
    TransactionRequestWrapper dataInf;

    SharedPreferences sharedPreferences;
    RmaAPIService rmaAPIService;
    String authorization, qrCode;
    int myUserId, transactionId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_detail);
        direct();
        setItemAdapter();
        getData();
    }

    private void setItemAdapter() {
        myItems = new ArrayList<>();
        myItemAdapter = new SelectedItemAdapter(this, myItems, new SelectedItemAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Item item) {
            }
        });

        rvMyItems.setHasFixedSize(true);
        rvMyItems.setLayoutManager(new GridLayoutManager(this, 1));
        rvMyItems.setAdapter(myItemAdapter);

        yourItems = new ArrayList<>();
        yourItemAdapter = new SelectedItemAdapter(this, yourItems, new SelectedItemAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Item item) {
            }
        });

        rvYourItems.setHasFixedSize(true);
        rvYourItems.setLayoutManager(new GridLayoutManager(this, 1));
        rvYourItems.setAdapter(yourItemAdapter);
    }


    private void direct() {
        rvMyItems = findViewById(R.id.rvMyItems);
        rvYourItems = findViewById(R.id.rvYourItems);
        txtReceiverName = findViewById(R.id.txtReceiverName);
        txtReceiverPhone = findViewById(R.id.txtReceiverPhone);
        txtReceiverAddress = findViewById(R.id.txtReceiverAddress);
        ivQRCode = findViewById(R.id.ivQRCode);
        btnMaps = findViewById(R.id.btnMaps);
        myItemLayout = findViewById(R.id.myItemLayout);
        toolbar = findViewById(R.id.tbToolbar);

        btnMaps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + txtReceiverAddress.getText().toString());
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);
            }
        });
    }

    private void getData() {
        transactionId = (int) getIntent().getSerializableExtra("transactionId");
//        qrCode = (String) getIntent().getSerializableExtra("qrCode");
//        transactionId = 27;
        qrCode = "nhi dễ thương";
        sharedPreferences = getSharedPreferences("localData", MODE_PRIVATE);
        authorization = sharedPreferences.getString("authorization", null);
        myUserId = sharedPreferences.getInt("userId", 0);

        rmaAPIService = RmaAPIUtils.getAPIService();

        if (authorization != null){
            rmaAPIService.getTransactionByTransID(authorization, transactionId).enqueue(new Callback<TransactionRequestWrapper>() {
                @Override
                public void onResponse(Call<TransactionRequestWrapper> call, Response<TransactionRequestWrapper> response) {
                    if (response.body() != null){
                        dataInf = response.body();
                        setUserInf();
                    } else {
                        Log.i("transDetail", "null");
                    }
                }

                @Override
                public void onFailure(Call<TransactionRequestWrapper> call, Throwable t) {

                }
            });
        }
    }

    private void setUserInf() {
        if (myUserId == dataInf.getTransaction().getReceiverId()){
            txtReceiverName.setText(dataInf.getTransaction().getSender().getFullName());
            txtReceiverPhone.setText(dataInf.getTransaction().getSender().getPhone());
        } else if (myUserId != dataInf.getTransaction().getReceiverId()){
            txtReceiverName.setText(dataInf.getTransaction().getReceiver().getFullName());
            txtReceiverPhone.setText(dataInf.getTransaction().getReceiver().getPhone());
        }

        setToolbar();

        transDetailList = dataInf.getDetails();

        ArrayList<Item> tmpMyItems = new ArrayList<>();
        ArrayList<Item> tmpYourItems = new ArrayList<>();

        for (int i = 0; i < transDetailList.size(); i++){
            Item tmpItem = transDetailList.get(i).getItem();
            if (tmpItem.getUser().getId() == myUserId){
                tmpMyItems.add(tmpItem);
            } else {
                tmpYourItems.add(tmpItem);
            }
        }

        if (tmpMyItems.size() != 0){
            myItemAdapter.setfilter(tmpMyItems);
        } else {
            myItemLayout.setVisibility(View.GONE);
        }

        if (tmpYourItems.size() != 0){
            txtReceiverAddress.setText(tmpYourItems.get(0).getAddress());
            yourItemAdapter.setfilter(tmpYourItems);
            createQRCode(qrCode);
        } else {
            txtReceiverAddress.setVisibility(View.GONE);
            btnMaps.setVisibility(View.GONE);
            ivQRCode.setVisibility(View.GONE);
            rvYourItems.setVisibility(View.GONE);
        }
    }

    private void createQRCode(String qrCode){
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        try {
            BitMatrix bitMatrix = qrCodeWriter.encode(qrCode, BarcodeFormat.QR_CODE, 250, 250);
            int width = bitMatrix.getWidth();
            int height = bitMatrix.getHeight();
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
            for (int i = 0; i < width; i++) {
                for (int j = 0; j < height; j++) {
                    bitmap.setPixel(i, j, bitMatrix.get(i, j) ? Color.BLACK : Color.WHITE);
                }
            }

            ivQRCode.setImageBitmap(bitmap);

        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

    private void setToolbar() {
        toolbar.setTitle(txtReceiverName.getText().toString());
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
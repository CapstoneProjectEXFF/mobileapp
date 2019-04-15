package com.project.capstone.exchangesystem.activity;

import android.app.Dialog;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.project.capstone.exchangesystem.R;
import com.project.capstone.exchangesystem.adapter.SelectedItemAdapter;
import com.project.capstone.exchangesystem.model.Rate;
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
    Button btnRating;
    Dialog dialog;

    ArrayList<Item> myItems, yourItems;
    SelectedItemAdapter myItemAdapter, yourItemAdapter;
    List<TransactionDetail> transDetailList;
    TransactionRequestWrapper dataInf;

    SharedPreferences sharedPreferences;
    RmaAPIService rmaAPIService;
    String authorization, qrCode;
    int myUserId, transactionId, rateStar = 0, yourUserId;

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

        btnRating = findViewById(R.id.btnRating);
        btnRating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRatingDialog();
            }
        });
    }

    private void showRatingDialog() {
        dialog = new Dialog(TransactionDetailActivity.this);
        dialog.setContentView(R.layout.rating_dialog);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        setDialogComponents(dialog);
    }

    private void setDialogComponents(final Dialog dialog) {
        final ImageButton btnStar1, btnStar2, btnStar3, btnStar4, btnStar5, btnSelectedStar1, btnSelectedStar2, btnSelectedStar3, btnSelectedStar4, btnSelectedStar5;
        final Button btnSend, btnClose;
        final EditText edtContent;

        btnClose = dialog.findViewById(R.id.btnClose);
        btnStar1 = dialog.findViewById(R.id.btnStar1);
        btnStar2 = dialog.findViewById(R.id.btnStar2);
        btnStar3 = dialog.findViewById(R.id.btnStar3);
        btnStar4 = dialog.findViewById(R.id.btnStar4);
        btnStar5 = dialog.findViewById(R.id.btnStar5);
        btnSelectedStar1 = dialog.findViewById(R.id.btnSelectedStar1);
        btnSelectedStar2 = dialog.findViewById(R.id.btnSelectedStar2);
        btnSelectedStar3 = dialog.findViewById(R.id.btnSelectedStar3);
        btnSelectedStar4 = dialog.findViewById(R.id.btnSelectedStar4);
        btnSelectedStar5 = dialog.findViewById(R.id.btnSelectedStar5);
        btnSend = dialog.findViewById(R.id.btnSend);
        edtContent = dialog.findViewById(R.id.edtContent);

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rateStar = 0;
                btnSend.setEnabled(false);
                dialog.dismiss();
            }
        });

        btnStar1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rateStar = 1;
                btnStar1.setVisibility(View.GONE);
                btnSelectedStar1.setVisibility(View.VISIBLE);

                btnSend.setEnabled(true);
            }
        });

        btnStar2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rateStar = 2;
                btnStar2.setVisibility(View.GONE);
                btnSelectedStar2.setVisibility(View.VISIBLE);

                btnStar1.setVisibility(View.GONE);
                btnSelectedStar1.setVisibility(View.VISIBLE);
                btnSend.setEnabled(true);
            }
        });

        btnStar3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rateStar = 3;
                btnStar3.setVisibility(View.GONE);
                btnSelectedStar3.setVisibility(View.VISIBLE);

                btnStar1.setVisibility(View.GONE);
                btnSelectedStar1.setVisibility(View.VISIBLE);
                btnStar2.setVisibility(View.GONE);
                btnSelectedStar2.setVisibility(View.VISIBLE);
                btnSend.setEnabled(true);
            }
        });

        btnStar4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rateStar = 4;
                btnStar4.setVisibility(View.GONE);
                btnSelectedStar4.setVisibility(View.VISIBLE);

                btnStar1.setVisibility(View.GONE);
                btnSelectedStar1.setVisibility(View.VISIBLE);
                btnStar2.setVisibility(View.GONE);
                btnSelectedStar2.setVisibility(View.VISIBLE);
                btnStar3.setVisibility(View.GONE);
                btnSelectedStar3.setVisibility(View.VISIBLE);

                btnSend.setEnabled(true);
            }
        });

        btnStar5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rateStar = 5;
                btnStar5.setVisibility(View.GONE);
                btnSelectedStar5.setVisibility(View.VISIBLE);

                btnStar1.setVisibility(View.GONE);
                btnSelectedStar1.setVisibility(View.VISIBLE);
                btnStar2.setVisibility(View.GONE);
                btnSelectedStar2.setVisibility(View.VISIBLE);
                btnStar3.setVisibility(View.GONE);
                btnSelectedStar3.setVisibility(View.VISIBLE);
                btnStar4.setVisibility(View.GONE);
                btnSelectedStar4.setVisibility(View.VISIBLE);

                btnSend.setEnabled(true);
            }
        });

        btnSelectedStar1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rateStar = 1;
                btnSelectedStar2.setVisibility(View.GONE);
                btnStar2.setVisibility(View.VISIBLE);
                btnSelectedStar3.setVisibility(View.GONE);
                btnStar3.setVisibility(View.VISIBLE);
                btnSelectedStar4.setVisibility(View.GONE);
                btnStar4.setVisibility(View.VISIBLE);
                btnSelectedStar5.setVisibility(View.GONE);
                btnStar5.setVisibility(View.VISIBLE);

                btnSend.setEnabled(true);
            }
        });

        btnSelectedStar2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rateStar = 2;
                btnSelectedStar3.setVisibility(View.GONE);
                btnStar3.setVisibility(View.VISIBLE);
                btnSelectedStar4.setVisibility(View.GONE);
                btnStar4.setVisibility(View.VISIBLE);
                btnSelectedStar5.setVisibility(View.GONE);
                btnStar5.setVisibility(View.VISIBLE);

                btnSend.setEnabled(true);
            }
        });

        btnSelectedStar3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rateStar = 3;
                btnSelectedStar4.setVisibility(View.GONE);
                btnStar4.setVisibility(View.VISIBLE);
                btnSelectedStar5.setVisibility(View.GONE);
                btnStar5.setVisibility(View.VISIBLE);

                btnSend.setEnabled(true);
            }
        });

        btnSelectedStar4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rateStar = 4;
                btnSelectedStar5.setVisibility(View.GONE);
                btnStar5.setVisibility(View.VISIBLE);

                btnSend.setEnabled(true);
            }
        });

        btnSelectedStar5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rateStar = 5;

                btnSend.setEnabled(true);
            }
        });


        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO call api
                if (authorization != null){
                    Rate rate = new Rate();
                    rate.setReceiverId(yourUserId);
                    rate.setContent(edtContent.getText().toString());
                    rate.setRate(rateStar);

                    rmaAPIService.createRating(authorization, rate).enqueue(new Callback<Rate>() {
                        @Override
                        public void onResponse(Call<Rate> call, Response<Rate> response) {
                            if (response.body() != null){
                                Toast.makeText(getApplicationContext(), "Cảm ơn bạn đã đánh giá", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            } else {
                                Log.i("rating", "null");
                            }
                        }

                        @Override
                        public void onFailure(Call<Rate> call, Throwable t) {
                            Log.i("rating", "failed");
                        }
                    });
                }
//                Toast.makeText(getApplicationContext(), "" + rateStar, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getData() {
        transactionId = (int) getIntent().getSerializableExtra("transactionId");
//        qrCode = (String) getIntent().getSerializableExtra("qrCode");
//        transactionId = 27;
//        qrCode = "nhi dễ thương";
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
            yourUserId = dataInf.getTransaction().getSenderId();
            txtReceiverName.setText(dataInf.getTransaction().getSender().getFullName());
            txtReceiverPhone.setText(dataInf.getTransaction().getSender().getPhone());
        } else if (myUserId != dataInf.getTransaction().getReceiverId()){
            yourUserId = dataInf.getTransaction().getReceiverId();
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

        qrCode = dataInf.getTransaction().getQrCode();
        createQRCode(qrCode);

        if (tmpMyItems.size() != 0){
            myItemAdapter.setfilter(tmpMyItems);
        } else {
            myItemLayout.setVisibility(View.GONE);
        }

        if (tmpYourItems.size() != 0){
            txtReceiverAddress.setText(tmpYourItems.get(0).getAddress());
            yourItemAdapter.setfilter(tmpYourItems);
        } else {
            txtReceiverAddress.setVisibility(View.GONE);
            btnMaps.setVisibility(View.GONE);
//            ivQRCode.setVisibility(View.GONE);
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
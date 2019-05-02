package com.project.capstone.exchangesystem.activity;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.project.capstone.exchangesystem.R;
import com.project.capstone.exchangesystem.adapter.DetailItemAdapter;
import com.project.capstone.exchangesystem.fragment.ImageOptionDialog;
import com.project.capstone.exchangesystem.model.FirebaseImg;
import com.project.capstone.exchangesystem.model.Rate;
import com.project.capstone.exchangesystem.utils.RmaAPIUtils;
import com.project.capstone.exchangesystem.model.Item;
import com.project.capstone.exchangesystem.model.Transaction;
import com.project.capstone.exchangesystem.model.TransactionDetail;
import com.project.capstone.exchangesystem.model.TransactionRequestWrapper;
import com.project.capstone.exchangesystem.remote.RmaAPIService;
import com.squareup.picasso.Picasso;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.project.capstone.exchangesystem.constants.AppStatus.ADD_IMAGE_FLAG;
import static com.project.capstone.exchangesystem.constants.AppStatus.CAMERA_REQUEST;
import static com.project.capstone.exchangesystem.constants.AppStatus.EXTERNAL_STORAGE_REQUEST;
import static com.project.capstone.exchangesystem.constants.AppStatus.GALLERY_REQUEST;
import static com.project.capstone.exchangesystem.constants.AppStatus.TRANSACTION_DONE;
import static com.project.capstone.exchangesystem.constants.AppStatus.TRANSACTION_RECEIVER_RECEIPT_CONFRIMED;
import static com.project.capstone.exchangesystem.constants.AppStatus.TRANSACTION_SENDER_RECEIPT_CONFRIMED;

public class TransactionDetailActivity extends AppCompatActivity implements ImageOptionDialog.ImageOptionListener {

    ListView lvYourItems, lvMyItems;
    TextView txtReceiverName, txtReceiverPhone, txtReceiverAddress, txtShowYourItems, txtShowMyItems, txtDeleteImage, txtReceiptTitle, btnAddViewImage;
    ImageView ivQRCode, receiptImage;
    Button btnConfirmReceipt;
    LinearLayout linearQR, linearReceipt, linearFinish, btnMaps;
    Toolbar toolbar;
    Dialog dialog;
    SwitchCompat swDelivery;
    ProgressDialog progressDialog;

    ArrayList<Item> myItems, yourItems;
    DetailItemAdapter myItemAdapter, yourItemAdapter;
    List<TransactionDetail> transDetailList;
    TransactionRequestWrapper dataInf;

    SharedPreferences sharedPreferences;
    RmaAPIService rmaAPIService;
    String authorization, qrCode, checkScanQRCode, myReceiptUrl, removedReceiptUrl, yourReceiptUrl, youConfirmedStatus, meConfirmedStatus;
    int myUserId, transactionId, rateStar = 0, yourUserId;
    boolean checkShowMyItems = false, checkShowYourItems = false, checkUploadReceipt = false;

    Context context;
    FirebaseImg firebaseImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_detail);
        context = this;
        direct();
        setItemAdapter();
        getData();
    }

    private void setItemAdapter() {
        myItems = new ArrayList<>();
        myItemAdapter = new DetailItemAdapter(this, myItems);
        lvMyItems.setAdapter(myItemAdapter);

        yourItems = new ArrayList<>();
        yourItemAdapter = new DetailItemAdapter(this, yourItems);
        lvYourItems.setAdapter(yourItemAdapter);
    }


    private void direct() {
        lvMyItems = findViewById(R.id.lvMyItems);
        lvYourItems = findViewById(R.id.lvYourItems);

        txtReceiverName = findViewById(R.id.txtReceiverName);
        txtReceiverPhone = findViewById(R.id.txtReceiverPhone);
        txtReceiverAddress = findViewById(R.id.txtReceiverAddress);
        ivQRCode = findViewById(R.id.ivQRCode);
        btnMaps = findViewById(R.id.btnMaps);
        toolbar = findViewById(R.id.tbToolbar);
        txtShowMyItems = findViewById(R.id.txtShowMyItems);
        txtShowYourItems = findViewById(R.id.txtShowYourItems);
        swDelivery = findViewById(R.id.swDelivery);
        linearQR = findViewById(R.id.linearQR);
        linearReceipt = findViewById(R.id.linearReceipt);
        btnAddViewImage = findViewById(R.id.btnAddViewImage);
        receiptImage = findViewById(R.id.receiptImage);
        firebaseImg = new FirebaseImg();
        txtDeleteImage = findViewById(R.id.txtDeleteImage);
        txtReceiptTitle = findViewById(R.id.txtReceiptTitle);
        btnConfirmReceipt = findViewById(R.id.btnConfirmReceipt);
        linearFinish = findViewById(R.id.linearFinish);

        btnMaps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + txtReceiverAddress.getText().toString());
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);
            }
        });

        txtShowMyItems.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkShowMyItems) {
                    lvMyItems.setVisibility(View.VISIBLE);
                    checkShowMyItems = true;
                    txtShowMyItems.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.round_keyboard_arrow_down_black_24, 0);
                } else {
                    lvMyItems.setVisibility(View.GONE);
                    checkShowMyItems = false;
                    txtShowMyItems.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.round_keyboard_arrow_right_black_24, 0);
                }
            }
        });

        txtShowYourItems.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkShowYourItems) {
                    lvYourItems.setVisibility(View.VISIBLE);
                    checkShowYourItems = true;
                    txtShowYourItems.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.round_keyboard_arrow_down_black_24, 0);
                } else {
                    lvYourItems.setVisibility(View.GONE);
                    checkShowYourItems = false;
                    txtShowYourItems.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.round_keyboard_arrow_right_black_24, 0);
                }
            }
        });

        swDelivery.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    linearQR.setVisibility(View.GONE);
                    linearReceipt.setVisibility(View.VISIBLE);

                    if (yourItems.size() == 0){
                        btnConfirmReceipt.setVisibility(View.GONE);
                        btnMaps.setVisibility(View.VISIBLE);
                    }

                    if (yourReceiptUrl != null){
                        btnConfirmReceipt.setVisibility(View.VISIBLE);
                    } else {
                        btnConfirmReceipt.setVisibility(View.GONE);
                    }

                    if (dataInf.getTransaction().getStatus().equals(TRANSACTION_DONE)){
                        btnAddViewImage.setText(getString(R.string.view_my_receipt));
                        checkUploadReceipt = true;
                        btnConfirmReceipt.setVisibility(View.GONE);
                        btnMaps.setVisibility(View.GONE);
                    }
                } else {
                    linearQR.setVisibility(View.VISIBLE);
                    linearReceipt.setVisibility(View.GONE);
                    btnConfirmReceipt.setVisibility(View.GONE);
                    btnMaps.setVisibility(View.GONE);
                }
            }
        });

        btnAddViewImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkUploadReceipt) {
                    ImageOptionDialog optionDialog = new ImageOptionDialog();
                    optionDialog.setActivityFlag(ADD_IMAGE_FLAG);
                    optionDialog.show(getSupportFragmentManager(), "optionDialog");
                } else {
                    showReceiptDialog(myReceiptUrl);
                }
            }
        });

        btnConfirmReceipt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmReceipt();
            }
        });

        receiptImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (yourReceiptUrl != null){
                    showReceiptDialog(yourReceiptUrl);
                }
            }
        });

        txtDeleteImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removedReceiptUrl = myReceiptUrl;
                btnAddViewImage.setText(getString(R.string.add_receipt));
                checkUploadReceipt = false;
                txtDeleteImage.setVisibility(View.GONE);
                btnMaps.setVisibility(View.VISIBLE);
                Toast.makeText(context, "Hãy thêm ảnh mới để cập nhật lại biên nhận của bạn.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        String[] imageTypes = {"image/jpeg", "image/png", "image/jpg"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES, imageTypes);
        startActivityForResult(intent, GALLERY_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null) {
            if (requestCode == GALLERY_REQUEST) {
                uploadImageToFireBase(data.getData());
            } else if (requestCode == CAMERA_REQUEST) {
                if (data.getExtras() != null) {
                    uploadImageToFireBase(getUriFromCaptureImage(data));
                }
            }
        }
    }

    private Uri getUriFromCaptureImage(Intent data) {
        Bitmap captureImg = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        captureImg.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(this.getContentResolver(), captureImg, "CaptureIMG", null);
        return Uri.parse(path);
    }

    private void showRatingDialog() {
        dialog = new Dialog(TransactionDetailActivity.this);
        dialog.setContentView(R.layout.rating_dialog);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        setRatingDialogComponents(dialog);
    }

    private void setRatingDialogComponents(final Dialog dialog) {
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
                if (authorization != null) {
                    Rate rate = new Rate();
                    rate.setReceiverId(yourUserId);
                    rate.setContent(edtContent.getText().toString());
                    rate.setRate(rateStar);

                    rmaAPIService.createRating(authorization, rate).enqueue(new Callback<Rate>() {
                        @Override
                        public void onResponse(Call<Rate> call, Response<Rate> response) {
                            if (response.body() != null) {
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
            }
        });
    }

    private void getData() {
        transactionId = (int) getIntent().getSerializableExtra("transactionId");
        sharedPreferences = getSharedPreferences("localData", MODE_PRIVATE);
        authorization = sharedPreferences.getString("authorization", null);
        myUserId = sharedPreferences.getInt("userId", 0);
        checkScanQRCode = (String) getIntent().getSerializableExtra("scannedQRCode");

        rmaAPIService = RmaAPIUtils.getAPIService();

        if (authorization != null) {
            rmaAPIService.getTransactionByTransID(authorization, transactionId).enqueue(new Callback<TransactionRequestWrapper>() {
                @Override
                public void onResponse(Call<TransactionRequestWrapper> call, Response<TransactionRequestWrapper> response) {
                    if (response.body() != null) {
                        dataInf = response.body();
                        setUserInf();
                    } else {
                        Log.i("transDetail", "null");
                    }
                }

                @Override
                public void onFailure(Call<TransactionRequestWrapper> call, Throwable t) {
                    Log.i("transDetail", "failed");
                }
            });
        }
    }

    private void setUserInf() {
        if (myUserId == dataInf.getTransaction().getReceiverId()) {
            yourUserId = dataInf.getTransaction().getSenderId();
            txtReceiverName.setText(dataInf.getTransaction().getSender().getFullName());
            txtReceiverPhone.setText(dataInf.getTransaction().getSender().getPhone());
            txtReceiverAddress.setText(dataInf.getTransaction().getSender().getAddress());
            myReceiptUrl = dataInf.getTransaction().getReceiverReceipt();
            yourReceiptUrl = dataInf.getTransaction().getSenderReceipt();
            meConfirmedStatus = TRANSACTION_SENDER_RECEIPT_CONFRIMED;
            youConfirmedStatus = TRANSACTION_RECEIVER_RECEIPT_CONFRIMED;
        } else if (myUserId != dataInf.getTransaction().getReceiverId()) {
            yourUserId = dataInf.getTransaction().getReceiverId();
            txtReceiverName.setText(dataInf.getTransaction().getReceiver().getFullName());
            txtReceiverPhone.setText(dataInf.getTransaction().getReceiver().getPhone());
            txtReceiverAddress.setText(dataInf.getTransaction().getReceiver().getAddress());
            myReceiptUrl = dataInf.getTransaction().getSenderReceipt();
            yourReceiptUrl = dataInf.getTransaction().getReceiverReceipt();
            meConfirmedStatus = TRANSACTION_RECEIVER_RECEIPT_CONFRIMED;
            youConfirmedStatus = TRANSACTION_SENDER_RECEIPT_CONFRIMED;
        }

        txtShowYourItems.setText(getString(R.string.trade_receive_item) + " " + txtReceiverName.getText().toString());
        setToolbar();

        transDetailList = dataInf.getDetails();

        ArrayList<Item> tmpMyItems = new ArrayList<>();
        ArrayList<Item> tmpYourItems = new ArrayList<>();

        for (int i = 0; i < transDetailList.size(); i++) {
            Item tmpItem = transDetailList.get(i).getItem();
            if (tmpItem.getUser().getId() == myUserId) {
                tmpMyItems.add(tmpItem);
            } else {
                tmpYourItems.add(tmpItem);
            }
        }

        qrCode = dataInf.getTransaction().getQrCode();
        if (qrCode != null) {
            createQRCode(qrCode);
        }

        if (tmpMyItems.size() != 0) {
            myItemAdapter.setfilter(tmpMyItems);
        } else {
            txtShowMyItems.setVisibility(View.GONE);
            btnAddViewImage.setVisibility(View.GONE);
            btnMaps.setVisibility(View.GONE);
        }

        if (tmpYourItems.size() != 0) {
            yourItemAdapter.setfilter(tmpYourItems);
        } else {
            txtShowYourItems.setVisibility(View.GONE);
            txtReceiptTitle.setVisibility(View.GONE);
            receiptImage.setVisibility(View.GONE);
        }

        if (yourReceiptUrl != null || myReceiptUrl != null) {

            if (yourReceiptUrl != null){
                Picasso.with(getApplicationContext()).load(yourReceiptUrl)
                        .placeholder(R.drawable.ic_no_image)
                        .error(R.drawable.ic_no_image)
                        .into(receiptImage);
                btnConfirmReceipt.setVisibility(View.VISIBLE);
                receiptImage.setVisibility(View.VISIBLE);
                txtReceiptTitle.setText(getString(R.string.receipt_title) + " " + txtReceiverName.getText().toString());
            }

            linearQR.setVisibility(View.GONE);
            linearReceipt.setVisibility(View.VISIBLE);
            swDelivery.setChecked(true);

            if (myReceiptUrl != null) {
                btnAddViewImage.setText(getString(R.string.view_my_receipt));
                checkUploadReceipt = true;
                txtDeleteImage.setVisibility(View.VISIBLE);
                btnMaps.setVisibility(View.GONE);
            }

            if (dataInf.getTransaction().getStatus().equals(youConfirmedStatus)){
                btnAddViewImage.setText(getString(R.string.view_my_receipt));
                checkUploadReceipt = true;
                txtDeleteImage.setVisibility(View.GONE);
                btnMaps.setVisibility(View.GONE);
            } else if (dataInf.getTransaction().getStatus().equals(meConfirmedStatus)) {
                btnConfirmReceipt.setVisibility(View.GONE);
            }
        } else {
            if (yourReceiptUrl == null){
                txtReceiptTitle.setText(txtReceiverName.getText().toString() + " " + getString(R.string.not_upload_receipt_yet));
                receiptImage.setVisibility(View.GONE);
                btnMaps.setVisibility(View.GONE);
            }

            if (myReceiptUrl == null) {

            }
        }

        if (dataInf.getTransaction().getStatus().equals(TRANSACTION_DONE)){
            linearFinish.setVisibility(View.VISIBLE);
            btnAddViewImage.setText(getString(R.string.view_my_receipt));
            checkUploadReceipt = true;
            btnConfirmReceipt.setVisibility(View.GONE);
            txtDeleteImage.setVisibility(View.GONE);
            btnMaps.setVisibility(View.GONE);
        }

        if (checkScanQRCode != null) {
            showRatingDialog();
        }
    }

    private void createQRCode(String qrCode) {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        try {
            BitMatrix bitMatrix = qrCodeWriter.encode(qrCode, BarcodeFormat.QR_CODE, 420, 420);
            int width = bitMatrix.getWidth();
            int height = bitMatrix.getHeight();
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
            for (int i = 0; i < width; i++) {
                for (int j = 0; j < height; j++) {
                    bitmap.setPixel(i, j, bitMatrix.get(i, j) ? Color.BLACK : Color.WHITE);
                }
            }

            ivQRCode.setImageBitmap(bitmap);
            ivQRCode.setVisibility(View.VISIBLE);

        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

    private void setToolbar() {
        toolbar.setTitle(getString(R.string.trade_inf_title));
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
    public void onButtonClicked(int choice) {
        switch (choice) {
            case 0:
                getImageFromGallery();
                break;
            case 1:
                takePhoto();
                break;
            default:
                break;
        }
    }

    private void takePhoto() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, EXTERNAL_STORAGE_REQUEST);
            } else {
                setCameraPermission();
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, CAMERA_REQUEST);
            }
        } else {
            setCameraPermission();
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent, CAMERA_REQUEST);
        }
    }

    private void setCameraPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.CAMERA}, CAMERA_REQUEST);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_REQUEST) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, CAMERA_REQUEST);
            }
        }
        if (requestCode == EXTERNAL_STORAGE_REQUEST) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                setCameraPermission();
            }
        }
    }

    private void uploadImageToFireBase(Uri uploadedImage) {

        if (firebaseImg.checkLoginFirebase()) {
            progressDialog = new ProgressDialog(context);
            progressDialog.setTitle("Đang tải hình ảnh...");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();

            FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
            StorageReference storageReference = firebaseStorage.getReference();
            StorageReference reference = storageReference.child("images/" + UUID.randomUUID().toString());

            reference.putFile(uploadedImage).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()) {
                        task.getResult().getMetadata().getReference().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String url = uri.toString();
                                progressDialog.dismiss();
                                addReceiptToDB(url);
                            }
                        });
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    progressDialog.setMessage("Vui lòng chờ!!!");
                }
            });
        }
    }

    private void addReceiptToDB(final String url) {
        if (authorization != null) {
            final Map<String, Object> jsonBody = new HashMap<String, Object>();
            jsonBody.put("id", dataInf.getTransaction().getId());
            jsonBody.put("url", url);

            rmaAPIService.uploadReceipt(authorization, jsonBody).enqueue(new Callback<Transaction>() {
                @Override
                public void onResponse(Call<Transaction> call, Response<Transaction> response) {
                    if (response.body() != null) {
                        myReceiptUrl = url;
                        btnAddViewImage.setText(getString(R.string.view_my_receipt));
                        txtDeleteImage.setVisibility(View.VISIBLE);
                        btnMaps.setVisibility(View.GONE);
                        checkUploadReceipt = true;
                        Toast.makeText(context, getString(R.string.added_receipt), Toast.LENGTH_SHORT).show();
                        if (removedReceiptUrl != null) {
                            firebaseImg.deleteImageOnFirebase(removedReceiptUrl);
                        }
                    } else {
                        Log.i("addReceiptToDB", "Cannot save to DB");
                        Log.i("addReceiptToDB", "" + response.code());
                    }
                }

                @Override
                public void onFailure(Call<Transaction> call, Throwable t) {
                    Log.i("addReceiptToDB", "Cannot connect DB");
                }
            });
        }
    }

    private void confirmReceipt() {
        if (authorization != null){
            final Map<String, Object> jsonBody = new HashMap<String, Object>();
            jsonBody.put("id", dataInf.getTransaction().getId());

            rmaAPIService.confirmReceipt(authorization, jsonBody).enqueue(new Callback<Transaction>() {
                @Override
                public void onResponse(Call<Transaction> call, Response<Transaction> response) {
                    if (response.body() != null){
                        Toast.makeText(context, getString(R.string.confirmed_receipt), Toast.LENGTH_SHORT).show();
                        btnConfirmReceipt.setVisibility(View.GONE);
                        showRatingDialog();
                        if (response.body().getStatus().equals(TRANSACTION_DONE)){
                            linearFinish.setVisibility(View.VISIBLE);
                        }
                    } else {
                        Log.i("confirmReceipt", "Cannot save to DB");
                        Log.i("confirmReceipt", "" + response.code());
                    }
                }

                @Override
                public void onFailure(Call<Transaction> call, Throwable t) {
                    Log.i("confirmReceipt", "Cannot connect DB");
                }
            });
        }
    }

    private void showReceiptDialog(String imageUrl) {
        dialog = new Dialog(TransactionDetailActivity.this);
        dialog.setContentView(R.layout.full_receipt_view);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        setReceiptDialogComponents(dialog, imageUrl);
    }

    private void setReceiptDialogComponents(final Dialog dialog, String imageUrl) {
        ImageView ivFullReceipt;
        ImageButton btnCloseImage;

        ivFullReceipt = dialog.findViewById(R.id.ivFullReceipt);
        btnCloseImage = dialog.findViewById(R.id.btnCloseImage);

        Picasso.with(getApplicationContext()).load(imageUrl)
                .placeholder(R.drawable.ic_no_image)
                .error(R.drawable.ic_no_image)
                .into(ivFullReceipt);

        btnCloseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }
}
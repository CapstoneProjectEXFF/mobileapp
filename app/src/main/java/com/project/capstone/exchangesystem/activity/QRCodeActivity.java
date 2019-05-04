package com.project.capstone.exchangesystem.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;

import com.github.nkzawa.emitter.Emitter;
import com.google.android.gms.vision.barcode.Barcode;
import com.project.capstone.exchangesystem.R;
import com.project.capstone.exchangesystem.sockets.SocketServer;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import info.androidhive.barcode.BarcodeReader;

public class QRCodeActivity extends AppCompatActivity implements BarcodeReader.BarcodeReaderListener {

    BarcodeReader barcodeReader;
    SocketServer socketServer;
    SharedPreferences sharedPreferences;
    int userId;
    Toolbar tbToolbar;
    ArrayList<Integer> transactionIds;
    Context context;
    String tmpQrCode, authorization;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode);
        context = this;
        sharedPreferences = getSharedPreferences("localData", MODE_PRIVATE);
        authorization = sharedPreferences.getString("authorization", null);
        userId = sharedPreferences.getInt("userId", 0);
        barcodeReader = (BarcodeReader) getSupportFragmentManager().findFragmentById(R.id.qr_scanner);
        tbToolbar = findViewById(R.id.tbToolbar);
        transactionIds = (ArrayList<Integer>) getIntent().getSerializableExtra("transactionIds");
        setToolbar();
        socketServer = new SocketServer();
        socketServer.mSocket.on("scan-succeeded", succeededQRCode);
        socketServer.mSocket.on("transaction-succeeded", succeededTransaction);
    }

    @Override
    public void onScanned(Barcode barcode) {
        barcodeReader.playBeep();

        JSONObject qrCode = new JSONObject();
        try {
            tmpQrCode = barcode.displayValue;
            qrCode.put("qrCode", barcode.displayValue);
            qrCode.put("userId", userId);
            qrCode.put("token", authorization);
            socketServer.connect();
            socketServer.emitQRCode(qrCode);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onScannedMultiple(List<Barcode> barcodes) {

    }

    @Override
    public void onBitmapScanned(SparseArray<Barcode> sparseArray) {

    }

    @Override
    public void onScanError(String errorMessage) {

    }

    Emitter.Listener succeededQRCode = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.i("succeededQRCode", args[0].toString());
            JSONObject data = (JSONObject) args[0];
            try {
                final int transactionId = Integer.parseInt(data.getString("transactionId"));
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.i("here", "aaa");
                        boolean checkExistedTransId = false;
                        for (int i = 0; i < transactionIds.size(); i++){
                            if (transactionId == transactionIds.get(i)){
                                checkExistedTransId = true;
                                break;
                            }
                        }
                        Log.i("here", "bbb");
                        Log.i("check", "" + checkExistedTransId);
                        if (checkExistedTransId){
                            Log.i("here", "ccc");
                            settingDialog("Xác nhận giao dịch", "Xác nhận thành công", "Hoàn thành", true, transactionId);
                        } else {
                            Log.i("here", "ddd");
                            settingDialog("Xác nhận giao dịch", "Mã giao dịch không đúng", "Thử lại", true, -1);
                        }
                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    Emitter.Listener succeededTransaction = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.i("succeededTransaction", args[0].toString());
            final int transactionId = Integer.parseInt(args[0].toString());

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    boolean checkExistedTransId = false;
                    for (int i = 0; i < transactionIds.size(); i++){
                        if (transactionId == transactionIds.get(i)){
                            checkExistedTransId = true;
                            break;
                        }
                    }
                    if (checkExistedTransId){
                        settingDialog("Xác nhận giao dịch", "Xác nhận thành công", "Hoàn thành", true, transactionId);
                    } else {
                        settingDialog("Xác nhận giao dịch", "Mã giao dịch không đúng", "Thử lại", false, -1);
                    }
                }
            });
        }
    };

    private void setToolbar() {
        setSupportActionBar(tbToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        tbToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void settingDialog(String title, String message, String btnContent, final boolean status, final int transactionId){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setNeutralButton(btnContent, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (status){
                    Intent intent = new Intent(context, TransactionDetailActivity.class);
                    intent.putExtra("qrCode", tmpQrCode);
                    intent.putExtra("transactionId", transactionId);
                    intent.putExtra("scannedQRCode", "scanned");
                    startActivity(intent);
                } else {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
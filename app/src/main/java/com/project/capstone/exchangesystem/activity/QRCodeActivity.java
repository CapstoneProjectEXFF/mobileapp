package com.project.capstone.exchangesystem.activity;

import android.content.Intent;
import android.content.SharedPreferences;
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

import java.util.List;

import info.androidhive.barcode.BarcodeReader;

public class QRCodeActivity extends AppCompatActivity implements BarcodeReader.BarcodeReaderListener {

    BarcodeReader barcodeReader;
    SocketServer socketServer;
    SharedPreferences sharedPreferences;
    int userId;
    Toolbar tbToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode);

        sharedPreferences = getSharedPreferences("localData", MODE_PRIVATE);
        userId = sharedPreferences.getInt("userId", 0);
        barcodeReader = (BarcodeReader) getSupportFragmentManager().findFragmentById(R.id.qr_scanner);
        tbToolbar = findViewById(R.id.tbToolbar);

        setToolbar();
        socketServer = new SocketServer();
        socketServer.mSocket.on("scan-succeeded", succeededQRCode);
        socketServer.mSocket.on("transaction-succeeded", succeededTransaction);

    }

    @Override
    public void onScanned(Barcode barcode) {
        barcodeReader.playBeep();

//        Intent intent = new Intent(this, QRCodeResultActivity.class);
//        intent.putExtra("result", barcode.displayValue);
//        startActivity(intent);

        JSONObject qrCode = new JSONObject();
        try {
            qrCode.put("qrCode", barcode.displayValue);
            qrCode.put("userId", userId);
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
                int transactionId = Integer.parseInt(data.getString("transactionId"));
                int tmpUserId = Integer.parseInt(data.getString("userId"));
                //TODO move to transactionDetail
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
//                    Intent intent = new Intent(getApplicationContext(), QRCodeResultActivity.class);
//                    intent.putExtra("result", barcode.displayValue);
//                    startActivity(intent);
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
            int transactionId = Integer.parseInt(args[0].toString());

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //TODO move to transactionDetail
                    Intent intent = new Intent(getApplicationContext(), QRCodeResultActivity.class);
                    intent.putExtra("result", "done");
                    startActivity(intent);
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
}

package com.project.capstone.exchangesystem.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode);

        barcodeReader = (BarcodeReader) getSupportFragmentManager().findFragmentById(R.id.qr_scanner);
        socketServer = new SocketServer();
        socketServer.mSocket.on("qr-scanned", qrCode);
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

    Emitter.Listener qrCode = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.i("qrCode", args[0].toString());
            JSONObject data = (JSONObject) args[0];
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
//                    Intent intent = new Intent(getApplicationContext(), QRCodeResultActivity.class);
//                    intent.putExtra("result", barcode.displayValue);
//                    startActivity(intent);
                }
            });

        }
    };
}

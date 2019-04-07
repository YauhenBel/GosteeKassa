package com.example.genia.gosteekassa.Controllers;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.genia.gosteekassa.ConnToDB.ConnDB;
import com.example.genia.gosteekassa.R;
import com.google.zxing.Result;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class QrScanner extends AppCompatActivity implements ZXingScannerView.ResultHandler{

    private static final String TAG = "QrScanner";
    private ZXingScannerView mScanner;
    private TextView tvStatus;
    private String mResult = "";
    private String service_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_scanner);

        SharedPreferences preferences = getSharedPreferences("info", MODE_PRIVATE);
        service_id = preferences.getString("service_id", "");

        int permissionStatus = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);

        if (permissionStatus == PackageManager.PERMISSION_GRANTED) {
            // Programmatically initialize the scanner view
            //setContentView(mScannerView);
        } else {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA},
                    1);
        }

        Button btnConfirm = findViewById(R.id.btnConfirm);

        mScanner = findViewById(R.id.zxscan);


        tvStatus = findViewById(R.id.tvStatus);

        tvStatus.setText("Сканировнаие...");

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyThread myThread = new MyThread();
                myThread.start();
                finish();
            }
        });

    }

    private void changeMarkForUser(){
        String input = "";
        try {

            String SERVER_NAME = "http://r2551241.beget.tech";
            input = SERVER_NAME
                    + "/gosteekassa.php?action=changeMarkForUser&idUser="
                    + URLEncoder.encode(mResult, "UTF-8")
                    + "&service_id="
                    + URLEncoder.encode(service_id, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        ConnDB connDB = new ConnDB();
        String ansver = connDB.sendRequest(input, this);
        Log.i(TAG, "updateDB: ansver: " + ansver);
        if (ansver.equals("1")) {
            Log.i(TAG, "changeMarkForUser: Данный пользователь не добавил вашу карту");
            workWithGui();
        }
    }

    private void workWithGui(){
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override public void run() {
                Toast.makeText(getApplicationContext(),
                        "Данный пользователь не добавил вашу карту", Toast.LENGTH_LONG)
                        .show();
            }
        });
    }
    class MyThread extends Thread{
        @Override
        public void run() {
            super.run();
            changeMarkForUser();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Programmatically initialize the scanner view
                    //setContentView(mScanner);
                } else {
                    // permission denied
                }
                return;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mScanner.setResultHandler(this); // Register ourselves as a handler for scan results.
        mScanner.startCamera();          // Start camera on resume
    }

    @Override
    public void onPause() {
        super.onPause();
        mScanner.stopCamera();           // Stop camera on pause
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void handleResult(Result result) {
        tvStatus.setText("Сканирование выполнено.");
        mResult = String.valueOf(result);

    }
}

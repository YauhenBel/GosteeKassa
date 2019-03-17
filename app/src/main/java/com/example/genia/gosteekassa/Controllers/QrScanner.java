package com.example.genia.gosteekassa.Controllers;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.genia.gosteekassa.R;
import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class QrScanner extends AppCompatActivity implements ZXingScannerView.ResultHandler{

    private ZXingScannerView mScanner;
    private Button btnConfirm;
    private TextView tvStatus, tvResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_scanner);

        int permissionStatus = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);

        if (permissionStatus == PackageManager.PERMISSION_GRANTED) {
            // Programmatically initialize the scanner view
            //setContentView(mScannerView);
        } else {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA},
                    1);
        }

        btnConfirm = findViewById(R.id.btnConfirm);

        mScanner = findViewById(R.id.zxscan);

        tvStatus = findViewById(R.id.tvStatus);

        tvResult = findViewById(R.id.tvResult);

        tvStatus.setText("Сканировнаие...");

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


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
        tvResult.setText("ID пользователя: " + result);

    }
}

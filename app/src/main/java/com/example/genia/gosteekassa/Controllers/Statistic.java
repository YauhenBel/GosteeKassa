package com.example.genia.gosteekassa.Controllers;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.genia.gosteekassa.ConnToDB.ConnDB;
import com.example.genia.gosteekassa.R;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.jasypt.util.password.StrongPasswordEncryptor;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class Statistic extends AppCompatActivity {

    private Button openQRScanner;
    private String TAG = "Statistic";
    private TextView userCount, useCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistic);

        userCount = findViewById(R.id.userCount);
        useCount = findViewById(R.id.useCount);

        openQRScanner = findViewById(R.id.openQRScanner);

        openQRScanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Statistic.this, QrScanner.class);
                startActivity(intent);
            }
        });
    }

    public void goBack(View view) {
        finish();
    }

    public void goEitInfo(View view) {

        Intent intent = new Intent(Statistic.this, EditKassa.class);
        startActivity(intent);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        new Thread(new Runnable() {
            @Override public void run() {
                getStatistic();
            }
        }).start();
    }

    private void getStatistic(){
        SharedPreferences preferences = getSharedPreferences("info", MODE_PRIVATE);
        String input = "";
        try {
            String SERVER_NAME = "http://r2551241.beget.tech";
            input = SERVER_NAME
                    + "/gosteekassa.php?action=getStatistic&service_id="
                    + URLEncoder.encode(preferences.getString("service_id", ""), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        ConnDB connDB = new ConnDB();
        String ansver = connDB.sendRequest(input, this);
        Log.i(TAG, "updateDB: ansver: " + ansver);
        if (ansver != null && !ansver.isEmpty()) {
            Log.i("ConnDB", "+ Connect ---------- reply contains JSON:" + ansver);
            try {
                Log.i("userLogIn", " - answer: " + ansver);
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode jsonNode = objectMapper.readTree(ansver);

                final JsonNode user_count = jsonNode.path("user_count");
                final JsonNode use_count = jsonNode.path("use_count");
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        userCount.setText(user_count.asText());
                        useCount.setText(use_count.asText());
                    }
                });

            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

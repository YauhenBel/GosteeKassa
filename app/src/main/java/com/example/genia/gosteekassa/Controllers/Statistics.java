package com.example.genia.gosteekassa.Controllers;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.genia.gosteekassa.R;

public class Statistics extends AppCompatActivity {

    private Button openQRScanner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        openQRScanner = findViewById(R.id.openQRScanner);

        openQRScanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Statistics.this, QrScanner.class);
                startActivity(intent);
            }
        });
    }

    public void goBack(View view) {
        finish();
    }

    public void goEitInfo(View view) {

        Intent intent = new Intent(Statistics.this, EditKassa.class);
        startActivity(intent);
    }


}

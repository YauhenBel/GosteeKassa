package com.example.genia.gosteekassa.Controllers;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.genia.gosteekassa.ConnToDB.ConnDB;
import com.example.genia.gosteekassa.R;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.jasypt.util.password.StrongPasswordEncryptor;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;


public class MainActivity extends AppCompatActivity {

    private Button btnLogIn;
    private EditText etLogin, etPassword;
    private String input = "";
    private ConstraintLayout constraintLayout;
    private String TAG = "MainActivity";
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnLogIn = (Button) findViewById(R.id.btnLogIn);
        etLogin = (EditText) findViewById(R.id.etLogin);
        etPassword = (EditText) findViewById(R.id.etPassword);
        TextView tvRegistration = (TextView) findViewById(R.id.tvRegistarton);
        TextView tvRecovery = (TextView) findViewById(R.id.tvRecovery);
        constraintLayout = (ConstraintLayout) findViewById(R.id.inputProcecc);

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()){
                    case R.id.btnLogIn:
                        new Thread(new Runnable() {
                            @Override public void run() {
                                userLogIn();
                                workWithGui(1);
                            }
                        }).start();
                        break;
                    case R.id.tvRegistarton:
                        goToRegistration();
                        break;
                    case R.id.tvRecovery:
                        goToRecovery();
                        break;
                }
            }
        };

        btnLogIn.setOnClickListener(onClickListener);
        tvRegistration.setOnClickListener(onClickListener);
        tvRecovery.setOnClickListener(onClickListener);
    }

    private void workWithGui(final int x){
        new Handler(Looper.getMainLooper()).post(       new Runnable() {
            @Override public void run() {
                switch (x){
                    case 0:
                        constraintLayout.setVisibility(View.VISIBLE);
                        btnLogIn.setEnabled(false);
                        break;
                    case 1:
                        constraintLayout.setVisibility(View.INVISIBLE);
                        btnLogIn.setEnabled(true);
                        break;
                    case 2:
                        Log.i("Registration", "Пароли не совпадают.");
                        Toast.makeText(getApplicationContext(),
                                "Неправильный логин или пароль", Toast.LENGTH_SHORT)
                                .show();
                        break;
                    case 3:
                        Toast.makeText(getApplicationContext(),
                                "Заполните все поля.", Toast.LENGTH_SHORT)
                                .show();
                        break;

                }
            }
        });
    }

    @SuppressLint("ResourceAsColor")
    private void userLogIn() {
        Log.i("MainActivity", "Authorization");
        String editLogin = etLogin.getText().toString();
        String editPassword = etPassword.getText().toString();

        if (editLogin.isEmpty() || editPassword.isEmpty()) {
            Log.i("MainActivity", "Authorization2");
            workWithGui(3);
            return;
        }
        workWithGui(0);
        Log.i("MainActivity", "Authorization1");

        try {
            String SERVER_NAME = "http://r2551241.beget.tech";
            input = SERVER_NAME
                    + "/gosteekassa.php?action=getUserInformation&login="
                    + URLEncoder.encode(editLogin, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        ConnDB connDB = new ConnDB();
        String ansver = connDB.sendRequest(input, this);

        if (ansver != null && !ansver.isEmpty()) {
            Log.i("ConnDB", "+ Connect ---------- reply contains JSON:" + ansver);
            try {
                Log.i("userLogIn", " - answer: " + ansver);
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode jsonNode = objectMapper.readTree(ansver);

                JsonNode idNode = jsonNode.path("service_id");
                String mServiceId = idNode.asText();
                JsonNode nameNode = jsonNode.path("manager_name");
                String mManagerName = nameNode.asText();
                JsonNode passwordNode = jsonNode.path("password");
                Log.i(TAG, "userLogIn: " + passwordNode.asText());
                Log.i(TAG, "editPassword: " + editPassword);
                JsonNode statusNode = jsonNode.path("statusRecovery");

                sharedPreferences = getSharedPreferences("info", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("service_id", mServiceId);
                editor.putString("manager_name", mManagerName);
                editor.apply();

                StrongPasswordEncryptor passwordEncryptor = new StrongPasswordEncryptor();
                if (passwordEncryptor.checkPassword(editPassword, passwordNode.asText())){
                    Log.i("MainActivity", "Пароли совпадают.");

                    if (statusNode.asText().equals("0")) {
                        goToMainWorkScreen();
                    }else {
                        goToCreateNewPassword();
                    }
                }else{
                    workWithGui(2);
                }

            }
            catch (Exception e) {
                workWithGui(2);
            }
        }
    }


    private void goToMainWorkScreen(){
        Intent intent = new Intent(this, Statistic.class);
        startActivity(intent);
    }

    private void goToCreateNewPassword(){
        Intent intent = new Intent(this, NewPassword.class);
        startActivity(intent);
    }

    private void goToRecovery(){
        Intent intent = new Intent(this, RecoveryPassword.class);
        startActivity(intent);
    }

    private void goToRegistration(){
        Intent intent = new Intent(this, Registration.class);
        startActivity(intent);
    }

}

package com.example.genia.gosteekassa.Controllers;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.genia.gosteekassa.ConnToDB.ConnDB;
import com.example.genia.gosteekassa.R;

import org.jasypt.util.password.StrongPasswordEncryptor;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class NewPassword extends AppCompatActivity {

    private EditText edNewPassword, edCheckNewPassword;
    private Button btnSaveNewPassword;
    private ConstraintLayout constraintLayout;
    private String input = "";
    private SharedPreferences preferences;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_password);

        preferences = getSharedPreferences("info", MODE_PRIVATE);

        //bundle = getIntent().getExtras();
        Log.i("NewPassword", "Номер пользователя: " + preferences.getString("userId", ""));

        edNewPassword = (EditText) findViewById(R.id.edNewPassword);
        edCheckNewPassword = (EditText) findViewById(R.id.edCheckNewPassword);
        btnSaveNewPassword = (Button) findViewById(R.id.btnSaveNewPassword);
        constraintLayout = (ConstraintLayout) findViewById(R.id.newPasswProcecc);


        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()){
                    case R.id.btnSaveNewPassword:
                        new Thread(new Runnable() {
                            @Override public void run() {
                                saveNewPassword();
                                workWithGui(1);
                            }
                        }).start();

                        break;
                }
            }
        };

        btnSaveNewPassword.setOnClickListener(onClickListener);
    }

    public void goBack(View view) {
        finish();
    }

    private void workWithGui(final int x){
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override public void run() {
                switch (x){
                    case 0:
                        constraintLayout.setVisibility(View.VISIBLE);
                        btnSaveNewPassword.setEnabled(false);
                        break;
                    case 1:
                        constraintLayout.setVisibility(View.INVISIBLE);
                        btnSaveNewPassword.setEnabled(true);
                        break;
                    case 2:
                        Toast.makeText(getApplicationContext(),
                                "Заполните все поля", Toast.LENGTH_SHORT)
                                .show();
                        break;
                    case 3:
                        Toast.makeText(getApplicationContext(),
                                "Пароль должен быть не менее шести символов", Toast.LENGTH_SHORT)
                                .show();
                        break;
                    case 4:
                        Toast.makeText(getApplicationContext(),
                                "Пароли не совпадают", Toast.LENGTH_SHORT)
                                .show();
                        break;

                }
            }
        });
    }

    private void saveNewPassword(){
        String password = edNewPassword.getText().toString();
        String check = edCheckNewPassword.getText().toString();
        if (password.isEmpty() || check.isEmpty()){
            workWithGui(2);
            return;
        }
        if (password.length()<6){
            workWithGui(3);
            return;
        }
        if (!password.equals(check)) {
            workWithGui(4);
            return;
        }
        workWithGui(0);

        StrongPasswordEncryptor passwordEncryptor = new StrongPasswordEncryptor();
        String encryptedPassword = passwordEncryptor.encryptPassword(password);

        try {
            String SERVER_NAME = "http://r2551241.beget.tech";
            input = SERVER_NAME
                    + "/gosteekassaRecoveryPassword.php?action=newPassword&service_id="
                    + URLEncoder.encode(preferences.getString("service_id", ""), "UTF-8")
                    +"&password="
                    +URLEncoder.encode(encryptedPassword, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        ConnDB connDB = new ConnDB();
        String ansver = connDB.sendRequest(input, this);

        if (ansver != null && !ansver.isEmpty()) {
                Log.i("ConnToDB",
                        "+ Connect ---------- reply contains JSON:" + ansver);

        }
        finish();
        goToMainWorkScreen();
    }

    private void goToMainWorkScreen(){
        Intent intent = new Intent(this, Statistic.class);
        //intent.putExtra("userId", bundle.getString("id"));
        //intent.putExtra("userName", bundle.getString("userName"));
        startActivity(intent);
    }
}

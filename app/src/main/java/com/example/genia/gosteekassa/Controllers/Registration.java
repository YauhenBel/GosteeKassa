package com.example.genia.gosteekassa.Controllers;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.RequiresApi;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.genia.gosteekassa.R;
import com.example.genia.gosteekassa.ConnToDB.ConnDB;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

import org.jasypt.util.password.StrongPasswordEncryptor;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Registration extends AppCompatActivity {
    private Button btnReg;
    private EditText edLogin, edPassword, edPasswordRepeat,  edName, edEmail, edPhone;
    private String login = "", password = "", passwordRepeat = "", name = "",
            email = "", phone = "",  ansver = "";
    private String encryptedPassword = "";
    private ProgressBar progressBar;
    private ConstraintLayout constraintLayout;
    private String SERVER_NAME = "http://r2551241.beget.tech";
    private String input = "";
    private ConnDB connDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);

        btnReg = (Button) findViewById(R.id.btnReg);

        edLogin = (EditText) findViewById(R.id.edNameOfShop);
        edPassword = (EditText) findViewById(R.id.edPassword);
        edPasswordRepeat = (EditText) findViewById(R.id.edPasswordRepeat);
        edName = (EditText) findViewById(R.id.edName);
        edEmail = (EditText) findViewById(R.id.edEmail);
        edPhone = findViewById(R.id.edPhone);

        login = "";
        password = "";
        passwordRepeat = "";
        name = "";
        email = "";
        phone = "";
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        constraintLayout = (ConstraintLayout) findViewById(R.id.regProcecc);



        OnClickListener onClickListener = new OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
              switch (view.getId()){

                  case R.id.btnReg:

                      new Thread(new Runnable() {
                          @Override public void run() {

                              registration();
                              workWithGui(1);
                              }
                          }).start();

                      break;
              }
            }
        };
        btnReg.setOnClickListener(onClickListener);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void registration() {
        Log.i("Registration", "Кнопка регистрации");
        login = edLogin.getText().toString();
        password = edPassword.getText().toString();
        passwordRepeat = edPasswordRepeat.getText().toString();
        name = edName.getText().toString();
        email = edEmail.getText().toString();
        phone = edPhone.getText().toString();
        if (login.isEmpty() || password.isEmpty() || name.isEmpty()
                || email.isEmpty() || phone.isEmpty()){
            workWithGui(2);
            return;
        }
        if (!isEmailValid(email)){
            workWithGui(3);
            return;
        }
        if (!isPhoneNumberValid(phone)){
            workWithGui(4);
            return;
        }
        if (password.length() <6){
            workWithGui(5);
            return;
        }
        if (!password.equals(passwordRepeat)){
            workWithGui(6);
            return;
        }

        workWithGui(0);
        StrongPasswordEncryptor passwordEncryptor = new StrongPasswordEncryptor();
        encryptedPassword = passwordEncryptor.encryptPassword(password);
        try {
            input = SERVER_NAME
                    + "/gosteekassa.php?action=reg&login="
                    + URLEncoder.encode(login, "UTF-8")
                    +"&password="
                    +URLEncoder.encode(encryptedPassword, "UTF-8")
                    +"&name="
                    +URLEncoder.encode(name, "UTF-8")
                    +"&email="
                    +URLEncoder.encode(email, "UTF-8")
                    +"&phone="
                    +URLEncoder.encode(phone, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        connDB = new ConnDB();
        ansver =  connDB.sendRequest(input, this);
        if (ansver.equals("1")){
            workWithGui(7);
            return;
        }
        finish();
    }

    private void workWithGui(final int x){
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override public void run() {
                switch (x){
                    case 0:
                        constraintLayout.setVisibility(View.VISIBLE);
                        btnReg.setEnabled(false);
                        break;
                    case 1:
                        constraintLayout.setVisibility(View.INVISIBLE);
                        btnReg.setEnabled(true);
                        break;
                    case 2:
                        Toast.makeText(getApplicationContext(),
                                "Заполните все поля", Toast.LENGTH_SHORT)
                                .show();
                        break;
                    case 3:
                        Toast.makeText(getApplicationContext(),
                                "Введите корректный Email", Toast.LENGTH_LONG)
                                .show();
                        break;
                    case 4:
                        Toast.makeText(getApplicationContext(),
                                "Введите корректный номер телефона", Toast.LENGTH_LONG)
                                .show();
                        break;
                    case 5:
                        Toast.makeText(getApplicationContext(),
                                "Пароль должен содержать не меньше шести символов", Toast.LENGTH_LONG)
                                .show();
                        break;
                    case 6:
                        Toast.makeText(getApplicationContext(),
                                "Введенные вами пароли не совпадают.", Toast.LENGTH_LONG)
                                .show();
                        break;
                    case 7:
                        Toast.makeText(getApplicationContext(),
                                "Пользователь с таким логином уже существует", Toast.LENGTH_LONG)
                                .show();
                        break;

                }
            }
        });
    }


    private boolean isPhoneNumberValid(String number){
        Log.i("Registration", "isPhoneNumberValid");
        try {
            PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
            Phonenumber.PhoneNumber phoneNumber = phoneUtil.parse(number, "");
            if (phoneUtil.isValidNumber(phoneNumber)){
                return true;
            }
        } catch (NumberParseException e) {
            e.printStackTrace();
            return false;
        }

        return false;
    }

    public boolean isEmailValid(String email) {
        Log.i("Registration", "isEmailValid");
        String regExpn =
                "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                        +"((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                        +"[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                        +"([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                        +"[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                        +"([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$";

        CharSequence inputStr = email;

        Pattern pattern = Pattern.compile(regExpn,Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);

        if(matcher.matches())
            return true;
        else
            return false;
    }

    public void goBack(View view) {
        finish();
    }
}

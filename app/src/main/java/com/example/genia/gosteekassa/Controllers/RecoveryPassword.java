package com.example.genia.gosteekassa.Controllers;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.genia.gosteekassa.R;
import com.example.genia.gosteekassa.ConnToDB.ConnDB;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

import org.jasypt.util.password.StrongPasswordEncryptor;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RecoveryPassword extends AppCompatActivity {

    private EditText newPassword;
    private Button btnSendNewPassword;
    private Context context;
    private String ansver = "", login = "", temporaryPassword = "" ;
    private ConstraintLayout constraintLayout;
    private String input = "";
    private String SERVER_NAME = "http://r2551241.beget.tech";
    private ConnDB connDB;



    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recoverypass);

        newPassword = (EditText) findViewById(R.id.edRecoveryPassword);
        btnSendNewPassword = (Button) findViewById(R.id.btnSaveRecoveryPassword);

        context = getApplicationContext();

        constraintLayout = (ConstraintLayout) findViewById(R.id.tempPassProcecc);

        OnClickListener onClickListener = new OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View view) {
                switch (view.getId()){
                    case R.id.btnSaveRecoveryPassword:
                        new Thread(new Runnable() {
                            @Override public void run() {
                                createTemporaryPassword();
                                workWithGui(1);
                            }
                        }).start();

                        break;
                }
            }
        };

        btnSendNewPassword.setOnClickListener(onClickListener);
    }

    private void workWithGui(final int x){
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override public void run() {
                switch (x){
                    case 0:
                        constraintLayout.setVisibility(View.VISIBLE);
                        btnSendNewPassword.setEnabled(false);
                        break;
                    case 1:
                        constraintLayout.setVisibility(View.INVISIBLE);
                        btnSendNewPassword.setEnabled(true);
                        break;
                    case 2:
                        Toast.makeText(context,
                                "Пользователь с таким логином не найден", Toast.LENGTH_SHORT)
                                .show();
                        break;
                    case 3:
                        Toast.makeText(context,
                                "Возникла непредвиденная ошибка. Повторите еще раз.", Toast.LENGTH_SHORT)
                                .show();
                        break;
                    case 4:
                        Toast.makeText(context,
                                "Пароль уже отправлен на вашу почту.", Toast.LENGTH_SHORT)
                                .show();
                        break;
                    case 5:
                        Toast.makeText(context,
                                "Введите логин", Toast.LENGTH_SHORT)
                                .show();
                        break;
                    case 6:
                        Toast.makeText(getApplicationContext(),
                                "Введите корректный email или номер телефона", Toast.LENGTH_LONG)
                                .show();
                        break;
                    case 7:
                        Toast.makeText(getApplicationContext(),
                                "Пароль отправлен", Toast.LENGTH_LONG)
                                .show();
                        break;


                }
            }
        });
    }

    private void createTemporaryPassword() {
        Log.i("ConnToDB","createTemporaryPassword");
        login = newPassword.getText().toString();
        if (login.isEmpty()){
            workWithGui(5);
            return;
        }

        /*if (!isEmailValid(login) && !isPhoneNumberValid(login)){
            workWithGui(6);
            return;
        }*/
        workWithGui(0);


        temporaryPassword = generateString(6);

        Log.i("SendRecoveryPassword",
                "registration - записываем в базу временный пароль");
        Log.i("SendRecoveryPassword",
                "Временный пароль: " + temporaryPassword);
        StrongPasswordEncryptor passwordEncryptor = new StrongPasswordEncryptor();
        String encryptedPassword = passwordEncryptor.encryptPassword(temporaryPassword);
        try {
            input = SERVER_NAME
                    + "/gosteekassaRecoveryPassword.php?action=temporaryPassword&login="
                    + URLEncoder.encode(login, "UTF-8")
                    + "&encryptedPassword="
                    + URLEncoder.encode(encryptedPassword, "UTF-8")
                    + "&password="
                    + URLEncoder.encode(temporaryPassword, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        connDB = new ConnDB();
        ansver = connDB.sendRequest(input);

        Log.i("ConnToDB","createTemporaryPassword4");


        if (ansver != null && !ansver.isEmpty()) {
            Log.i("ConnToDB",
                    "+ Connect ---------- reply contains JSON:" + ansver);
            if (ansver.equals("1")){
                workWithGui(7);
                finish();
            }
            if (ansver.equals("0")){
                workWithGui(2);
            }
            if (ansver.equals("2")){
                   workWithGui(3);
            }
            if (ansver.equals("3")){
                workWithGui(4);
                finish();
            }
        }

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

    public static String generateString(int length)
    {
        String RANDSTRING = "ACEFGHJKLMNPQRUVWXYabcdefhijkprstuvwx0123456789";
        Random ran = new Random();
        char[] text = new char[length];
        for (int i = 0; i < length; i++)
        {
            text[i] = RANDSTRING.charAt(ran.nextInt(RANDSTRING.length()));
        }
        return new String(text);
    }
}

package com.example.genia.gosteekassa.Controllers;

import android.annotation.SuppressLint;
import android.app.DialogFragment;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.genia.gosteekassa.ConnToDB.ConnDB;
import com.example.genia.gosteekassa.Dialogs.Dialog1;
import com.example.genia.gosteekassa.R;

import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class EditKassa extends AppCompatActivity implements View.OnClickListener, Dialog1.ButtonOKListener{

    private static final String TAG = "EditKassa";
    private Button btnInProcess, btnInFinish, btnToSave;
    private ImageButton imbtnGetImage;
    private InputStream inputStream;
    private String name = "", filePath, puth, nameOfShop = "", description = "", workerDay = "",
    workerTime = "", typeOfCard = "", input = "", countOfCircle = "", service_id = "";
    private CheckBox chBMonday, chBTuesday, chBWednesday, chBThursday, chBFriday, chBSaturday,
            chBSunday;
    private EditText edHourFrom, edMinutesFrom, edHourTo, edMinutesTo, edNameOfShop, edDescription,
    edCount;
    private Uri chosenImageUri;
    private Boolean isReady = true, isAgree = false, imageIsSelected = false;
    private DialogFragment dialogFragment;
    private ConstraintLayout constraintLayout;

    @SuppressLint("CutPasteId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_kassa);

        SharedPreferences preferences = getSharedPreferences("info", MODE_PRIVATE);
        service_id = preferences.getString("service_id", "");

        btnInProcess = findViewById(R.id.btnInProcess);
        btnInFinish = findViewById(R.id.btnInFinish);
        btnToSave = findViewById(R.id.btnToSave);
        imbtnGetImage = findViewById(R.id.imbtnGetImage);

        chBMonday = findViewById(R.id.chBMonday);
        chBTuesday = findViewById(R.id.chBTuesday);
        chBWednesday = findViewById(R.id.chBWednesday);
        chBThursday = findViewById(R.id.chBThursday);
        chBFriday = findViewById(R.id.chBFriday);
        chBSaturday = findViewById(R.id.chBSaturday);
        chBSunday = findViewById(R.id.chBSunday);

        edNameOfShop = findViewById(R.id.edNameOfShop);
        edDescription = findViewById(R.id.edDescription);
        edHourFrom = findViewById(R.id.edHourFrom);
        edMinutesFrom = findViewById(R.id.edMinutesFrom);
        edHourTo = findViewById(R.id.edHourTo);
        edMinutesTo = findViewById(R.id.edHourTo);
        edCount = findViewById(R.id.edCount);

        btnInProcess.setOnClickListener(this);
        btnInFinish.setOnClickListener(this);
        btnToSave.setOnClickListener(this);
        imbtnGetImage.setOnClickListener(this);

        dialogFragment = new Dialog1();

        constraintLayout = findViewById(R.id.editCard);
        constraintLayout.setVisibility(View.INVISIBLE);

    }

    public void goBack(View view) {
        finish();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id){
            case R.id.btnInProcess:
                btnInProcess.setBackground(getDrawable(R.drawable.button_states_three));
                btnInFinish.setBackground(getDrawable(R.drawable.button_states_two));
                typeOfCard = "0";
                break;
            case R.id.btnInFinish:
                btnInProcess.setBackground(getDrawable(R.drawable.button_states_two));
                btnInFinish.setBackground(getDrawable(R.drawable.button_states_three));
                typeOfCard = "1";
                break;
            case R.id.btnToSave:
                MyThread thread = new MyThread();
                thread.start();
                break;
            case R.id.imbtnGetImage:
                Intent photoPickerIntent = new Intent(Intent.ACTION_GET_CONTENT);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, 1);
                break;
        }
    }

    private class MyThread extends Thread{
        @Override
        public void run() {
            super.run();
            workWithGUI(0);
            toSaveData();
            workWithGUI( 1);

        }
    }

    private void workWithGUI(final int i){
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                switch (i){
                    case 0:
                        constraintLayout.setVisibility(View.VISIBLE);
                        break;
                    case 1:
                        constraintLayout.setVisibility(View.INVISIBLE);
                        break;
                    case 2:
                        Toast.makeText(EditKassa.this, "Выберите файл  формате PNG",
                                Toast.LENGTH_LONG).show();
                        break;
                    case 3:
                        Toast.makeText(EditKassa.this, "Выберите файл меньшего размера",
                                Toast.LENGTH_LONG).show();
                        break;
                    case 4:
                        Toast.makeText(EditKassa.this, "Введите название заведения",
                                Toast.LENGTH_LONG).show();
                        break;
                    case 5:
                        Toast.makeText(EditKassa.this, "Введите описание",
                                Toast.LENGTH_LONG).show();
                        break;
                    case 6:
                        Toast.makeText(EditKassa.this, "Введите описание",
                                Toast.LENGTH_LONG).show();
                        break;
                    case 7:
                        Toast.makeText(EditKassa.this, "Введите корректное время работы",
                                Toast.LENGTH_LONG).show();
                        break;
                    case 8:
                        Toast.makeText(EditKassa.this, "Укажите рабочие дни",
                                Toast.LENGTH_LONG).show();
                        break;





                }
            }
        });

    }

    private void toSaveData(){
        Log.i(TAG, "onClick: btnToSave-0");
        isReady = true;
        workerDay = getWorkerDays();
        Log.i(TAG, "toSaveData: workerDay: " + workerDay);
        getData();
        if (imageIsSelected && isReady) {
            Log.i(TAG, "toSaveData: imageIsSelected && isReady");
            if (!getInputStream())
            {
                Log.i(TAG, "toSaveData: !getInputStream()");
                return;
            }
            UploadFileAsync uploadFileAsync = new UploadFileAsync();
            uploadFileAsync.execute();
            updateDB();
            return;
        }else {
            Log.i(TAG, "onClick: error-0");
        }
        if (!isAgree){
            Log.i(TAG, "toSaveData: !isAgree");
            dialogFragment.show(getFragmentManager(), "dialog1");
        }else {
            Log.i(TAG, "onClick: error-1");
        }
        if (isAgree && isReady)
        {
            Log.i(TAG, "toSaveData: isAgree && isReady");
            updateDB();
        }
    }

    private void getData(){
        if (!edNameOfShop.getText().toString().isEmpty()){
            nameOfShop = edNameOfShop.getText().toString();
        }else {
            isReady = false;
            workWithGUI(4);
            return;
        }
        if (!edDescription.getText().toString().isEmpty()){
            description = edDescription.getText().toString();
        }else {
            isReady = false;
            workWithGUI(5);
            return;
        }
        if (!edCount.getText().toString().isEmpty()){
            countOfCircle = edCount.getText().toString();
        }else {
            isReady = false;
            workWithGUI(6);
            return;
        }
        String hourFrom = edHourFrom.getText().toString(),
                minutesFrom = edMinutesFrom.getText().toString(),
                hourTo = edHourTo.getText().toString(),
                minutesTo = edMinutesTo.getText().toString();

        if (!hourFrom.isEmpty() &&
                !minutesFrom.isEmpty() &&
                !hourTo.isEmpty()
                && !minutesTo.isEmpty()
                && TextUtils.isDigitsOnly(hourFrom)
                && TextUtils.isDigitsOnly(minutesFrom)
                && TextUtils.isDigitsOnly(hourTo)
                && TextUtils.isDigitsOnly(minutesTo)
                && hourFrom.length() == 2
                && minutesFrom.length() == 2
                && hourTo.length() == 2
                && minutesTo.length() == 2){

            workerTime = hourFrom + ":" + minutesFrom + " - " + hourTo + ":" + minutesTo;

        }else {
            isReady = false;
            workWithGUI(7);
            return;
        }
    }

    private String getWorkerDays(){
        String workerDays = "", firsDay = "", lastDay = "";
        Boolean fDay = false, lDay = false, status = true, isLast = false, isAllNotisCheck = false;
        Boolean [] week = {false, false, false, false, false, false, false};

        if (chBMonday.isChecked()) week[0] = true;
        if (chBTuesday.isChecked()) week[1] = true;
        if (chBWednesday.isChecked()) week[2] = true;
        if (chBThursday.isChecked()) week[3] = true;
        if (chBFriday.isChecked()) week[4] = true;
        if (chBSaturday.isChecked()) week[5] = true;
        if (chBSunday.isChecked()) week[6] = true;



        for (int i = 0; i < 7; i++){
            if (week[i]){
                isAllNotisCheck = true;
                break;
            }
        }

        if (!isAllNotisCheck){
            workWithGUI(8);
            isReady = false;
            return "";
        }

        for (int i = 0; i < 7; i++){
            if (!week[i]){
                status = false;
                break;
            }
        }

        if (status) return "Ежедневно";

        for (int i = 0; i < 7; i++){
            Log.i(TAG, "getWorkerDays: [" + i + "] = " + week[i]);
            if (!fDay && week[i]){
                Log.i(TAG, "getWorkerDays: sds");
                firsDay = getDay(i);
                if (isLast) workerDays += ", ";
                isLast = false;
                fDay = true;
                if (i < 6 && !week[i+1]){
                    if (i+1 == 6){
                        workerDays +=firsDay;
                        fDay = false;
                    }else {
                        workerDays +=firsDay + ",";
                        fDay = false;
                    }

                }
            }
            if (fDay && week[i]){
                if (i < 6 && week[i+1]) continue;
                lastDay += getDay(i);
                lDay = true;
            }
            if (fDay && lDay) {
                workerDays += firsDay + "-" + lastDay;
                fDay = false;
                lDay = false;
                firsDay = "";
                lastDay = "";
                isLast = true;
            }

        }

        return workerDays;
    }

    private String getDay(int i){
        switch (i){
            case 0:
                return "ПН";
            case 1:
                return "ВТ";
            case 2:
                return "СР";
            case 3:
                return "ЧТ";
            case 4:
                return "ПТ";
            case 5:
                return "СБ";
            case 6:
                return "ВС";

        }
        return "";
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.i(TAG, "requestCode: " + requestCode);

        switch(requestCode)
        {
            case 1:
            {
                if (resultCode == RESULT_OK)
                {
                    chosenImageUri = data.getData();
                    final Cursor cursor = getContentResolver().query( chosenImageUri,
                            null, null, null, null );
                    assert cursor != null;
                    cursor.moveToFirst();
                    filePath = cursor.getString(0);
                    cursor.close();
                    puth = chosenImageUri.getEncodedPath();
                    Log.i(TAG, "uri1: " + puth);
                    imbtnGetImage.setImageURI(chosenImageUri);
                    imageIsSelected = true;
                }
                break;
            }
        }

    }



    private void updateDB(){

        try {

            String SERVER_NAME = "http://r2551241.beget.tech";
            input = SERVER_NAME
                    + "/gosteekassa.php?action=updateCard&nameOfShop="
                    + URLEncoder.encode(nameOfShop, "UTF-8")
                    + "&description="
                    + URLEncoder.encode(description, "UTF-8")
                    + "&workerDay="
                    + URLEncoder.encode(workerDay, "UTF-8")
                    + "&workerTime="
                    + URLEncoder.encode(workerTime, "UTF-8")
                    + "&typeOfCard="
                    + URLEncoder.encode(typeOfCard, "UTF-8")
                    + "&countOfCircle="
                    + URLEncoder.encode(countOfCircle, "UTF-8")
                    + "&service_id="
                    + URLEncoder.encode(service_id, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        ConnDB connDB = new ConnDB();
        String ansver = connDB.sendRequest(input, this);
        Log.i(TAG, "updateDB: ansver: " +ansver);

    }

    private Boolean getInputStream(){
        String[] projection = {MediaStore.MediaColumns.DISPLAY_NAME};
        ContentResolver cr = getApplicationContext().getContentResolver();
        Cursor metaCursor;
        try {
            metaCursor = cr.query(chosenImageUri, projection, null, null, null);
        }catch (NullPointerException e){
            e.printStackTrace();
            Log.i(TAG, "inputStream: файл не выбран");

            return false;
        }

        if (metaCursor != null) {
            try {
                if (metaCursor.moveToFirst()) {
                    //name = metaCursor.getString(0);
                    name = service_id + ".png";
                    Log.i(TAG, "Имя файла: " + name);
                    String [] strings = name.split("\\.");
                    if (!strings[1].equals("png")) {
                        Log.i(TAG, "Выберите файл  формате PNG");
                        workWithGUI(2);
                        return false;
                    }

                }
            } finally {
                metaCursor.close();
            }
        }

        try {
            assert chosenImageUri != null;
            inputStream = cr.openInputStream(chosenImageUri);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.i(TAG, "inputStream: файл не найден");
        }



        try {
            if (inputStream.available() >320000 ) {
                Log.i(TAG, "inputStream: Выберите файл меньшего размера ");
                workWithGUI(3);
                return false;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return true;
    }

    @Override
    public void onFinishButtonDialog(Boolean _isAgree) {
        isAgree = _isAgree;
    }

    private class UploadFileAsync extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

            Log.i(TAG, "doInBackground: Загрузка файла на сервер...");
            try {
                String sourceFileUri = name;


                HttpURLConnection conn = null;
                DataOutputStream dos = null;
                String lineEnd = "\r\n";
                String twoHyphens = "--";
                String boundary = "*****";
                int bytesRead, bytesAvailable, bufferSize;
                byte[] buffer;
                int maxBufferSize = 1 * 1024 * 1024;
//File sourceFile = new File(sourceFileUri);
                Log.i(TAG, "doInBackground: Файл загружается...");

                try {
                    String upLoadServerUri = "http://r2551241.beget.tech/gosteeUploadImages.php?";

// open a URL connection to the Servlet
//FileInputStream fileInputStream = inputStream;
                    URL url = new URL(upLoadServerUri);

// Open a HTTP connection to the URL
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setDoInput(true); // Allow Inputs
                    conn.setDoOutput(true); // Allow Outputs
                    conn.setUseCaches(false); // Don't use a Cached Copy
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Connection", "Keep-Alive");
                    conn.setRequestProperty("ENCTYPE",
                            "multipart/form-data");
                    conn.setRequestProperty("User-Agent","Mozilla/5.0 (Windows NT 6.2) " +
                            "AppleWebKit/536.6 (KHTML, like Gecko) " +
                            "Chrome/20.0.1090.0 Safari/536.6')");


                    conn.setRequestProperty("Content-Type",
                            "multipart/form-data;boundary=" + boundary);
                    conn.setRequestProperty("bill", sourceFileUri);

                    dos = new DataOutputStream(conn.getOutputStream());

                    dos.writeBytes(twoHyphens + boundary + lineEnd);
                    dos.writeBytes("Content-Disposition: form-data; name=\"bill\";filename=\""
                            + sourceFileUri + "\"" + lineEnd);
                    Log.i(TAG, "Content-Disposition: form-data; " +
                            "name=\"bill\"; filename=\"" + name + "\" lineEnd:" + lineEnd);
                    dos.writeBytes(lineEnd);

// create a buffer of maximum size
                    bytesAvailable = inputStream.available();

                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    buffer = new byte[bufferSize];

// read file and write it into form...
                    bytesRead = inputStream.read(buffer, 0, bufferSize);
                    Log.i(TAG, "doInBackground: bytesRead = " + bytesRead);
                    while (bytesRead > 0) {

                        Log.i(TAG, "doInBackground: Отправка данных...");

                        dos.write(buffer, 0, bufferSize);
                        bytesAvailable = inputStream.available();
                        bufferSize = Math
                                .min(bytesAvailable, maxBufferSize);
                        bytesRead = inputStream.read(buffer, 0,
                                bufferSize);

                    }

// send multipart form data necesssary after file
// data...
                    dos.writeBytes(lineEnd);
                    dos.writeBytes(twoHyphens + boundary + twoHyphens
                            + lineEnd);

// Responses from the server (code and message)
                    int serverResponseCode = conn.getResponseCode();
                    Log.i(TAG, "serverResponseCode: " + serverResponseCode);
                    String serverResponseMessage = conn.getResponseMessage();
                    Log.i(TAG, "serverResponseMessage: " + serverResponseMessage);

                    if (serverResponseCode == 200) {

                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(EditKassa.this, "File Upload Complete.",
                                        Toast.LENGTH_LONG).show();
                            }
                        });




                    }

// close the streams //
                    inputStream.close();
                    dos.flush();
                    dos.close();

                } catch (Exception e) {

// dialog.dismiss();
                    e.printStackTrace();

                }
// dialog.dismiss();

// End else block


            } catch (Exception ex) {
// dialog.dismiss();

                ex.printStackTrace();
            }
            return "Executed";
        }

        @Override
        protected void onPostExecute(String result) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(EditKassa.this, "Готово",
                            Toast.LENGTH_LONG).show();
                }
            });

        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }
}

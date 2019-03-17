package com.example.genia.gosteekassa.Controllers;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.genia.gosteekassa.R;

import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class EditKassa extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = "EditKassa";
    private Button btnInProcess, btnInFinish, btnToSave;
    private ImageButton imbtnGetImage;
    private InputStream inputStream;
    private String name, filePath, puth;
    private CheckBox chBMonday, chBTuesday, chBWednesday, chBThursday, chBFriday, chBSaturday,
            chBSunday;
    private EditText edHourFrom, edMinutesFrom, edHourTo, edMinutesTo;

    @SuppressLint("CutPasteId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_kassa);

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

        edHourFrom = findViewById(R.id.edHourFrom);
        edMinutesFrom = findViewById(R.id.edMinutesFrom);
        edHourTo = findViewById(R.id.edHourTo);
        edMinutesTo = findViewById(R.id.edHourTo);

        btnInProcess.setOnClickListener(this);
        btnInFinish.setOnClickListener(this);
        btnToSave.setOnClickListener(this);
        imbtnGetImage.setOnClickListener(this);

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

                break;
            case R.id.btnInFinish:
                btnInProcess.setBackground(getDrawable(R.drawable.button_states_two));
                btnInFinish.setBackground(getDrawable(R.drawable.button_states_three));
                break;
            case R.id.btnToSave:
                break;
            case R.id.imbtnGetImage:
                Intent photoPickerIntent = new Intent(Intent.ACTION_GET_CONTENT);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, 1);
                break;
        }
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

                    Uri chosenImageUri = data.getData();

                    String[] projection = {MediaStore.MediaColumns.DISPLAY_NAME};
                    ContentResolver cr = getApplicationContext().getContentResolver();

                    Cursor metaCursor = cr.query(chosenImageUri, projection, null, null, null);
                    if (metaCursor != null) {
                        try {
                            if (metaCursor.moveToFirst()) {
                                name = metaCursor.getString(0);
                                Log.i(TAG, "Имя файла: " + name);
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
                        Log.i(TAG, "inputStream: файл не найден.");
                    }

//Log.i("MainActivity", "getRealPathFromURI: " + getRealPathFromURI(this, chosenImageUri));


                    final Cursor cursor = getContentResolver().query( chosenImageUri,
                            null, null, null, null );
                    assert cursor != null;
                    cursor.moveToFirst();
                    filePath = cursor.getString(0);
                    cursor.close();
                    puth = chosenImageUri.getEncodedPath();
                    Log.i(TAG, "uri1: " + puth);
                    imbtnGetImage.setImageURI(chosenImageUri);
                }
                break;
            }
        }
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
                    String serverResponseMessage = conn
                            .getResponseMessage();
                    Log.i(TAG, "serverResponseMessage: " + serverResponseMessage);

                    if (serverResponseCode == 200) {


                        Toast.makeText(getApplicationContext(), "File Upload Complete.",
                                Toast.LENGTH_SHORT).show();

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
            for (int i = 0; i < 3; i++) {
                Log.i(TAG, "onPostExecute: " + i);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }
}

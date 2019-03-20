package com.example.genia.gosteekassa.ConnToDB;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;

public class ConnDB {

    private String  ansver  = "";
    private HttpURLConnection conn;

    public String sendRequest(String input, final Context context){
        try {

            Log.i("ConnDB",
                    "sendRequest: Send request on the server "
                            + input);
            URL url = new URL(input);
            conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("User-Agent", "Mozilla/5.0");
            conn.setDoInput(true);
            conn.connect();
            Integer res = conn.getResponseCode();
            Log.i("ConnDB", "sendRequest: Answer from server (200 = ОК): "
                    + res.toString());

        } catch (UnknownHostException e){
            Log.i("ConnDB",
                    "Нет подключения к сети");
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override public void run() {
                    Toast.makeText(context,
                            "Нет подключения к сети", Toast.LENGTH_SHORT)
                            .show();
                }
            });
        } catch (Exception e ) {
            Log.i("ConnDB",
                    "sendRequest: Answer from server ERROR: "
                            + e.getMessage());
            e.printStackTrace();
        }
        try {
            InputStream is = conn.getInputStream();
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(is, "UTF-8"));
            StringBuilder sb = new StringBuilder();
            String bfr_st = null;
            while ((bfr_st = br.readLine()) != null) {
                sb.append(bfr_st);
            }
            Log.i("ConnDB", "sendRequest: Full answer from server: "
                    + sb.toString());
            ansver = sb.toString();
            ansver = ansver.substring(ansver.indexOf("[") + 1, ansver.indexOf("]"));

            Log.i("ConnDB", "sendRequest: Answer: " + ansver);

            is.close();
            br.close();
        }
        catch (Exception e) {
            Log.i("sendRequest", "sendRequest: Error: " + e.getMessage());
        }
        finally {
            conn.disconnect();
        }

        return ansver;
    }
}

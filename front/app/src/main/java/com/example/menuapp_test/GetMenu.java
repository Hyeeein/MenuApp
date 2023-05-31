package com.example.menuapp_test;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class GetMenu extends AsyncTask<String, Void, String> {
    ProgressDialog progressDialog;
    Context context;
    GetMenu(Context context){
        this.context = context;
    }
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog = ProgressDialog.show(context, "Please Wait", null, true, true);
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        progressDialog.dismiss();
        Log.d("GetMenu", "GET response - " + result);
    }

    @Override
    protected String doInBackground(String... params) {
        String serverURL = params[0];
        String token = params[1];

        try {
            URL url = new URL(serverURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setReadTimeout(5000);
            conn.setConnectTimeout(5000);
            conn.setRequestProperty("Authorization", "TOKEN " + token);
            conn.connect();

            int responseStatusCode = conn.getResponseCode();
            Log.d("GetMenu", "GET response code : " + responseStatusCode);

            InputStream inputStream;
            if(responseStatusCode == conn.HTTP_OK || responseStatusCode == 201){         // 연결 성공 시
                inputStream = conn.getInputStream();
            }
            else {                                          // 연결 실패 시
                inputStream = conn.getErrorStream();
            }
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            StringBuilder sb = new StringBuilder();
            String line;

            while((line = bufferedReader.readLine()) != null){
                sb.append(line);
            }

            bufferedReader.close();

            return sb.toString().trim();
        }
        catch (Exception e) {
            Log.d("GetMenu", "GetData : Error ", e);
            return new String("Error: " + e.getMessage());
        }
    }
}

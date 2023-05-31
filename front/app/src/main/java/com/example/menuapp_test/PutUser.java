package com.example.menuapp_test;


import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class PutUser extends AsyncTask<String, Void, String> {
    ProgressDialog progressDialog;
    Context context;
    PutUser(Context context){
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
        Log.d("PutUser", "PUT response - " + result);
    }

    @Override
    protected String doInBackground(String... params) {
        String Nickname = params[1];
        String Introduction = params[2];
        String Token = params[3];

        String serverURL = params[0];

        try {
            URL url = new URL(serverURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setReadTimeout(5000);
            conn.setConnectTimeout(5000);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Authorization", "TOKEN " + Token);
            conn.setRequestMethod("PUT");
            conn.connect();

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("nickname", Nickname);
            jsonObject.put("introduction", Introduction);

            OutputStream outputStream = conn.getOutputStream();
            outputStream.write(jsonObject.toString().getBytes());
            outputStream.flush();
            outputStream.close();

            int responseStatusCode = conn.getResponseCode();
            Log.d("PutUser", "PUT response code - " + responseStatusCode);

            InputStream inputStream;
            if (responseStatusCode == conn.HTTP_OK || responseStatusCode == 201) {
                inputStream = conn.getInputStream();
            }
            else {
                inputStream = conn.getErrorStream();
            }

            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            StringBuilder sb = new StringBuilder();
            String line = null;

            while((line = bufferedReader.readLine())!= null)  {
                sb.append(line);
            }

            bufferedReader.close();

            return sb.toString();
        }
        catch (Exception e) {
            Log.d("PutUser", "InsertData : Error ", e);
            return new String("Error: " + e.getMessage());
        }
    }
}
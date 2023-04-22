package com.example.menuapp_test;


import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class GetRecommend extends AsyncTask<String, Void, String> {
    ProgressDialog progressDialog;
    Context context;
    GetRecommend(Context context){
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
        Log.d("PostLogin", "POST response - " + result);
    }

    @Override
    protected String doInBackground(String... params) {
        String Price = params[1];
        String Weather = params[2];
        String Emotion = params[3];
        String Token = params[4];

        String serverURL = params[0];
        String postParameters = "price=" + Price + "&weather=" + Weather + "&emotion=" + Emotion;

        try {
            URL url = new URL(serverURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setReadTimeout(5000);
            conn.setConnectTimeout(5000);
            conn.setRequestProperty("Authorization", "TOKEN " + Token);
            conn.setRequestMethod("POST");
            conn.connect();

            OutputStream outputStream = conn.getOutputStream();
            outputStream.write(postParameters.getBytes("UTF-8"));
            outputStream.flush();
            outputStream.close();

            int responseStatusCode = conn.getResponseCode();
            Log.d("PostLogin", "POST response code - " + responseStatusCode);

            InputStream inputStream;
            if (responseStatusCode == conn.HTTP_OK) {
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
            Log.d("PostLogin", "InsertData : Error ", e);
            return new String("Error: " + e.getMessage());
        }
    }
}

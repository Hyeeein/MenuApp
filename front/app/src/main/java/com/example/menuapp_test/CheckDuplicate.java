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

public class CheckDuplicate extends AsyncTask<String, Void, String> {
    ProgressDialog progressDialog;
    Context context;

    CheckDuplicate(Context context){ this.context = context; }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog = ProgressDialog.show(context, "Please Wait", null, true, true);
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        progressDialog.dismiss();
        Log.d("CheckDuplicate", "POST response - " + result);
    }

    @Override
    protected String doInBackground(String... params) {
        String data = params[1];
        String type = params[2];

        String serverURL = params[0];
        String postParameters = "";

        if(type == "e")
            postParameters = "email=" + data ;
        else postParameters = "nickname=" + data;

        try {
            URL url = new URL(serverURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setReadTimeout(5000);
            conn.setConnectTimeout(5000);
            conn.setRequestMethod("POST");
            conn.connect();

            OutputStream outputStream = conn.getOutputStream();
            outputStream.write(postParameters.getBytes("UTF-8"));
            outputStream.flush();
            outputStream.close();

            int responseStatusCode = conn.getResponseCode();
            Log.d("CheckDuplicate", "POST response code - " + responseStatusCode);

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
            Log.d("CheckDuplicate", "CheckDuplicate : Error ", e);
            return new String("Error: " + e.getMessage());
        }
    }
}
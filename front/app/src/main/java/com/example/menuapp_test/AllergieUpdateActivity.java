package com.example.menuapp_test;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;


public class AllergieUpdateActivity extends AppCompatActivity {
    private static String ADDRESS_PUT = "http://52.78.72.175/data/allergy";
    private CheckBox egg, milk, wheat, bean, peanut, fish, meat, shellfish, crab;
    private String e, m, w, b, p, f, me, s, c;
    private boolean scheck;
    private Button save, end;
    private TextView txt_result;
    private String token;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_allergie_update);

        egg = (CheckBox) findViewById(R.id.chk_egg);
        milk = (CheckBox) findViewById(R.id.chk_milk);
        wheat = (CheckBox) findViewById(R.id.chk_wheat);
        bean = (CheckBox) findViewById(R.id.chk_bean);
        peanut = (CheckBox) findViewById(R.id.chk_peanut);
        fish = (CheckBox) findViewById(R.id.chk_fish);
        meat = (CheckBox) findViewById(R.id.chk_meat);
        shellfish = (CheckBox) findViewById(R.id.chk_shellfish);
        crab = (CheckBox) findViewById(R.id.chk_crab);

        end = findViewById(R.id.btn_end_update);
        txt_result = findViewById(R.id.txt_result);

        scheck = false;

        end.setOnClickListener(view -> {
            Intent getintent = getIntent();
            //token = getintent.getStringExtra("token");
            token = "49e9d8db7d6d31d3623b4af2d3fb97178d6d773e";

            e = "0"; m = "0"; w = "0"; b = "0"; p = "0"; f = "0"; me = "0"; s = "0"; c = "0";

            sendAllergie(egg, milk, wheat, bean, peanut, fish, meat, shellfish, crab);
            scheck = true;

            Toast.makeText(getApplicationContext(), e+m+w+b+p+f+me+s+c, Toast.LENGTH_LONG).show();
            InsertAllergie insertAllergie = new InsertAllergie();
            insertAllergie.execute(ADDRESS_PUT, e, m, w, b, p, f, me, s, c, token);

            Intent intent = new Intent(AllergieUpdateActivity.this, SettingActivity.class);
            intent.putExtra("token", token);
            startActivity(intent);
        });
    }

    private void sendAllergie(CheckBox egg, CheckBox milk, CheckBox wheat, CheckBox bean, CheckBox peanut,
                              CheckBox fish, CheckBox meat, CheckBox shellfish, CheckBox crab){

        if(egg.isChecked()){
            e = "1";
        }
        if(milk.isChecked()){
            m = "1";
        }
        if(wheat.isChecked()){
            w = "1";
        }
        if(bean.isChecked()){
            b = "1";
        }
        if(peanut.isChecked()){
            p = "1";
        }
        if(fish.isChecked()){
            f = "1";
        }
        if(meat.isChecked()){
            me = "1";
        }
        if(shellfish.isChecked()){
            s = "1";
        }
        if(crab.isChecked()){
            c = "1";
        }
    }

    class InsertAllergie extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(AllergieUpdateActivity.this, "Please Wait", null, true, true);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            progressDialog.dismiss();
            txt_result.setText(result);
            Log.d("PutAllergie", "POST response - " + result);
        }

        @Override
        protected String doInBackground(String ... params) {
            String Egg = params[1];
            String Milk = params[2];
            String Wheat = params[3];
            String Bean = params[4];
            String Peanut = params[5];
            String Fish = params[6];
            String Meat = params[7];
            String Shellfish = params[8];
            String Crustaceans = params[9];
            String Token = params[10];

            String serverURL = params[0];

            try {
                URL url = new URL(serverURL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                conn.setReadTimeout(5000);
                conn.setConnectTimeout(5000);
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Authorization", "TOKEN " + Token);
                conn.setRequestMethod("PUT");

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("달걀", Egg);
                jsonObject.put("우유", Milk);
                jsonObject.put("밀", Wheat);
                jsonObject.put("콩", Bean);
                jsonObject.put("땅콩", Peanut);
                jsonObject.put("생선", Fish);
                jsonObject.put("고기", Meat);
                jsonObject.put("조개", Shellfish);
                jsonObject.put("갑각류", Crustaceans);

                OutputStream outputStream = conn.getOutputStream();
                outputStream.write(jsonObject.toString().getBytes());
                outputStream.flush();
                outputStream.close();

                int responseStatusCode = conn.getResponseCode();
                Log.d("PutAllergie", "POST response code - " + responseStatusCode);

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
                Log.d("PutAllergie", "InsertData : Error ", e);
                return new String("Error: " + e.getMessage());
            }
        }
    }
}
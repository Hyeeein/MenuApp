package com.example.menuapp_test;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ReviewShowActivity extends AppCompatActivity {
    private static String ADDRESS_REVIEW = "http://52.78.72.175/data/restaurant/";
    private ListView listView;
    private String token, rid, rname, JsonString;
    private ReviewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_show);

        listView = findViewById(R.id.listv_review_show);

        Intent getIntent = getIntent();
        //token = getIntent.getStringExtra("token");
        token = "49e9d8db7d6d31d3623b4af2d3fb97178d6d773e";
        //rid = getIntent.getStringExtra("Rid");
        rid = "1";
        rname = getIntent.getStringExtra("Rname");

        GetData getData = new GetData();
        getData.execute(ADDRESS_REVIEW + rid + "/review", token);
    }


    private class GetData extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;
        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            progressDialog = ProgressDialog.show(ReviewShowActivity.this, "Please Wait", null, true, true);
        }

        @Override
        protected void onPostExecute(String result){
            super.onPostExecute(result);
            progressDialog.dismiss();
            Log.d("GetReview", "response : " + result);

            JsonString = result;               // 서버의 데이터를 문자열로 읽어와서 변수에 저장
            showResult();

        }

        @Override
        protected String doInBackground(String... params){
            String serverURL = params[0];
            String Token = params[1];

            try {
                URL url = new URL(serverURL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                conn.setReadTimeout(5000);
                conn.setConnectTimeout(5000);
                conn.setRequestProperty("Authorization", "TOKEN " + Token);
                conn.connect();

                int responseStatusCode = conn.getResponseCode();
                Log.d("GetReview", "response code : " + responseStatusCode);

                InputStream inputStream;
                if(responseStatusCode == conn.HTTP_OK){         // 연결 성공 시
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
                conn.disconnect();

                return sb.toString().trim();

            }
            catch (Exception e){
                e.printStackTrace();
                return null;
            }

        }
    }

    private void showResult() {
        try {
            JSONArray jsonArray = new JSONArray(JsonString);       // 전체 데이터를 배열에 저장
            adapter = new ReviewAdapter();

            for (int i = 0; i < jsonArray.length(); i++) {                // 한 그룹{} 씩 읽음
                JSONObject item = jsonArray.getJSONObject(i);       // 해당 그룹의 데이터 하나씩 읽어서 각각의 변수에 저장
                int id = Integer.parseInt(item.getString("id"));
                String content = item.getString("content");
                String datetime = item.getString("datetime");

                JSONObject user = (JSONObject) item.get("user");
                String nickname = user.getString("nickname");

                JSONObject menu = (JSONObject) item.get("menu");
                String menuname = menu.getString("name");

                String image = ""; float rating = 0;
                if(!item.getString("image").equals("null"))
                    image = item.getString("image");
                else image = "null";
                if(!item.getString("rating").equals("null"))
                    rating = Float.parseFloat(item.getString("rating"));
                else rating = 0;

                adapter.addReviewItem(id, rating, content, datetime, nickname, menuname, "http://52.78.72.175" + image);
            }

            listView.setAdapter(adapter);

        } catch (JSONException e) {
            Log.d("GetReview", "showResult : ", e);
        }
    }
}

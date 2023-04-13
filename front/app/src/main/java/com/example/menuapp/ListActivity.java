package com.example.menuapp;

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
import java.util.ArrayList;

public class ListActivity extends AppCompatActivity {
    private static String ADDRESS = "http://52.78.72.175/data/restaurant";
    private static String TAG = "data";
    private static final String TAG_NAME = "name";
    private static final String TAG_ADDRESS = "address";
    private static final String TAG_CATEGORY_NAME = "category_name";
    private static final String TAG_IMAGE = "image";
    private ListView mlistView;
    //private ArrayList<HashMap<String, String>> listItems;
    private ArrayList<ListItem> listItems;
    private RAdapter adapter;
    private String token, mJsonString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        Intent getIntent = getIntent();
        token = getIntent.getStringExtra("token");

        mlistView = (ListView) findViewById(R.id.listv_list);
        listItems = new ArrayList<>();

        GetData task = new GetData();
        task.execute(ADDRESS, token);

    }

    private class GetData extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;
        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            progressDialog = ProgressDialog.show(ListActivity.this, "Please Wait", null, true, true);
        }

        @Override
        protected void onPostExecute(String result){
            super.onPostExecute(result);
            progressDialog.dismiss();
            Log.d(TAG, "response : " + result);

            mJsonString = result;               // 서버의 데이터를 문자열로 읽어와서 변수에 저장
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
                Log.d(TAG, "response code : " + responseStatusCode);

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
            JSONArray jsonArray = new JSONArray(mJsonString);       // 전체 데이터를 배열에 저장
            /*JSONObject item = jsonArray.getJSONObject(i);       // 해당 그룹의 데이터 하나씩 읽어서 각각의 변수에 저장
            int id = Integer.parseInt(item.getString("id"));
            String name = item.getString(TAG_NAME);
            String address = item.getString(TAG_ADDRESS);
            String category_name = item.getString(TAG_CATEGORY_NAME);
            String image = item.getString(TAG_IMAGE);*/
            adapter = new RAdapter();
            //adapter.addRItem(id, name, address, category_name, "http://52.78.72.175" + image);

            for (int i = 0; i < jsonArray.length(); i++) {                // 한 그룹{} 씩 읽음
                JSONObject item = jsonArray.getJSONObject(i);       // 해당 그룹의 데이터 하나씩 읽어서 각각의 변수에 저장
                int id = Integer.parseInt(item.getString("id"));
                String name = item.getString(TAG_NAME);
                String address = item.getString(TAG_ADDRESS);
                String category_name = item.getString(TAG_CATEGORY_NAME);
                String image = item.getString(TAG_IMAGE);
                adapter.addRItem(id, name, address, category_name, "http://52.78.72.175" + image);
            }

            mlistView.setAdapter(adapter);

                /*HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put(TAG_NAME, name);
                hashMap.put(TAG_ADDRESS, address);
                hashMap.put(TAG_CATEGORY_NAME, category_name);
                hashMap.put(TAG_IMAGE, "http://52.78.72.175"+image);                      // hashmap에 짝 지어 넣음

                listItems.add(hashMap);                             // 데이터 저장된 최종 변수
                ListAdapter adapter = new SimpleAdapter(                // 이미지 불러오기 위해 어댑터 클래스 새로 만들어야 함
                    ListActivity.this, listItems, R.layout.item_list,
                    new String[]{TAG_NAME, TAG_ADDRESS, TAG_CATEGORY_NAME, TAG_IMAGE},
                    new int[]{R.id.rname_list, R.id.address_item_list, R.id.category_list, R.id.img_list}
                );
                mlistView.setAdapter(adapter);
                 */


        } catch (JSONException e) {
            Log.d(TAG, "showResult : ", e);
        }
    }
}
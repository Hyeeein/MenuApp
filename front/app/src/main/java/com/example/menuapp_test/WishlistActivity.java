package com.example.menuapp_test;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class WishlistActivity extends AppCompatActivity {
    private static String ADDRESS_LIST = "http://52.78.72.175/data/restaurant";
    private static String ADDRESS_WISH = "http://52.78.72.175/data/favorite";
    private static String TAG = "Wishlist";
    private static final String TAG_NAME = "name";
    private static final String TAG_ADDRESS = "address";
    private static String TAG_BUSINESS = "business_hours";
    private static String TAG_PHONE = "phone_number";
    private static final String TAG_CATEGORY_NAME = "category_name";
    private static final String TAG_IMAGE = "image";
    private static String TAG_RATING = "rating";
    private ListView mlistView;
    private WishlistAdapter adapter;
    private String token, mJsonString, rid;
    private boolean Wish;
    private FloatingActionButton home;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wishlist);

        Intent getIntent = getIntent();
        token = getIntent.getStringExtra("token");

        mlistView = (ListView) findViewById(R.id.listv_wishlist);
        home = findViewById(R.id.fab);

        home.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("token", token);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        GetData task = new GetData();
        task.execute(ADDRESS_LIST, token);
        mlistView.setOnItemClickListener((adapterView, view, i, l) -> {
            ListItem item = (ListItem) adapter.getItem(i);
            String Rid = String.valueOf(item.getId());
            Intent intent = new Intent(this, RestaurantActivity.class);
            intent.putExtra("token", token);
            intent.putExtra("Rid", Rid);
            startActivity(intent);
        });

    }

    private class GetData extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;
        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            progressDialog = ProgressDialog.show(WishlistActivity.this, "Please Wait", null, true, true);
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
            JSONArray jsonArray = new JSONArray(mJsonString);       // 전체 데이터를 배열에 저장
            adapter = new WishlistAdapter();

            for(int i=0; i<jsonArray.length(); i++){
                JSONObject item = jsonArray.getJSONObject(i);
                if(item.getBoolean("favor")){
                    int id = Integer.parseInt(item.getString("id"));
                    String name = item.getString(TAG_NAME);
                    String address = item.getString(TAG_ADDRESS);
                    String image = ""; String rating = "0";
                    if(!item.getString(TAG_IMAGE).equals("null"))
                        image = item.getString(TAG_IMAGE);
                    else image = "null";
                    if(!item.getString(TAG_RATING).equals("null"))
                        rating = item.getString(TAG_RATING);
                    else rating = "0";
                    //int distance = item.getInt("distance");
                    int distance = 70;
                    boolean wish = Boolean.parseBoolean(item.getString("favor"));
                    adapter.addWItem(id, name, address, image, rating, distance, wish);
                }
            }
            mlistView.setAdapter(adapter);
        } catch (JSONException e) {
            Log.d(TAG, "showResult : ", e);
        }
    }
    class WishlistAdapter extends BaseAdapter {
        private ArrayList<ListItem> listItems = new ArrayList<ListItem>();
        private Bitmap bitmap;
        public WishlistAdapter(){
        }

        @Override
        public int getCount(){
            return listItems.size();
        }
        @Override
        public long getItemId(int position) {
            return position;
        }
        @Override
        public Object getItem(int position) {
            return listItems.get(position);
        }
        @Override
        public View getView(int position, View view, ViewGroup parent){
            Context context = parent.getContext();

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.item_wishlist, parent, false);

            ImageView imageView = (ImageView) view.findViewById(R.id.img_wishlist);
            TextView name = (TextView) view.findViewById(R.id.rname_wishlist);
            TextView distance = view.findViewById(R.id.distance_wishlist);
            CheckBox wish = view.findViewById(R.id.imgbtn_wishlist);

            ListItem listItem = listItems.get(position);

            name.setText(listItem.getName());
            distance.setText(String.valueOf(listItem.getAddress()));

            if(!listItem.getImage().equals("null")){
                Thread thread = new Thread() {
                    @Override
                    public void run(){
                        try {
                            URL url = new URL(listItem.getImage());
                            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                            conn.setDoInput(true);
                            conn.connect();

                            InputStream is = conn.getInputStream();
                            bitmap = BitmapFactory.decodeStream(is);
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                };
                thread.start();
                try {
                    thread.join();
                    imageView.setImageBitmap(bitmap);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            // 이미 찜한 음식점일 경우
            if(listItem.getWish()) wish.setChecked(true);
            // 찜하기 기능
            wish.setOnClickListener(v -> {
                String Rid = String.valueOf(listItem.getId());
                PostWish postWish = new PostWish(WishlistActivity.this);
                postWish.execute(ADDRESS_WISH, Rid, token);

                listItems.remove(position);
                mlistView.clearChoices();
                adapter.notifyDataSetChanged();
            });
            return view;
        }

        void addWItem(int id, String name, String address, String image, String rating, int distance, boolean wish){
            ListItem item = new ListItem();
            item.setId(id);
            item.setName(name);
            item.setAddress(address);
            item.setImage(image);
            item.setRating(rating);
            item.setDistance(distance);
            item.setWish(wish);

            listItems.add(item);
        }
    }
}

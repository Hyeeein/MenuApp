package com.example.menuapp_test;


import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
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
import java.util.concurrent.ExecutionException;

public class RestaurantActivity extends AppCompatActivity {
    private static String ADDRESS_RESTAURANT = "http://52.78.72.175/data/restaurant";
    private static String ADDRESS_WISH = "http://52.78.72.175/data/favorite";
    private ImageView restaurant;
    private ListView listView;
    private MenuItem menuItems;
    private MAdapter adapter;
    private CheckBox wish;
    private TextView name, category, phone, address, time, rating, review, count;
    private String token, rid, rname, rJsonString, mJsonstring;
    private ListItem listItem = new ListItem();
    private Bitmap bitmap;
    private FloatingActionButton home;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant);

        Intent getIntent = getIntent();
        token = getIntent.getStringExtra("token");
        rid = getIntent.getStringExtra("Rid");
        listItem = (ListItem) getIntent.getSerializableExtra("listItem");

        restaurant = findViewById(R.id.img_restaurant);
        wish = findViewById(R.id.imgbtn_restaurant);
        name = findViewById(R.id.rname_restaurant);
        category = findViewById(R.id.category_restaurant);
        phone = findViewById(R.id.rphone_restaurant);
        address = findViewById(R.id.address_restaurant);
        time = findViewById(R.id.time_restaurant);
        rating = findViewById(R.id.rating_restaurant);
        review = findViewById(R.id.review_restaurant);
        listView = findViewById(R.id.listv_menulist);
        count = findViewById(R.id.count_restaurant);
        home = findViewById(R.id.fab);

        home.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("token", token);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        /*GetRestaurant getRestaurant = new GetRestaurant();
        getRestaurant.execute(ADDRESS_RESTAURANT, token);
        try {
            rJsonString = getRestaurant.get();
            int Resid = Integer.parseInt(rid) - 1;
            JSONArray jsonArray = new JSONArray(rJsonString);
            JSONObject item = jsonArray.getJSONObject(Resid);
            int id = Integer.parseInt(item.getString("id"));
            String name = item.getString("name");
            String address = item.getString("address");
            String business = item.getString("business_hours");
            String phone = item.getString("phone_number");
            String category_name = item.getString("category_name");
            String image = ""; double rating = 0;
            if(!item.getString("image").equals("null"))
                image = item.getString("image");
            else image = "null";
            if(!item.getString("rating").equals("null"))
                rating = item.getDouble("rating");
            else rating = 0;
            String Rating = String.format("%.1f", rating);
            //String distance = item.getString("distance");
            boolean Wish = Boolean.parseBoolean(item.getString("favor"));
            //String distance = "70";
            String Count = item.getString("count");

            listItem.setId(id); listItem.setName(name); listItem.setBusiness_hours(business);
            listItem.setPhone_number(phone); listItem.setCategory_name(category_name); listItem.setImage(image);
            listItem.setWish(Wish); listItem.setRating(Rating); listItem.setAddress(address); listItem.setCount(Count);
        } catch (JSONException e) {
            Log.d("Restaurant", "showResult : ", e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }*/

        if(listItem.getWish()) wish.setChecked(true);

        wish.setOnClickListener(v -> {
            String Rid = String.valueOf(listItem.getId());
            PostWish postWish = new PostWish(RestaurantActivity.this);
            postWish.execute(ADDRESS_WISH, Rid, token);
        });

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
                restaurant.setImageBitmap(bitmap);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        rname = listItem.getName();
        name.setText(rname);
        category.setText(listItem.getCategory_name());
        if(listItem.getPhone_number().equals("null")) { phone.setText("-"); }
        else { phone.setText(listItem.getPhone_number()); }
        address.setText(listItem.getAddress());
        time.setText(listItem.getBusiness_hours());
        rating.setText(listItem.getRating());
        count.setText(listItem.getCount());

        review.setOnClickListener(v -> {
            Intent intent = new Intent(this, ReviewShowActivity.class);
            intent.putExtra("token", token);
            intent.putExtra("Rname", rname);
            intent.putExtra("Rid", rid);
            startActivity(intent);
        });

        GetMenu getMenu = new GetMenu(RestaurantActivity.this);
        getMenu.execute(ADDRESS_RESTAURANT + "/" + rid + "/menu", token);

        try {
            mJsonstring = getMenu.get();
        }catch (ExecutionException | InterruptedException e) {
            Log.d("Menulist", "getMenu : ", e);
        }

        try {
            JSONArray jsonArray = new JSONArray(mJsonstring);       // 전체 데이터를 배열에 저장
            adapter = new MAdapter();

            for (int i = 0; i < jsonArray.length(); i++) {                // 한 그룹{} 씩 읽음
                JSONObject item = jsonArray.getJSONObject(i);       // 해당 그룹의 데이터 하나씩 읽어서 각각의 변수에 저장
                int id = Integer.parseInt(item.getString("id"));
                int restaurant = Integer.parseInt(item.getString("restaurant"));
                String category = item.getString("category");
                String name = item.getString("name");
                String price = item.getString("price");
                String emotion = item.getString("emotion");
                String weather = item.getString("weather");
                boolean checkallergy = item.getBoolean("checkallergy");
                String image = "";
                if(!item.getString("image").equals("null"))
                    image = item.getString("image");
                else image = "null";
                adapter.addRItem(id, restaurant, category, name, price, emotion, weather, image, checkallergy);
            }

            listView.setAdapter(adapter);

        } catch (JSONException e) {
            Log.d("Menulist", "showResult : ", e);
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                MenuItem item = (MenuItem) adapter.getItem(i);
                String Mid = String.valueOf(item.getId());
                String Name = item.getName();
                Intent intent = new Intent(adapterView.getContext(), NutritionActivity.class);
                intent.putExtra("token", token);
                intent.putExtra("Mid", Mid);
                intent.putExtra("mname", Name);
                startActivity(intent);
            }
        });
    }

    private class GetRestaurant extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;
        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            progressDialog = ProgressDialog.show(RestaurantActivity.this, "Please Wait", null, true, true);
        }

        @Override
        protected void onPostExecute(String result){
            super.onPostExecute(result);
            progressDialog.dismiss();
            Log.d("Restaurant", "response : " + result);

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
                Log.d("Restaurant", "response code : " + responseStatusCode);

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

}

package com.example.menuapp_test;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class RestaurantActivity extends AppCompatActivity {
    private static String ADDRESS_RESTAURANT = "http://52.78.72.175/data/restaurant/{restaurant_id}/menu";
    private ImageView restaurant;
    private ImageButton back, wish;
    private TextView name, category, phone, address, time, rating;
    private RecyclerView recyclerView;
    private Button menu, review;
    private String token;
    private int id;
    private ListItem listItem;
    private ImageAdapter adapter;
    private Bitmap bitmap;
    private ArrayList<String> images;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant);

        Intent getIntent = getIntent();
        token = getIntent.getStringExtra("token");
        listItem = (ListItem) getIntent.getSerializableExtra("item");
        id = listItem.getId();

        restaurant = findViewById(R.id.img_restaurant);
        wish = findViewById(R.id.imgbtn_wish_restaurant);
        name = findViewById(R.id.rname_restaurant);
        category = findViewById(R.id.category_restaurant);
        phone = findViewById(R.id.rphone_restaurant);
        address = findViewById(R.id.address_restaurant);
        time = findViewById(R.id.time_restaurant);
        rating = findViewById(R.id.rating_restaurant);
        recyclerView = findViewById(R.id.recyclerv_restaurant);
        menu = findViewById(R.id.menu_restaurant);
        review = findViewById(R.id.review_restaurant);

        if(!listItem.getImage().equals("http://52.78.72.175null")){
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

        name.setText(listItem.getName());
        category.setText(listItem.getCategory_name());
        phone.setText(listItem.getPhone_number());
        address.setText(listItem.getAddress());
        time.setText(listItem.getBusiness_hours());
        rating.setText(listItem.getRating());

        /*LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new ImageAdapter(images);
        recyclerView.setAdapter(adapter);*/

        menu.setOnClickListener(v -> {
            String Rid = String.valueOf(id);
            Intent intent = new Intent(this, MenulistActivity.class);
            intent.putExtra("token", token);
            intent.putExtra("Rid", Rid);
            startActivity(intent);
        });
        review.setOnClickListener(v -> {
            String Rid = String.valueOf(id);
            Intent intent = new Intent(this, ReviewShowActivity.class);
            intent.putExtra("token", token);
            intent.putExtra("Rid", Rid);
            startActivity(intent);
        });
        /*back.setOnClickListener(v -> {
            if(pre.equals("list")){
                Intent intent = new Intent(this, ListActivity.class);
                intent.putExtra("token", token);
                startActivity(intent);
            }
            else if(pre.equals("menu")){
                Intent intent = new Intent(this, RecommendResActivity.class);
                intent.putExtra("token", token);
                startActivity(intent);
            }

        });*/
    }
}

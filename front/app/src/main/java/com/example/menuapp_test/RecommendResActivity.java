package com.example.menuapp_test;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class RecommendResActivity extends AppCompatActivity {
    private static String ADDRESS_RECOMMEND = "http://52.78.72.175/recommendation/menurecommend";
    private TextView nickname, menu, restaurant, mprice;            // 닉네임, 추천되는 메뉴, 음식점
    private Button info, select;        // 상세정보 보기, 메뉴 이용하기, 재추천
    private ImageButton re;
    private ImageView imageView;
    private String token, price, weather, emotion, Nickname, id, rid, menuid, mname, rname;
    private RecommendItem recommendItem;
    private Bitmap bitmap;
    private FloatingActionButton home;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommend_res);

        Intent getIntent = getIntent();
        token = getIntent.getStringExtra("token");
        price = getIntent.getStringExtra("price");
        weather = getIntent.getStringExtra("weather");
        emotion = getIntent.getStringExtra("emotion");
        Nickname = getIntent.getStringExtra("nickname");
        id = getIntent.getStringExtra("id");
        menuid = getIntent.getStringExtra("mid");

        menu = findViewById(R.id.menu_recommend_res);
        info = findViewById(R.id.btn_recommend_nut);
        select = findViewById(R.id.btn_recommend_ok);
        re = findViewById(R.id.btn_recommend_re);
        restaurant = findViewById(R.id.rname_recommend_res);
        mprice = findViewById(R.id.price_recommend_res);
        imageView = findViewById(R.id.img_recommend_res);
        home = findViewById(R.id.fab);

        home.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("token", token);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        PostRecommend postRecommend = new PostRecommend(RecommendResActivity.this);
        postRecommend.execute(ADDRESS_RECOMMEND, price, weather, emotion, token);

        try {
            int mid = 0; String name = " "; int price = 0; int Rid = 0; String Rname = " "; String image = "null";
            String JsonString = postRecommend.get();
            if(JsonString.contains("없습니다")){
                showPop("m");
            }
            else {
                JSONObject jsonObject = new JSONObject(JsonString);
                mid = jsonObject.getInt("menu_id");
                name = jsonObject.getString("menu_name");
                price = jsonObject.getInt("menu_price");
                Rid = jsonObject.getInt("restaurant_id");
                Rname = jsonObject.getString("restaurant_name");
                image = jsonObject.getString("image");

                recommendItem = new RecommendItem(mid, Rid, name, price, Rname, "http://52.78.72.175" + image);

                if(!recommendItem.getImage().equals("http://52.78.72.175null")){
                    Thread thread = new Thread() {
                        @Override
                        public void run(){
                            try {
                                URL url = new URL(recommendItem.getImage());
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
                menuid = String.valueOf(recommendItem.getId());
                mname = recommendItem.getName();
                menu.setText(mname);
                rname = recommendItem.getRname();
                restaurant.setText("음식점 : " + rname);
                rid = String.valueOf(recommendItem.getRestaurant());
                String Price = String.valueOf(recommendItem.getPrice());
                mprice.setText("가격 : " + Price + "원");
            }
        } catch (Exception e) {
            Log.d("survey", "Error ", e);
        }

        info.setOnClickListener(v -> {                      // 영양 정보 화면으로 이동
            Intent intent = new Intent(this, NutritionActivity.class);
            intent.putExtra("token", token);
            intent.putExtra("Mid", menuid);
            intent.putExtra("Mname", mname);
            startActivity(intent);
        });

        select.setOnClickListener(view -> {

            showPop("r");
        });

        re.setOnClickListener(v -> {                        // 액티비티 재실행
            Intent intent = new Intent(this, RecommendResActivity.class);
            intent.putExtra("token", token);
            intent.putExtra("price", price);
            intent.putExtra("weather", weather);
            intent.putExtra("emotion", emotion);
            intent.putExtra("nickname", Nickname);
            intent.putExtra("mid", menuid);
            intent.putExtra("id", id);
            startActivity(intent);
        });
    }
    void showPop(String r) {
        if(r.equals("r")) {
            // 리뷰 작성 팝업창 (예 - 리뷰 작성 페이지 / 아니오 - 메인 화면)
            Intent intent = new Intent(RecommendResActivity.this, PopupReview.class);
            intent.putExtra("token", token);
            intent.putExtra("Mid", menuid);
            intent.putExtra("Rid", rid);
            intent.putExtra("Rname", rname);
            intent.putExtra("Mname", mname);
            intent.putExtra("RecommendItem", recommendItem);
            startActivity(intent);
        }
        else {
            Intent intent = new Intent(RecommendResActivity.this, PopupMain.class);
            intent.putExtra("token", token);
            startActivity(intent);
        }
    }
}

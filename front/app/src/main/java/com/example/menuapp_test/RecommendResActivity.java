package com.example.menuapp_test;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Random;

public class RecommendResActivity extends AppCompatActivity {
    private static String ADDRESS_RECOMMEND = "";
    TextView nickname, menu;            // 닉네임, 추천되는 메뉴
    Button info, select, re;        // 상세정보 보기, 메뉴 이용하기, 재추천
    private String token, price, weather, emotion, restaurant, menuid;
    private ArrayList<RecommendItem> recommendItems;
    private Random random;
    private int r[] = new int[10];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommend_res);

        random = new Random();
        random.setSeed(System.currentTimeMillis());

        Intent getIntent = getIntent();
        token = getIntent.getStringExtra("token");
        price = getIntent.getStringExtra("price");
        weather = getIntent.getStringExtra("weather");
        emotion = getIntent.getStringExtra("emotion");

        nickname = findViewById(R.id.name_recommend_res);
        menu = findViewById(R.id.menu_recommend_res);
        info = findViewById(R.id.btn_recommend_list);
        select = findViewById(R.id.btn_recommend_ok);
        re = findViewById(R.id.btn_recommend_re);

        GetRecommend getRecommend = new GetRecommend(RecommendResActivity.this);
        getRecommend.execute(ADDRESS_RECOMMEND, price, weather, emotion, token);

        recommendItems = new ArrayList<RecommendItem>();

        try {
            JSONArray jsonArray = new JSONArray(getRecommend.get());
            for(int i=0; i<jsonArray.length(); i++){
                JSONObject item = jsonArray.getJSONObject(i);
                int id = Integer.parseInt(item.getString("id"));
                int restaurant = Integer.parseInt(item.getString("restaurant"));
                String category = item.getString("category");
                String name = item.getString("name");
                String image = item.getString("image");

                RecommendItem data = new RecommendItem(id, restaurant, name, category, image);
                recommendItems.add(data);
            }
            boolean chk;

            for(int i=0; i<10; i++){
                r[i] = random.nextInt(30) + 1;
                chk = false;

                for(int j=0; j<i; j++){
                    if(r[i] == r[j]) {
                        i--;
                        chk = true;
                        break;
                    }
                }
                if(chk) break;
            }
            RecommendItem res = recommendItems.get(r[0]);

            menuid = String.valueOf(res.getId());
            menu.setText(res.getName());
            restaurant = String.valueOf(res.getRestaurant());

        } catch (Exception e) {
            Log.d("survey", "Error ", e);
        }

        /*info.setOnClickListener(v -> {                      // 음식점 화면으로 이동
            Intent intent = new Intent(this, RestaurantActivity.class);
            intent.putExtra("token", token);
            intent.puExtra("restaurant", restaurant);
            startActivity(intent);
        });*/
        select.setOnClickListener(v -> {                    // 변수에 저장 후 토스트 메시지 출력
            String selectmenu = menuid;
            Toast.makeText(getApplicationContext(), "확정되었습니다!", Toast.LENGTH_SHORT).show();
            // 리뷰 작성 팝업창 (예 - 리뷰 작성 페이지 / 아니오 - 메인 화면)
        });
        re.setOnClickListener(v -> {                        // 메뉴 추천 화면으로 이동(이용자 번거로움) or 이 화면 재실행 ??
            int i = 0;
            RecommendItem res = recommendItems.get(r[i+1]);
            menuid = String.valueOf(res.getId());
            menu.setText(res.getName());
            restaurant = String.valueOf(res.getRestaurant());
        });

    }
}

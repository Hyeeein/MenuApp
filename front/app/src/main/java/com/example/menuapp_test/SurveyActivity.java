package com.example.menuapp_test;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class SurveyActivity extends AppCompatActivity {
    private static String ADDRESS_MENU = "http://52.78.72.175/data/preference";
    private static String ADDRESS_SURVEY = "http://52.78.72.175/data/preference/update";
    private Button skip, save;
    private String token;
    private ListView mListview;
    private SurveyAdapter adapter;
    private TextView txt;
    private Random random;
    private HashMap<String, String> map = new HashMap<>();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey);

        random = new Random();
        random.setSeed(System.currentTimeMillis());

        skip = findViewById(R.id.btn_skip);
        save = findViewById(R.id.btn_save);
        mListview = findViewById(R.id.listv_survey);

        Intent getintent = getIntent();
        //token = getintent.getStringExtra("token");
        token = "49e9d8db7d6d31d3623b4af2d3fb97178d6d773e";


        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        GetMenu getMenu = new GetMenu(SurveyActivity.this);
        getMenu.execute(ADDRESS_MENU, token);
        adapter = new SurveyAdapter();

        // 서버의 데이터를 순서대로 읽어 어댑터에 저장
        try {
            JSONArray jsonArray = new JSONArray(getMenu.get());
            int j = 0;

            for(int i=0; i<5; i++){
                String preference = "";
                JSONObject item = new JSONObject();
                do {
                    item = jsonArray.getJSONObject(j);
                    preference = item.getString("preference");
                    j++;
                } while (preference.contains("1")); // 이미 평가된 메뉴일 경우 어댑터에 저장하지 않음

                int id = Integer.parseInt(item.getString("id"));
                String category = item.getString("category");
                String name = item.getString("name");
                String image = item.getString("image");

                adapter.addSItem(id, category, name, "http://52.78.72.175" + image, Integer.parseInt(preference));
            }
        } catch (Exception e) {
            Log.d("survey", "Error ", e);
        }
        mListview.setAdapter(adapter);

        skip.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
        });

        save.setOnClickListener(v -> {
            for(Map.Entry<String, String> entrySet : map.entrySet()){
                PostSurvey postSurvey = new PostSurvey(SurveyActivity.this);
                postSurvey.execute(ADDRESS_SURVEY, entrySet.getKey(), entrySet.getValue(), token);
            }

            Toast.makeText(SurveyActivity.this, "취향 정보가 저장되었습니다.", Toast.LENGTH_LONG).show();

            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
        });
    }

    class SurveyAdapter extends BaseAdapter {
        ArrayList<SurveyItem> data = new ArrayList<SurveyItem>();
        private String radio;
        private Bitmap bitmap;

        public SurveyAdapter(){ }
        @Override
        public int getCount(){
            return data.size();
        }
        @Override
        public long getItemId(int position) {
            return position;
        }
        @Override
        public SurveyItem getItem(int position) {
            return data.get(position);
        }
        @Override
        public View getView(int position, View view, ViewGroup parent){
            Context context = parent.getContext();

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.item_survey, parent, false);

            ImageView imageView = view.findViewById(R.id.img_survey);
            TextView name = view.findViewById(R.id.menu_survey);
            RadioGroup radioGroup = view.findViewById(R.id.radio_group);

            SurveyItem surveyItem = data.get(position);
            name.setText(surveyItem.getName());

            if(!surveyItem.getImage().equals("http://52.78.72.175null")){
                Thread thread = new Thread() {
                    @Override
                    public void run(){
                        try {
                            URL url = new URL(surveyItem.getImage());
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
            radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup radioGroup, int i) {
                    switch (i) {
                        case R.id.radio_good:
                            radio = "1";
                            break;
                        case R.id.radio_soso:
                            radio = "0";
                            break;
                        case R.id.radio_bad:
                            radio = "-1";
                            break;
                    }
                    txt.setText(String.valueOf(surveyItem.getId()) + radio);
                    map.put(String.valueOf(surveyItem.getId()), radio);
                }
            });
            return view;
        }
        public void addSItem(int id, String category, String name, String image, int preference){
            SurveyItem item = new SurveyItem();
            item.setId(id);
            item.setCategory(category);
            item.setName(name);
            item.setImage(image);
            item.setPreference(preference);
            data.add(item);
        }
    }
}
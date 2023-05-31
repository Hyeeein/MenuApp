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

import com.google.android.material.floatingactionbutton.FloatingActionButton;

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

public class PreferenceUpdateActivity extends AppCompatActivity {
    private static String ADDRESS_MENU = "http://52.78.72.175/data/preference/50";
    private static String ADDRESS_SURVEY = "http://52.78.72.175/data/preference/update";
    private Button skip, save;
    private String token, nickname, email, intro;
    private ListView mListview;
    private SurveyAdapter adapter;
    private Random random;
    private HashMap<String, String> map = new HashMap<>();
    private FloatingActionButton home;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preference_update);

        random = new Random();
        random.setSeed(System.currentTimeMillis());

        save = findViewById(R.id.btn_prefer);
        mListview = findViewById(R.id.listv_prefer);

        Intent getIntent = getIntent();
        token = getIntent.getStringExtra("token");
        nickname = getIntent.getStringExtra("nickname");
        email = getIntent.getStringExtra("email");
        intro = getIntent.getStringExtra("intro");

        home = findViewById(R.id.fab);

        home.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("token", token);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
/*
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }*/
        GetMenu getMenu = new GetMenu(PreferenceUpdateActivity.this);
        getMenu.execute(ADDRESS_MENU, token);
        adapter = new SurveyAdapter();

        // 서버의 데이터를 순서대로 읽어 어댑터에 저장
        try {
            JSONArray jsonArray = new JSONArray(getMenu.get());
            int j = 0;

            for(int i=0; i<50; i++){
                String preference = "";
                JSONObject item = new JSONObject();
/*                do{
                    item = jsonArray.getJSONObject(j);
                    preference = item.getString("preference");
                    j++;
                } while (preference.equals("1"));*/
                item = jsonArray.getJSONObject(i);
                int id = Integer.parseInt(item.getString("id"));
                String name = item.getString("name");
                String image = item.getString("image");

                adapter.addSItem(id, name, image);
            }
        } catch (Exception e) {
            Log.d("survey", "Error ", e);
        }
        mListview.setAdapter(adapter);

        save.setOnClickListener(v -> {
            for(Map.Entry<String, String> entrySet : map.entrySet()){
                PostSurvey postSurvey = new PostSurvey(PreferenceUpdateActivity.this);
                postSurvey.execute(ADDRESS_SURVEY, entrySet.getKey(), entrySet.getValue(), token);
            }

            Toast.makeText(PreferenceUpdateActivity.this, "취향 정보가 저장되었습니다.", Toast.LENGTH_LONG).show();

            Intent intent = new Intent(this, SettingActivity.class);
            intent.putExtra("token", token);
            intent.putExtra("nickname", nickname);
            intent.putExtra("email", email);
            intent.putExtra("intro", intro);
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

            if(!surveyItem.getImage().equals("null")){
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
                    map.put(String.valueOf(surveyItem.getId()), radio);
                }
            });
            return view;
        }
        public void addSItem(int id, String name, String image){
            SurveyItem item = new SurveyItem();
            item.setId(id);
            item.setName(name);
            item.setImage(image);
            data.add(item);
        }
    }
}
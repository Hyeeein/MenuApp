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
    private static String ADDRESS_SURVEY = "http://52.78.72.175/data/preference/10";
    private static String ADDRESS_PREFER = "http://52.78.72.175/data/preference/update";
    private Button skip, save;
    private String token;
    private ListView mListview;
    private SurveyAdapter adapter;
    private Random random;
    private HashMap<String, String> map = new HashMap<>();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey);

        random = new Random();
        random.setSeed(System.currentTimeMillis());

        save = findViewById(R.id.btn_save);
        mListview = findViewById(R.id.listv_survey);

        Intent getintent = getIntent();
        token = getintent.getStringExtra("token");
/*
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }*/
        GetPreference getPreference = new GetPreference(SurveyActivity.this);
        getPreference.execute(ADDRESS_SURVEY, token);
        adapter = new SurveyAdapter();

        // 서버의 데이터를 순서대로 읽어 어댑터에 저장
        try {
            JSONArray jsonArray = new JSONArray(getPreference.get());
            int j = 0;

            for(int i=0; i<10; i++){
                JSONObject item = new JSONObject();
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
                PostSurvey postSurvey = new PostSurvey(SurveyActivity.this);
                postSurvey.execute(ADDRESS_PREFER, entrySet.getKey(), entrySet.getValue(), token);
            }

            Toast.makeText(SurveyActivity.this, "가입이 완료되었습니다.", Toast.LENGTH_LONG).show();

            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            intent.putExtra("key", "s");
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
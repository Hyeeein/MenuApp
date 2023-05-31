package com.example.menuapp_test;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class RecommendActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private TextView nickname;
    private Spinner price;       // 스피너 변수
    private Button sunny, cloud, rain, snow, dust, hungry, soso, good, tired, stress, next;
    private String Nickname, Price, Weather, Emotion, token, id;
    private boolean w, e;
    private FloatingActionButton home;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommend_b);

        Intent getIntent = getIntent();
        token = getIntent.getStringExtra("token");
        Nickname = getIntent.getStringExtra("nickname");
        id = getIntent.getStringExtra("id");

        nickname = findViewById(R.id.name_recommend);
        nickname.setText(Nickname);
        price = findViewById(R.id.spinner_price);
        sunny = findViewById(R.id.btn_sunny);
        cloud = findViewById(R.id.btn_cloud);
        rain = findViewById(R.id.btn_rain);
        snow = findViewById(R.id.btn_snow);
        dust = findViewById(R.id.btn_dust);
        hungry = findViewById(R.id.btn_hungry);
        soso = findViewById(R.id.btn_soso);
        good = findViewById(R.id.btn_good);
        tired = findViewById(R.id.btn_tired);
        stress = findViewById(R.id.btn_stress);
        next = findViewById(R.id.btn_recommend);
        home = findViewById(R.id.fab);

        w = false; e = false;

        home.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("token", token);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        sunny.setOnClickListener(v -> {
            Weather = sunny.getText().toString();
            sunny.setTextColor(Color.parseColor("#faf4c0"));
            cloud.setTextColor(Color.parseColor("#747474"));
            rain.setTextColor(Color.parseColor("#747474"));
            snow.setTextColor(Color.parseColor("#747474"));
            dust.setTextColor(Color.parseColor("#747474"));
            w = true;
        });
        cloud.setOnClickListener(v -> {
            Weather = cloud.getText().toString();
            cloud.setTextColor(Color.parseColor("#faf4c0"));
            sunny.setTextColor(Color.parseColor("#747474"));
            rain.setTextColor(Color.parseColor("#747474"));
            snow.setTextColor(Color.parseColor("#747474"));
            dust.setTextColor(Color.parseColor("#747474"));
            w = true;
        });
        rain.setOnClickListener(v -> {
            Weather = rain.getText().toString();
            rain.setTextColor(Color.parseColor("#faf4c0"));
            sunny.setTextColor(Color.parseColor("#747474"));
            cloud.setTextColor(Color.parseColor("#747474"));
            snow.setTextColor(Color.parseColor("#747474"));
            dust.setTextColor(Color.parseColor("#747474"));
            w = true;
        });
        snow.setOnClickListener(v -> {
            Weather = snow.getText().toString();
            snow.setTextColor(Color.parseColor("#faf4c0"));
            sunny.setTextColor(Color.parseColor("#747474"));
            cloud.setTextColor(Color.parseColor("#747474"));
            rain.setTextColor(Color.parseColor("#747474"));
            dust.setTextColor(Color.parseColor("#747474"));
            w = true;
        });
        dust.setOnClickListener(v -> {
            Weather = dust.getText().toString();
            dust.setTextColor(Color.parseColor("#faf4c0"));
            sunny.setTextColor(Color.parseColor("#747474"));
            cloud.setTextColor(Color.parseColor("#747474"));
            rain.setTextColor(Color.parseColor("#747474"));
            snow.setTextColor(Color.parseColor("#747474"));
            w = true;
        });

        hungry.setOnClickListener(v -> {
            Emotion = hungry.getText().toString();
            hungry.setTextColor(Color.parseColor("#faf4c0"));
            soso.setTextColor(Color.parseColor("#747474"));
            good.setTextColor(Color.parseColor("#747474"));
            tired.setTextColor(Color.parseColor("#747474"));
            stress.setTextColor(Color.parseColor("#747474"));
            e = true;
        });
        soso.setOnClickListener(v -> {
            Emotion = soso.getText().toString();
            soso.setTextColor(Color.parseColor("#faf4c0"));
            hungry.setTextColor(Color.parseColor("#747474"));
            good.setTextColor(Color.parseColor("#747474"));
            tired.setTextColor(Color.parseColor("#747474"));
            stress.setTextColor(Color.parseColor("#747474"));
            e = true;
        });
        good.setOnClickListener(v -> {
            Emotion = good.getText().toString();
            good.setTextColor(Color.parseColor("#faf4c0"));
            hungry.setTextColor(Color.parseColor("#747474"));
            soso.setTextColor(Color.parseColor("#747474"));
            tired.setTextColor(Color.parseColor("#747474"));
            stress.setTextColor(Color.parseColor("#747474"));
            e = true;
        });
        tired.setOnClickListener(v -> {
            Emotion = tired.getText().toString();
            tired.setTextColor(Color.parseColor("#faf4c0"));
            hungry.setTextColor(Color.parseColor("#747474"));
            soso.setTextColor(Color.parseColor("#747474"));
            good.setTextColor(Color.parseColor("#747474"));
            stress.setTextColor(Color.parseColor("#747474"));
            e = true;
        });
        stress.setOnClickListener(v -> {
            Emotion = stress.getText().toString();
            stress.setTextColor(Color.parseColor("#faf4c0"));
            hungry.setTextColor(Color.parseColor("#747474"));
            soso.setTextColor(Color.parseColor("#747474"));
            good.setTextColor(Color.parseColor("#747474"));
            tired.setTextColor(Color.parseColor("#747474"));
            e = true;
        });


        ArrayAdapter<CharSequence> priceadaper = ArrayAdapter.createFromResource(this, R.array.가격, android.R.layout.simple_spinner_item);
        priceadaper.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        price.setAdapter(priceadaper);
        price.setSelection(0);

        price.setOnItemSelectedListener(this);

        next.setOnClickListener(v -> {
            Price = price.getSelectedItem().toString();

            if(w){
                if(e){
                    Intent intent = new Intent(this, RecommendResActivity.class);
                    intent.putExtra("token", token);
                    intent.putExtra("id", id);
                    intent.putExtra("price", Price);
                    intent.putExtra("weather", Weather);
                    intent.putExtra("emotion", Emotion);
                    intent.putExtra("nickname", Nickname);
                    startActivity(intent);
                }
                else Toast.makeText(this, "기분을 선택해 주세요.", Toast.LENGTH_SHORT).show();
            }
            else Toast.makeText(this, "날씨를 선택해 주세요.", Toast.LENGTH_SHORT).show();
       });
    }
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l){

    }
    @Override
    public void onNothingSelected(AdapterView<?> adapterView){    }
}

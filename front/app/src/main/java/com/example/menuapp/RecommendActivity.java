package com.example.menuapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class RecommendActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    TextView nickname;
    Spinner price, weather, emotion;       // 스피너 변수
    Button next;
    private String Price, Weather, Emotion, token;                  // 선택된 가격 값 담을 문자형 변수

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommend);

        Intent getIntent = getIntent();
        token = getIntent.getStringExtra("token");

        nickname = findViewById(R.id.name_recommend);
        price = findViewById(R.id.spinner_price);
        weather = findViewById(R.id.spinner_weather);
        emotion = findViewById(R.id.spinner_emotion);
        next = findViewById(R.id.btn_recommend);

        ArrayAdapter<CharSequence> priceadaper = ArrayAdapter.createFromResource(this, R.array.가격, android.R.layout.simple_spinner_item);
        priceadaper.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        price.setAdapter(priceadaper);

        ArrayAdapter<CharSequence> weatheradaper = ArrayAdapter.createFromResource(this, R.array.날씨, android.R.layout.simple_spinner_item);
        weatheradaper.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        weather.setAdapter(weatheradaper);

        ArrayAdapter<CharSequence> emotionadaper = ArrayAdapter.createFromResource(this, R.array.기분, android.R.layout.simple_spinner_item);
        emotionadaper.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        emotion.setAdapter(emotionadaper);

        price.setOnItemSelectedListener(this);

        next.setOnClickListener(v -> {
            Intent intent = new Intent(this, RecommendResActivity.class);
            intent.putExtra("token", token);
            intent.putExtra("price", Price);
            intent.putExtra("weather", Weather);
            intent.putExtra("emotion", Emotion);
            startActivity(intent);
       });
    }
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l){

    }
    @Override
    public void onNothingSelected(AdapterView<?> adapterView){    }
}

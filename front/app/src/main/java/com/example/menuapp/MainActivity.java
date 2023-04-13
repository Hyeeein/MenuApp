package com.example.menuapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    TextView name, location;
    Button list, menu, survey, mypage;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        name = findViewById(R.id.name_main);
        location = findViewById(R.id.location_main);
        list = findViewById(R.id.btn_main_list);
        menu = findViewById(R.id.btn_main_menu);
        survey = findViewById(R.id.btn_main_survey);
        mypage = findViewById(R.id.btn_main_mypage);

        Intent getIntent = getIntent();
        token = getIntent.getStringExtra("token");

        list.setOnClickListener(v -> {
            Intent intent = new Intent(this, ListActivity.class);
            intent.putExtra("token", token);
            startActivity(intent);
        });

        menu.setOnClickListener(v -> {
            Intent intent = new Intent(this, RecommendActivity.class);
            intent.putExtra("token", token);
            startActivity(intent);
        });

        survey.setOnClickListener(v -> {
            Intent intent = new Intent(this, SurveyActivity.class);
            intent.putExtra("token", token);
            startActivity(intent);
        });

        mypage.setOnClickListener(v -> {
            Intent intent = new Intent(this, MypageActivity.class);
            intent.putExtra("token", token);
            startActivity(intent);
        });

    }
}

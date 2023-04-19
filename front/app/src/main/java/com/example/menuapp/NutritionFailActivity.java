package com.example.menuapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

public class NutritionFailActivity extends AppCompatActivity {
    ImageButton backarrow;
    Button back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nutrition_fail);

        backarrow = findViewById(R.id.imgbtn_nutrition_fail);
        back = findViewById(R.id.btn_nutrition_fail);

        backarrow.setOnClickListener(v -> {
            Intent intent = new Intent(this, MenulistActivity.class);
            startActivity(intent);
        });

        back.setOnClickListener(v -> {
            Intent intent = new Intent(this, MenulistActivity.class);
            startActivity(intent);
        });
    }
}

package com.example.menuapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class NutritionActivity extends AppCompatActivity {
    ImageButton back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nutrition);

        back = findViewById(R.id.imgbtn_nutrition);

        back.setOnClickListener(v -> {
            Intent intent = new Intent(this, MenulistActivity.class);
            startActivity(intent);
        });
    }
}

package com.example.menuapp_test;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class GateActivity extends AppCompatActivity {
    Button login, join;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gate);

        login = findViewById(R.id.btn_gate);
        join = findViewById(R.id.btn_gate2);

        login.setOnClickListener(v -> {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.putExtra("key", "g");
            startActivity(intent);
        });

        join.setOnClickListener(v -> {
            Intent intent = new Intent(this, JoinActivity.class);
            startActivity(intent);
        });
    }
}

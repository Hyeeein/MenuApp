package com.example.menuapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

public class JoinActivity3 extends AppCompatActivity {
    Spinner phonefirst;
    EditText phonelast;
    Button check, next;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_3);

        phonefirst = findViewById(R.id.spinner_phone);
        phonelast = findViewById(R.id.edit_phone);
        check = findViewById(R.id.btn_join_num);
        next = findViewById(R.id.btn_join_3);

        ArrayAdapter phoneAdapter = ArrayAdapter.createFromResource(this, R.array.전화번호, android.R.layout.simple_spinner_dropdown_item);
        phoneAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        phonefirst.setAdapter(phoneAdapter);

        next.setOnClickListener(v -> {
            Intent intent = new Intent(this, JoinAllergieActivity.class);
            startActivity(intent);
        });

    }

}

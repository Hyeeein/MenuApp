package com.example.menuapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class FindPwActivity extends AppCompatActivity {
    EditText id, checknum;
    Button sendnum, check, next;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_pw);

        id = findViewById(R.id.edit_find_email);
        checknum = findViewById(R.id.edit_find_num);
        sendnum = findViewById(R.id.btn_find_num);
        check = findViewById(R.id.btn_find_check);
        next = findViewById(R.id.btn_find_pw);

        next.setOnClickListener(v -> {
            Intent intent = new Intent(this, NewPwActivity.class);
            startActivity(intent);
        });

    }
}

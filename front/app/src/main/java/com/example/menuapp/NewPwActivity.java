package com.example.menuapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

public class NewPwActivity extends AppCompatActivity {
    TextInputEditText newpw, checkpw;
    Button pwcheck, save, relogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_pw);

        newpw = findViewById(R.id.newpw);
        checkpw = findViewById(R.id.checkpw);
        pwcheck = findViewById(R.id.btn_checkpw);
        save = findViewById(R.id.btn_newpw);
        relogin = findViewById(R.id.btn_newpw2);

        pwcheck.setOnClickListener(v -> {
            if (newpw.getText().toString().equals(checkpw.getText().toString())) {
                pwcheck.setText("비밀번호가 일치합니다.");
            } else {
                Toast.makeText(NewPwActivity.this, "비밀번호가 다릅니다.", Toast.LENGTH_LONG).show();
            }
        });

        relogin.setOnClickListener(v -> {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        });
    }
}

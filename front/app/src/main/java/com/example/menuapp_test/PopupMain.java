package com.example.menuapp_test;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.widget.Button;

public class PopupMain extends Activity {
    private Button skip;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.popup_main);

        skip = findViewById(R.id.btn_main);

        Intent getIntent = getIntent();
        token = getIntent.getStringExtra("token");

        skip.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("token", token);
            startActivity(intent);
        });
    }
}
package com.example.menuapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MypageActivity extends AppCompatActivity {
    private static String ADDRESS_LOGOUT = "http://52.78.72.175/account/logout";
    private String token;
    TextView name, comment, wishlist, setting, logout, delete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mypage);

        Intent getIntent = getIntent();
        token = getIntent.getStringExtra("token");

        name = findViewById(R.id.name_mypage);
        comment = findViewById(R.id.comment_mypage);
        wishlist = findViewById(R.id.txt_btn_wish);
        setting = findViewById(R.id.txt_btn_setting);
        logout = findViewById(R.id.txt_btn_logout);
        delete = findViewById(R.id.txt_btn_delete);

        /*wishlist.setOnClickListener(v -> {
            Intent intent = new Intent(this, WishlistActivity.class);
            startActivity(intent);
        });
        */

        setting.setOnClickListener(v -> {
            Intent intent = new Intent(this, SettingActivity.class);
            intent.putExtra("token", token);
            startActivity(intent);
        });

        logout.setOnClickListener(v -> {
            Logout logout1 = new Logout(MypageActivity.this);
            logout1.execute(ADDRESS_LOGOUT, token);

            Intent intent = new Intent(this, GateActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPop();
            }
        });
    }
    void showPop() {
        Intent intent = new Intent(this, PopupPW.class);
        intent.putExtra("token", token);
        startActivity(intent);
    }
}


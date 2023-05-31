package com.example.menuapp_test;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

public class MypageActivity extends AppCompatActivity {
    private static String ADDRESS_USER = "http://52.78.72.175/data/mypage";
    private static String ADDRESS_LOGOUT = "http://52.78.72.175/account/logout";
    private String token, nickname, intro, email;
    private TextView name, comment, review, wishlist, setting, logout, delete;
    private FloatingActionButton home;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mypage);

        Intent getIntent = getIntent();
        token = getIntent.getStringExtra("token");
        nickname = getIntent.getStringExtra("nickname");
        email = getIntent.getStringExtra("email");
        intro = getIntent.getStringExtra("intro");

        name = findViewById(R.id.name_mypage);
        comment = findViewById(R.id.comment_mypage);
        wishlist = findViewById(R.id.txt_btn_wish);
        setting = findViewById(R.id.txt_btn_setting);
        logout = findViewById(R.id.txt_btn_logout);
        delete = findViewById(R.id.txt_btn_delete);
        review = findViewById(R.id.mypage_review);
        home = findViewById(R.id.fab);

        home.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("token", token);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        GetUser getUser = new GetUser(MypageActivity.this);
        getUser.execute(ADDRESS_USER, token);

        try {
            JSONObject jsonObject = new JSONObject(getUser.get());
            email = jsonObject.getString("email");
            nickname = jsonObject.getString("nickname");
            intro = jsonObject.getString("introduction");
            name.setText(nickname);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        name.setText(nickname);
        comment.setText(intro);

        wishlist.setOnClickListener(v -> {
            Intent intent = new Intent(this, WishlistActivity.class);
            intent.putExtra("token", token);
            startActivity(intent);
        });

        setting.setOnClickListener(v -> {
            Intent intent = new Intent(this, SettingActivity.class);
            intent.putExtra("token", token);
            intent.putExtra("nickname", nickname);
            intent.putExtra("email", email);
            intent.putExtra("intro", intro);
            startActivity(intent);
        });

        logout.setOnClickListener(v -> {
            Logout logout1 = new Logout(MypageActivity.this);
            logout1.execute(ADDRESS_LOGOUT, token);

            Intent intent = new Intent(this, GateActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        review.setOnClickListener(v -> {
            Intent intent = new Intent(this, ReviewUserActivity.class);
            intent.putExtra("token", token);
            startActivity(intent);
        });

        delete.setOnClickListener(view -> showPop());
    }
    void showPop() {
        Intent intent = new Intent(this, PopupPW.class);
        intent.putExtra("token", token);
        startActivity(intent);
    }
}


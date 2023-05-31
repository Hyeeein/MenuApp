package com.example.menuapp_test;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

public class PopupReview extends Activity {
    private Button ok, skip;
    private String token, mid, rid, rname, mname;
    private RecommendItem recommendItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.popup_review);

        ok = findViewById(R.id.btn_review);
        skip = findViewById(R.id.btn_skip);

        Intent getIntent = getIntent();
        token = getIntent.getStringExtra("token");
        mid = getIntent.getStringExtra("Mid");
        rid = getIntent.getStringExtra("Rid");
        rname = getIntent.getStringExtra("Rname");
        mname = getIntent.getStringExtra("Mname");
        recommendItem = (RecommendItem) getIntent.getSerializableExtra("RecommendItem");

        ok.setOnClickListener(v -> {
            Intent intent = new Intent(this, ReviewWriteActivity.class);
            intent.putExtra("token", token);
            intent.putExtra("Mid", mid);
            intent.putExtra("Rid", rid);
            intent.putExtra("Mname", mname);
            intent.putExtra("Rname", rname);
            intent.putExtra("RecommendItem", recommendItem);
            startActivity(intent);
        });

        skip.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("token", token);
            startActivity(intent);
        });
    }
}

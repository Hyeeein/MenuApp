package com.example.menuapp_test;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

public class PopupMap extends Activity {
    private Button info, skip;
    private TextView rname, category, phone, address;
    private String token, rid;
    private ListItem listItem = new ListItem();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.popup_map);

        Intent getIntent = getIntent();
        token = getIntent.getStringExtra("token");
        rid = getIntent.getStringExtra("Rid");
        listItem = (ListItem) getIntent.getSerializableExtra("listItem");

        info = findViewById(R.id.btn_info);
        skip = findViewById(R.id.btn_skip);
        rname = findViewById(R.id.rname_popup_map);
        category = findViewById(R.id.category_popup_map);
        phone = findViewById(R.id.phone_popup_map);
        address = findViewById(R.id.address_popup_map);

        rname.setText(listItem.getName());
        category.setText(listItem.getCategory_name());
        if(listItem.getPhone_number().equals("null")) { phone.setText("-"); }
        else { phone.setText(listItem.getPhone_number()); }
        address.setText(listItem.getAddress());

        info.setOnClickListener(v -> {
            Intent intent = new Intent(this, RestaurantActivity.class);
            intent.putExtra("token", token);
            intent.putExtra("Rid", rid);
            intent.putExtra("listItem", listItem);
            startActivity(intent);
        });

        skip.setOnClickListener(v -> {
            finish();
        });
    }
}

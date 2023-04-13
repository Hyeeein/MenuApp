package com.example.menuapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SettingAllergieActivity extends AppCompatActivity {
    CheckBox egg, milk, wheat, bean, peanut, walnut, fish, shellfish, shrimp, crab;
    CheckBox korean, japanese, chinese, school, soup, barbecue, sushi, burger;
    Button save;
    String allergie_setting, like_setting;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_allergie);

        egg = (CheckBox) findViewById(R.id.chk_egg);
        milk = (CheckBox) findViewById(R.id.chk_milk);
        wheat = (CheckBox) findViewById(R.id.chk_wheat);
        bean = (CheckBox) findViewById(R.id.chk_bean);
        peanut = (CheckBox) findViewById(R.id.chk_peanut);
        walnut = (CheckBox) findViewById(R.id.chk_walnut);
        fish = (CheckBox) findViewById(R.id.chk_fish);
        shellfish = (CheckBox) findViewById(R.id.chk_shellfish);
        shrimp = (CheckBox) findViewById(R.id.chk_shrimp);
        crab = (CheckBox) findViewById(R.id.chk_crab);

        korean = (CheckBox) findViewById(R.id.chk_korean);
        japanese = (CheckBox) findViewById(R.id.chk_japanese);
        chinese = (CheckBox) findViewById(R.id.chk_chinese);
        school = (CheckBox) findViewById(R.id.chk_school);
        soup = (CheckBox) findViewById(R.id.chk_soup);
        barbecue = (CheckBox) findViewById(R.id.chk_barbecue);
        sushi = (CheckBox) findViewById(R.id.chk_sushi);
        burger = (CheckBox) findViewById(R.id.chk_burger);

        save = findViewById(R.id.btn_setting_allergie);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendAllergie(egg, milk, wheat, bean, peanut, walnut, fish, shellfish, shrimp, crab);
                sendLike(korean, japanese, chinese, school, soup, barbecue, sushi, burger);
                Intent intent = new Intent(getApplicationContext(), SettingActivity.class);
                Toast.makeText(getApplicationContext(), "저장되었습니다.", Toast.LENGTH_LONG).show();
                startActivity(intent);
            }
        });


    private String sendAllergie(CheckBox egg, CheckBox milk, CheckBox wheat, CheckBox bean, CheckBox peanut,
                                CheckBox walnut, CheckBox fish, CheckBox shellfish, CheckBox shrimp, CheckBox crab){
        String allergie = "";
        if(egg.isChecked())
            allergie += (egg.getText().toString() + ",");
        if(milk.isChecked())
            allergie += (milk.getText().toString() + ",");
        if(wheat.isChecked())
            allergie += (wheat.getText().toString() + ",");
        if(bean.isChecked())
            allergie += (bean.getText().toString() + ",");
        if(peanut.isChecked())
            allergie += (peanut.getText().toString() + ",");
        if(fish.isChecked())
            allergie += (walnut.getText().toString() + ",");
        if(shellfish.isChecked())
            allergie += (shellfish.getText().toString() + ",");
        if(shrimp.isChecked())
            allergie += (shrimp.getText().toString() + ",");
        if(crab.isChecked())
            allergie += (crab.getText().toString());
        String[] hArr = allergie.split(",");            // ,로 분리해서 배열에 넣어줌

        allergie_setting = "";
        for(int i=0; i<hArr.length; i++){                   // 리턴 변수에 넣어줌
            if(i == hArr.length-1)                          // 배열의 마지막이면 , 안 붙이고 넣음
                allergie_setting += hArr[i];
            else allergie_setting += (hArr[i] + ", ");           // 마지막 아니면 뒤에 , 붙여서 넣음
        }
        return allergie_setting;
    }

    private String sendLike(CheckBox korean, CheckBox japanese, CheckBox chinese, CheckBox school, CheckBox soup,
                            CheckBox barbecue, CheckBox sushi, CheckBox burger){
        String like = "";
        if(korean.isChecked())
            like += (korean.getText().toString() + ",");
        if(japanese.isChecked())
            like += (japanese.getText().toString() + ",");
        if(chinese.isChecked())
            like += (chinese.getText().toString() + ",");
        if(school.isChecked())
            like += (school.getText().toString() + ",");
        if(soup.isChecked())
            like += (soup.getText().toString() + ",");
        if(barbecue.isChecked())
            like += (barbecue.getText().toString() + ",");
        if(sushi.isChecked())
            like += (sushi.getText().toString() + ",");
        if(burger.isChecked())
            like += (burger.getText().toString() + ",");
        String[] hArr = like.split(",");            // ,로 분리해서 배열에 넣어줌

        like_setting = "";
        for(int i=0; i<hArr.length; i++){                   // 리턴 변수에 넣어줌
            if(i == hArr.length-1)                          // 배열의 마지막이면 , 안 붙이고 넣음
                like_setting += hArr[i];
            else like_setting += (hArr[i] + ", ");           // 마지막 아니면 뒤에 , 붙여서 넣음
        }
        return like_setting;
    }
}

package com.example.menuapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Random;

public class SurveyActivity extends AppCompatActivity {
    private static String ADDRESS_MENU = "http://52.78.72.175/data/preference";
    private static String ADDRESS_SURVEY = "http://52.78.72.175/data/preference/update";
    private Button skip, save;
    private String token;
    private ListView mListview;
    private ArrayList<MenuItem> menuItems;
    private SurveyAdapter adapter;
    private Random random;
    private TextView txt;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey);

        random = new Random();
        random.setSeed(System.currentTimeMillis());

        skip = findViewById(R.id.btn_skip);
        save = findViewById(R.id.btn_save);
        mListview = findViewById(R.id.listv_survey);
        txt = findViewById(R.id.survey_result);

        Intent getintent = getIntent();
        token = getintent.getStringExtra("token");

        adapter = new SurveyAdapter(token);

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        GetMenu getMenu = new GetMenu(SurveyActivity.this);
        getMenu.execute(ADDRESS_MENU, token);

        int r[] = new int[5];

            try {
                JSONArray jsonArray = new JSONArray(getMenu.get());
                boolean chk;

                for(int i=0; i<5; i++){
                    r[i] = random.nextInt(30) + 1;
                    chk = false;

                    for(int j=0; j<i; j++){
                        if(r[i] == r[j]) {
                            i--;
                            chk = true;
                            break;
                        }
                    }
                    if(chk) break;

                    JSONObject item = jsonArray.getJSONObject(i);
                    int id = Integer.parseInt(item.getString("id"));
                    String category = item.getString("category");
                    String name = item.getString("name");
                    String image = item.getString("image");
                    int preference = Integer.parseInt(item.getString("preference"));
                    adapter.addSItem(id, category, name, image, preference);
                }

               } catch (Exception e) {
                Log.d("survey", "Error ", e);
            }
        //}
            mListview.setAdapter(adapter);

            skip.setOnClickListener(v -> {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
            });

            save.setOnClickListener(v -> {
                /*SurveyItem item = new SurveyItem();
                String[] res = new String[10];

                for(int i=0; i<8; i+=2){
                    int id = adapter.getItem(i).getId();
                    int prefer = adapter.getItem(i).getPreference();
                    res[i] = String.valueOf(id);
                    res[i+1] = String.valueOf(prefer);
                }

                PostSurvey postSurvey = new PostSurvey();
                PostSurvey.execute(ADDRESS_SURVEY, , token);
*/
                //Toast.makeText(SurveyActivity.this, "취향 정보가 저장되었습니다.", Toast.LENGTH_LONG).show();

                //Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                //startActivity(intent);
            });


    }
}
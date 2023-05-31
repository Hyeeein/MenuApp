package com.example.menuapp_test;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutionException;

public class SettingActivity extends AppCompatActivity {
    private static String ADDRESS_ALL = "http://52.78.72.175/data/allergy";
    private static String ADDRESS_PRE = "http://52.78.72.175/data/preference/all";
    private static String ADDRESS_USER = "http://52.78.72.175/data/mypage";

    private static String ADDRESS_NICKNAME = "http://52.78.72.175/account/checknickname";
    private String mJsonString, token, Preference, nickname, email, intro, newname, newintro;
    private EditText name, introduction;
    private TextView id, allergie, preference;
    private Button set_allergie, set_preference, save, check;
    private SurveyItem surveyItem = new SurveyItem();
    private FloatingActionButton home;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        name = findViewById(R.id.name_setting);
        id = findViewById(R.id.id_setting);
        allergie = findViewById(R.id.allergie_setting);
        preference = findViewById(R.id.like_setting);
        set_allergie = findViewById(R.id.btn_setting);
        set_preference = findViewById(R.id.btn_setting_like);
        introduction = findViewById(R.id.introduce_setting);
        save = findViewById(R.id.btn_setting2);
        home = findViewById(R.id.fab);

        home.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("token", token);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        Intent getIntent = getIntent();
        token = getIntent.getStringExtra("token");
        nickname = getIntent.getStringExtra("nickname");
        email = getIntent.getStringExtra("email");
        intro = getIntent.getStringExtra("intro");

        name.setText(nickname);
        id.setText(email);
        introduction.setText(intro);
        newname = nickname;
        newintro = intro;

        GetAllergie getAllergie = new GetAllergie();
        getAllergie.execute(ADDRESS_ALL, token);

        GetPreference getPreference = new GetPreference(SettingActivity.this);
        getPreference.execute(ADDRESS_PRE, token);

        try {
            JSONArray jsonArray = new JSONArray(getPreference.get());

            Preference = "";

            for(int i=0; i<jsonArray.length(); i++){
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                if(jsonObject.getString("preference").equals("1")){
                    String Name = jsonObject.getString("name");
                    Preference += Name + ",";
                }
            }
            String[] P = Preference.split(",");
            Preference = "";
            for(int i=0; i<P.length; i++){
                if(i == P.length - 1) Preference += P[i];
                else Preference += P[i] + ", ";
            }
            if(Preference.equals("")) preference.setText("좋아하는 음식을 추가해 주세요.");
            else preference.setText(Preference);
        } catch (JSONException | ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }

        check = findViewById(R.id.btn_checkname);
        check.setOnClickListener(v -> {
            try{
                String Nickname = name.getText().toString();
                CheckDuplicate checkNickname = new CheckDuplicate(SettingActivity.this);
                checkNickname.execute(ADDRESS_NICKNAME, Nickname, "n");

                if(checkNickname.get().contains("true")) {
                    Toast.makeText(getApplicationContext(), "사용할 수 있는 닉네임입니다.", Toast.LENGTH_SHORT).show();
                    nickname = Nickname;
                }
                else {
                    Toast.makeText(getApplicationContext(), "이미 사용 중인 닉네임입니다.", Toast.LENGTH_SHORT).show();
                    name.setText("");
                }
            } catch (Exception e) {
                Log.d("nickname", "Error ", e);
            }
        });

        set_allergie.setOnClickListener(v -> {
            Intent intent = new Intent(this, AllergieUpdateActivity.class);
            intent.putExtra("token", token);
            intent.putExtra("allergie", allergie.getText().toString());
            intent.putExtra("nickname", nickname);
            intent.putExtra("email", email);
            intent.putExtra("intro", intro);
            startActivity(intent);
        });
        set_preference.setOnClickListener(v -> {
            Intent intent = new Intent(this, PreferenceUpdateActivity.class);
            intent.putExtra("token", token);
            intent.putExtra("nickname", nickname);
            intent.putExtra("email", email);
            intent.putExtra("intro", intro);
            startActivity(intent);
        });

        save.setOnClickListener(view -> {
            newname = name.getText().toString();
            newintro = introduction.getText().toString();
            if(!nickname.equals(newname)){
                Toast.makeText(getApplicationContext(), "닉네임 중복을 확인해 주세요.", Toast.LENGTH_SHORT).show();
            }
            else {
                PutUser putUser = new PutUser(SettingActivity.this);
                putUser.execute(ADDRESS_USER, newname, newintro, token);

                Intent intent = new Intent(getApplicationContext(), MypageActivity.class);
                intent.putExtra("token", token);
                Toast.makeText(getApplicationContext(), "저장되었습니다.", Toast.LENGTH_LONG).show();
                startActivity(intent);
            }

        });
    }
    private class GetAllergie extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;
        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            progressDialog = ProgressDialog.show(SettingActivity.this, "Please Wait", null, true, true);
        }

        @Override
        protected void onPostExecute(String result){
            super.onPostExecute(result);
            progressDialog.dismiss();
            Log.d("GetAllergie : ", "response : " + result);

            mJsonString = result;               // 서버의 데이터를 문자열로 읽어와서 변수에 저장
            showResult();

        }

        @Override
        protected String doInBackground(String... params){
            String serverURL = params[0];
            String Token = params[1];

            try {
                URL url = new URL(serverURL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                conn.setReadTimeout(5000);
                conn.setConnectTimeout(5000);
                conn.setRequestProperty("Authorization", "TOKEN " + Token);
                conn.connect();

                int responseStatusCode = conn.getResponseCode();
                Log.d("GetAllergie : ", "response code : " + responseStatusCode);

                InputStream inputStream;
                if(responseStatusCode == conn.HTTP_OK || responseStatusCode == 201){         // 연결 성공 시
                    inputStream = conn.getInputStream();
                }
                else {                                          // 연결 실패 시
                    inputStream = conn.getErrorStream();
                }
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line;

                while((line = bufferedReader.readLine()) != null){
                    sb.append(line);
                }

                bufferedReader.close();

                return sb.toString().trim();

            }
            catch (Exception e){
                e.printStackTrace();
                return null;
            }
        }
    }

    private void showResult() {
        try {
            JSONArray jsonArray = new JSONArray(mJsonString);
            JSONObject item = jsonArray.getJSONObject(0);
            String Egg = item.getString("달걀");
            String Milk = item.getString("우유");
            String Wheat = item.getString("밀");
            String Bean = item.getString("콩");
            String Peanut = item.getString("땅콩");
            String Fish = item.getString("생선");
            String Meat = item.getString("고기");
            String Shellfish = item.getString("조개");
            String Crustaceans = item.getString("갑각류");

            String all = "";

            if(Egg.equals("1")) all += "달걀" + ",";
            if(Milk.equals("1")) all += "우유" + ",";
            if(Wheat.equals("1")) all += "밀" + ",";
            if(Bean.equals("1")) all += "콩" + ",";
            if(Peanut.equals("1")) all += "땅콩&대두" + ",";
            if(Fish.equals("1")) all += "생선" + ",";
            if(Meat.equals("1")) all += "고기" + ",";
            if(Shellfish.equals("1")) all += "조개" + ",";
            if(Crustaceans.equals("1")) all += "갑각류";

            String[] hArr = all.split(",");            // ,로 분리해서 배열에 넣어줌
            String aller = "";

            for(int i=0; i<hArr.length; i++){                   // 리턴 변수에 넣어줌
                if(i == hArr.length-1)                          // 배열의 마지막이면 , 안 붙이고 넣음
                    aller += hArr[i];
                else aller += (hArr[i] + ", ");           // 마지막 아니면 뒤에 , 붙여서 넣음
            }

            allergie.setText(aller);

        } catch (JSONException e) {
            Log.d("GetAllergie : ", "showResult : ", e);
        }
    }
}

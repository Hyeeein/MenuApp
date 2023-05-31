package com.example.menuapp_test;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JoinActivity extends AppCompatActivity {
    private static String ADDRESS = "http://52.78.72.175/account/signup";
    private static String ADDRESS_LOGIN = "http://52.78.72.175/account/login";
    private static String ADDRESS_EMAIL = "http://52.78.72.175/account/checkemail";
    private static String ADDRESS_NICKNAME = "http://52.78.72.175/account/checknickname";
    private static String TAG = "duplicatetest";
    private String duplicate, token;           // 중복 검사 결과 리턴 변수
    private boolean echeck, pcheck, ncheck;
    private TextInputEditText email, password, password2, nickname;
    private Button idcheck, pwcheck, namecheck, next;
    private TextView txt_pw;
    private Spinner ageS, genderS;
    private String age, gender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);

        email = findViewById(R.id.join_id);
        password = findViewById(R.id.join_pw);
        password2 = findViewById(R.id.join_pw2);
        nickname = findViewById(R.id.join_name);
        idcheck = findViewById(R.id.btn_join_id);
        pwcheck = findViewById(R.id.btn_join_pw);
        namecheck = findViewById(R.id.btn_join_name);
        next = findViewById(R.id.btn_join_1);
        ageS = findViewById(R.id.spinner_year);
        genderS = findViewById(R.id.spinner_gender);
        txt_pw = findViewById(R.id.txt_join_pw);

        echeck = false; pcheck = false; ncheck = false;

        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                check_validation(password.getText().toString());
            }
            @Override
            public void afterTextChanged(Editable arg0) {
                check_validation(password.getText().toString());
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                check_validation(password.getText().toString());
            }
        });

        idcheck.setOnClickListener(v -> {
            try{
                String Email = email.getText().toString();
                CheckDuplicate checkEmail = new CheckDuplicate(JoinActivity.this);
                checkEmail.execute(ADDRESS_EMAIL, Email, "e");

                duplicate = checkEmail.get();
                if(duplicate.contains("true")) {
                    Toast.makeText(getApplicationContext(), "사용할 수 있는 이메일입니다.", Toast.LENGTH_SHORT).show();
                    echeck = true ;
                }
                else {
                    Toast.makeText(getApplicationContext(), "이미 사용 중인 이메일입니다.", Toast.LENGTH_SHORT).show();
                    email.setText("");
                }
            } catch (Exception e) {
                Log.d("email", "Error ", e);
            }

        });

        namecheck.setOnClickListener(v -> {
            try{
                String Nickname = nickname.getText().toString();
                CheckDuplicate checkNickname = new CheckDuplicate(JoinActivity.this);
                checkNickname.execute(ADDRESS_NICKNAME, Nickname, "n");

                duplicate = checkNickname.get();
                if(duplicate.contains("true")) {
                    Toast.makeText(getApplicationContext(), "사용할 수 있는 닉네임입니다.", Toast.LENGTH_SHORT).show();
                    ncheck = true;
                }
                else {
                    Toast.makeText(getApplicationContext(), "이미 사용 중인 닉네임입니다.", Toast.LENGTH_SHORT).show();
                    nickname.setText("");
                }
            } catch (Exception e) {
                Log.d("nickname", "Error ", e);
            }

        });

        pwcheck.setOnClickListener(v -> {
            if(password.getText().toString().equals(password2.getText().toString())){
                Toast.makeText(JoinActivity.this, "비밀번호가 일치합니다.", Toast.LENGTH_SHORT).show();
                pcheck = true;
            }
            else {
                Toast.makeText(JoinActivity.this, "비밀번호가 다릅니다.", Toast.LENGTH_SHORT).show();
            }
        });
        // spinner
        ArrayAdapter<CharSequence> yearAdapter = ArrayAdapter.createFromResource(this,R.array.출생연도, android.R.layout.simple_spinner_item);
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ageS.setAdapter(yearAdapter);
        ageS.setSelection(0);

        ArrayAdapter<CharSequence> genderAdapter = ArrayAdapter.createFromResource(this,R.array.성별, android.R.layout.simple_spinner_item);
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderS.setAdapter(genderAdapter);
        genderS.setSelection(0);

        ageS.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                age = ageS.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                age = ageS.getSelectedItem().toString();
            }
        });

        genderS.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                gender = genderS.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                gender = genderS.getSelectedItem().toString();
            }
        });

        next.setOnClickListener(view -> {
            if(echeck){
                if(pcheck){
                    if(ncheck){
                        String Email = email.getText().toString();
                        String Password = password.getText().toString();
                        String Nickname = nickname.getText().toString();
                        String Gender = gender;
                        String Age = age;

                        PostSignup task = new PostSignup(JoinActivity.this);
                        task.execute(ADDRESS, Email, Password, Nickname, Gender, Age);

                        PostLogin postLogin = new PostLogin(JoinActivity.this);
                        postLogin.execute(ADDRESS_LOGIN, Email, Password);

                        try{
                            JSONObject jsonObject = new JSONObject(postLogin.get());
                            token = jsonObject.getString("token");
                        }
                        catch (Exception e) {
                            Log.d("token", "Error ", e);
                        }
                        Intent intent = new Intent(getApplicationContext(), JoinAllergieActivity.class);
                        intent.putExtra("token", token);
                        startActivity(intent);
                    }
                    else Toast.makeText(getApplicationContext(), "닉네임 중복을 확인해 주세요.", Toast.LENGTH_SHORT).show();
                }
                else Toast.makeText(getApplicationContext(), "비밀번호가 확인되지 않았습니다.", Toast.LENGTH_SHORT).show();
            }
            else Toast.makeText(getApplicationContext(), "이메일 중복을 확인해 주세요.", Toast.LENGTH_SHORT).show();
        });
    }
    void check_validation(String password) {
        // 비밀번호 유효성 검사식1 : 숫자, 특수문자가 포함되어야 한다.
        String val_symbol = "([0-9].*[!,@,#,^,&,*,(,)])|([!,@,#,^,&,*,(,)].*[0-9])";
        // 비밀번호 유효성 검사식2 : 영문자 대소문자가 적어도 하나씩은 포함되어야 한다.
        String val_alpha = "([a-z].*[A-Z])|([A-Z].*[a-z])";
        // 정규표현식 컴파일
        Pattern pattern_symbol = Pattern.compile(val_symbol);
        Pattern pattern_alpha = Pattern.compile(val_alpha);

        Matcher matcher_symbol = pattern_symbol.matcher(password);
        Matcher matcher_alpha = pattern_alpha.matcher(password);

        if (matcher_symbol.find() && matcher_alpha.find()) {
            txt_pw.setText("");
        }else {
            txt_pw.setText("숫자,영문 대소문자,특수문자를 포함해주세요.");
            txt_pw.setTextColor(Color.parseColor("#DB0000"));
        }
    }
}
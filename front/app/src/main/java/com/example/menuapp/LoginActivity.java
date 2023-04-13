package com.example.menuapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONObject;

import java.util.concurrent.ExecutionException;


public class LoginActivity extends AppCompatActivity {

    private static String ADDRESS = "http://52.78.72.175/account/login";
    private static String TAG = "logintest";

    public String token;
    private String duplicate;
    private TextInputEditText email, password;
    private Button findpw, next, join;
    private TextView txt_result;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email = findViewById(R.id.login_email);
        password = findViewById(R.id.login_pwd);
        findpw = findViewById(R.id.btn_login_find);
        next = findViewById(R.id.btn_login);
        join = findViewById(R.id.btn_login2);
        txt_result = findViewById(R.id.txt_result);

        /*
        findpw.setOnClickListener(v -> {
            Intent intent = new Intent(this, FindPwActivity.class);
            startActivity(intent);
        });
         */

        next.setOnClickListener(v -> {

            try{
                String Email = email.getText().toString();
                String Password = password.getText().toString();
                if(Email.equals("") || Password.equals("")){
                    Toast.makeText(LoginActivity.this, "이메일 또는 비밀번호를 입력해 주세요.", Toast.LENGTH_LONG).show();
                }
                else {
                    PostLogin task = new PostLogin(LoginActivity.this);
                    task.execute(ADDRESS, Email, Password);

                    duplicate = task.get();

                    try{
                        JSONObject jsonObject = new JSONObject(duplicate);
                        token = jsonObject.getString("token");
                    }
                    catch (Exception e) {
                        Log.d("token", "Error ", e);
                    }

                    if(duplicate.contains("success")){
                        Intent intent = new Intent(this, MainActivity.class);
                        intent.putExtra("token", token);
                        startActivity(intent);
                    }
                    else {
                        Toast.makeText(getApplicationContext(), "이메일 또는 비밀번호를 다시 입력해 주세요.", Toast.LENGTH_LONG).show();
                    }
                }
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

        });

        join.setOnClickListener(v -> {
            Intent intent = new Intent(this, JoinActivity.class);
            startActivity(intent);
        });
    }
}
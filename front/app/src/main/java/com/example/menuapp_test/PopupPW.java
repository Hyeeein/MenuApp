package com.example.menuapp_test;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.ExecutionException;


public class PopupPW extends Activity {
    private static String ADDRESS_DELETE = "http://52.78.72.175/account/delete";
    private TextView txt_popup;
    private EditText password;
    private Button ok;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //타이틀바 없애기
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.popup_password);

        txt_popup = findViewById(R.id.txt_popup_pw);
        password = findViewById(R.id.popup_pw);

        ok = findViewById(R.id.btn_ok);

        Intent getIntent = getIntent();
        token = getIntent.getStringExtra("token");

        ok.setOnClickListener(v -> {
            String Password = password.getText().toString();

            Delete delete = new Delete(PopupPW.this);
            delete.execute(ADDRESS_DELETE, Password, token);

            try {
                if(delete.get().contains("delete")){
                    Toast.makeText(getApplicationContext(), "삭제되었습니다.", Toast.LENGTH_SHORT).show();
                    SharedPreferencesManager.clearPreferences(PopupPW.this);
                    Intent intent = new Intent(this, GateActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
                else {
                    Toast.makeText(getApplicationContext(), "잘못된 비밀번호입니다.", Toast.LENGTH_SHORT).show();
                    password.setText("");
                }

            } catch (Exception e) {
                Log.d("popup", "Error ", e);
            }

        });
    }
}

package com.example.menuapp_test;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ReviewWriteActivity extends AppCompatActivity {
    private static String ADDRESS_POST = "http://52.78.72.175/data/review";
    private ImageButton imageButton;
    private ImageView rimg;
    private TextView rname, menu, date;
    private RatingBar ratingbar;
    private ImageView imageView;
    private EditText review;
    private Button image, good, soso, bad, fast, god, save, cancel;
    private float rating;
    private String menuid, rid;
    private String token, comment;
    private Uri uri;
    private Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_write);

        Intent getIntent = getIntent();
        //token = getIntent.getStringExtra("token");
        //menuid = getIntent.getStringExtra("Mid");
        //rid = getIntent.getStringExtra("Rid");

        imageButton = findViewById(R.id.imgbtn_review);
        rimg = findViewById(R.id.rimg_review);
        image = findViewById(R.id.btn_image);
        rname = findViewById(R.id.rname_review);
        menu = findViewById(R.id.menu_review);
        date = findViewById(R.id.date_review);
        ratingbar = findViewById(R.id.review_rating);
        review = findViewById(R.id.edit_review);
        good = findViewById(R.id.btn_good);
        soso = findViewById(R.id.btn_soso);
        bad = findViewById(R.id.btn_bad);
        fast = findViewById(R.id.btn_fast);
        god = findViewById(R.id.btn_god);
        save = findViewById(R.id.btn_save);
        cancel = findViewById(R.id.btn_cancel);
        token = "49e9d8db7d6d31d3623b4af2d3fb97178d6d773e";
        rating = 0;
        menuid = "1";
        rid = "1";
        comment = "";

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        long mNow = System.currentTimeMillis();
        Date mDate = new Date(mNow);
        date.setText(format.format(mDate));

        ratingbar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float r, boolean b) {
                rating = r;
            }
        });

        good.setOnClickListener(v -> {
            comment += good.getText().toString() + " ";
            review.setText(comment);
        });
        soso.setOnClickListener(v -> {
            comment += soso.getText().toString() + " ";
            review.setText(comment);
        });
        bad.setOnClickListener(v -> {
            comment += bad.getText().toString() + " ";
            review.setText(comment);
        });
        fast.setOnClickListener(v -> {
            comment += fast.getText().toString() + " ";
            review.setText(comment);
        });
        god.setOnClickListener(v -> {
            comment += god.getText().toString() + " ";
            review.setText(comment);
        });

        image.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
            intent.setAction(Intent.ACTION_PICK);
            startActivityResult.launch(intent);
        });

        save.setOnClickListener(v -> {
            if(!review.equals("")){
                if(rating>0){
                    String Content = review.getText().toString();
                    String Rating = String.valueOf(rating);
                    String Menuid = String.valueOf(menuid);
                    String Rid = String.valueOf(rid);
                    String image = String.valueOf(uri);
                    PostReview postReview = new PostReview();
                    postReview.execute(ADDRESS_POST, Rating, Content, Menuid, Rid, image, token);
                }
            }
        });

    }
    ActivityResultLauncher<Intent> startActivityResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if(result.getResultCode() == RESULT_OK && result.getData() != null){
                        uri = result.getData().getData();
                        try {
                            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                            imageView.setImageBitmap(bitmap);
                        } catch (FileNotFoundException e){
                            e.printStackTrace();
                        } catch (IOException e){
                            e.printStackTrace();
                        }
                    }
                }
            }
    );
    public class PostReview extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(ReviewWriteActivity.this, "Please Wait", null, true, true);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            progressDialog.dismiss();
            Log.d("PostReview", "POST response - " + result);
        }

        @Override
        protected String doInBackground(String... params) {
            String Rating = params[1];
            String Content = params[2];
            String Menu = params[3];
            String Restaurant = params[4];
            String Image = params[5];
            String Token = params[6];

            String serverURL = params[0];

            try {
                URL url = new URL(serverURL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                conn.setReadTimeout(5000);
                conn.setConnectTimeout(5000);
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Authorization", "TOKEN " + Token);
                conn.setRequestMethod("POST");
                conn.connect();

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("rating", Rating);
                jsonObject.put("content", Content);
                jsonObject.put("menu", Menu);
                jsonObject.put("restaurant", Restaurant);
                jsonObject.put("image", Image);

                OutputStream outputStream = conn.getOutputStream();
                outputStream.write(jsonObject.toString().getBytes());
                outputStream.flush();
                outputStream.close();

                int responseStatusCode = conn.getResponseCode();
                Log.d("ReviewWrite", "POST response code - " + responseStatusCode);

                InputStream inputStream;
                if (responseStatusCode == conn.HTTP_OK || responseStatusCode == 201) {
                    inputStream = conn.getInputStream();
                }
                else {
                    inputStream = conn.getErrorStream();
                }

                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line = null;

                while((line = bufferedReader.readLine())!= null)  {
                    sb.append(line);
                }

                bufferedReader.close();
                return sb.toString();
            }
            catch (Exception e) {
                Log.d("ReviewWrite", "InsertSignup : Error ", e);
                return new String("Error: " + e.getMessage());
            }
        }
    }

}









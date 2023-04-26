package com.example.menuapp_test;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class ReviewUserActivity extends AppCompatActivity {
    private static String ADDRESS_DELETE = "http://52.78.72.175/data/review/delete/";
    private static String ADDRESS_USER = "http://52.78.72.175/data/review/user";
    private ListView listView;
    private ReviewUserAdapter adapter;
    private String token, rid, JsonString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_user);

        Intent getIntent = getIntent();
        //token = getIntent.getStringExtra("token");
        token = "49e9d8db7d6d31d3623b4af2d3fb97178d6d773e";

        listView = findViewById(R.id.listv_review_user);

        // 사용자 리뷰 받아와서 리스트뷰에 추가
        APIReviewUser getReview = new APIReviewUser(ReviewUserActivity.this);
        getReview.execute(ADDRESS_USER, "GET", token);

        try {
            JsonString = getReview.get();
            JSONArray jsonArray = new JSONArray(JsonString);
            adapter = new ReviewUserAdapter();

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject item = jsonArray.getJSONObject(i);
                int id = Integer.parseInt(item.getString("id"));
                String content = item.getString("content");
                String datetime = item.getString("datetime");

                JSONObject user = (JSONObject) item.get("user");
                String nickname = user.getString("nickname");

                JSONObject menu = (JSONObject) item.get("menu");
                String menuname = menu.getString("name");

                JSONObject restaurant = (JSONObject) item.get("restaurant");
                String rname = restaurant.getString("name");

                String image = ""; float rating = 0;
                if(!item.getString("image").equals("null"))
                    image = item.getString("image");
                else image = "null";
                if(!item.getString("rating").equals("null"))
                    rating = Float.parseFloat(item.getString("rating"));
                else rating = 0;

                adapter.addReviewItem(id, rating, content, datetime, nickname, menuname, rname, "http://52.78.72.175" + image);
            }

            listView.setAdapter(adapter);

        } catch (JSONException e) {
            Log.d("GetReview", "showResult : ", e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    class ReviewUserAdapter extends BaseAdapter {
        private ArrayList<ReviewItem> reviewItems = new ArrayList<ReviewItem>();
        private Bitmap bitmap;
        public ReviewUserAdapter(){    }

        @Override
        public int getCount(){
            return reviewItems.size();
        }
        @Override
        public long getItemId(int position) {
            return position;
        }
        @Override
        public Object getItem(int position) {
            return reviewItems.get(position);
        }
        @Override
        public View getView(int position, View view, ViewGroup parent){
            Context context = parent.getContext();

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.item_review_user, parent, false);

            TextView rname = view.findViewById(R.id.rname_review_user);
            ImageButton trash = view.findViewById(R.id.btn_trash);
            TextView menu = view.findViewById(R.id.menu_review_user);
            TextView content = view.findViewById(R.id.txt_review_user);
            TextView datetime = view.findViewById(R.id.date_review_user);
            RatingBar rating = view.findViewById(R.id.rating_user);
            ImageView imageView = (ImageView) view.findViewById(R.id.img_review_user);

            ReviewItem reviewItem = reviewItems.get(position);

            rname.setText(reviewItem.getRname());
            content.setText(reviewItem.getContent());
            datetime.setText(reviewItem.getDatetime());
            menu.setText(reviewItem.getMenuname());
            rating.setRating(reviewItem.getRating());
            String id = String.valueOf(reviewItem.getId());

            trash.setOnClickListener(v -> {
                rid = String.valueOf(reviewItem.getId());

                APIReviewUser deleteReview = new APIReviewUser(ReviewUserActivity.this);
                deleteReview.execute(ADDRESS_DELETE + rid, "DELETE", token);

                try {
                    if(deleteReview.get().contains("success")){
                        reviewItems.remove(position);
                        listView.clearChoices();
                        adapter.notifyDataSetChanged();
                    }
                } catch (ExecutionException e) {
                    throw new RuntimeException(e);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

            });

            if(!reviewItem.getImage().equals("http://52.78.72.175null")){
                Thread thread = new Thread() {
                    @Override
                    public void run(){
                        try {
                            URL url = new URL(reviewItem.getImage());
                            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                            conn.setDoInput(true);
                            conn.connect();

                            InputStream is = conn.getInputStream();
                            bitmap = BitmapFactory.decodeStream(is);
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                };
                thread.start();
                try {
                    thread.join();
                    imageView.setImageBitmap(bitmap);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            else imageView.setVisibility(View.GONE);

            return view;
        }
        public void addReviewItem(int id, float rating, String content, String datetime, String nickname, String menuname, String rname, String image){
            ReviewItem item = new ReviewItem();
            item.setId(id);
            item.setRating(rating);
            item.setContent(content);
            item.setDatetime(datetime);
            item.setNickname(nickname);
            item.setMenuname(menuname);
            item.setRname(rname);
            item.setImage(image);
            reviewItems.add(item);
        }

        public void removeItem(int position) {
            reviewItems.remove(position);
        }

    }
}



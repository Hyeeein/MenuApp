package com.example.menuapp_test;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class ReviewAdapter extends BaseAdapter {
    private ArrayList<ReviewItem> reviewItems = new ArrayList<ReviewItem>();
    private Bitmap bitmap;
    public ReviewAdapter(){
    }

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
        view = inflater.inflate(R.layout.item_review, parent, false);

        TextView content = view.findViewById(R.id.txt_review_show4);
        TextView nickname = view.findViewById(R.id.name_review_show);
        TextView datetime = view.findViewById(R.id.date_review_show);
        TextView menu = view.findViewById(R.id.menu_review_show);
        RatingBar rating = view.findViewById(R.id.rating_show);
        ImageView imageView = (ImageView) view.findViewById(R.id.img_review_show);

        ReviewItem reviewItem = reviewItems.get(position);

        content.setText(reviewItem.getContent());
        nickname.setText(reviewItem.getNickname());
        datetime.setText(reviewItem.getDatetime());
        menu.setText(reviewItem.getMenuname());
        rating.setRating(reviewItem.getRating());


        if(!reviewItem.getImage().equals("null")){
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
    public void addReviewItem(int id, float rating, String content, String datetime, String nickname, String menuname, String image){
        ReviewItem item = new ReviewItem();
        item.setId(id);
        item.setRating(rating);
        item.setContent(content);
        item.setDatetime(datetime);
        item.setNickname(nickname);
        item.setMenuname(menuname);
        item.setImage(image);
        reviewItems.add(item);
    }

}

package com.example.menuapp_test;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class MAdapter extends BaseAdapter {
    private ArrayList<MenuItem> menuItems = new ArrayList<MenuItem>();
    private Bitmap bitmap;
    public MAdapter(){
    }

    @Override
    public int getCount(){
        return menuItems.size();
    }
    @Override
    public long getItemId(int position) {
        return position;
    }
    @Override
    public Object getItem(int position) {
        return menuItems.get(position);
    }
    @Override
    public View getView(int position, View view, ViewGroup parent){
        Context context = parent.getContext();

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.item_menulist, parent, false);

        ImageView imageView = (ImageView) view.findViewById(R.id.img_menulist);
        TextView name = (TextView) view.findViewById(R.id.mname_menulist);
        TextView price = view.findViewById(R.id.price_menulist);
        TextView allergie = view.findViewById(R.id.allergie_menulist);

        MenuItem menuItem = menuItems.get(position);

        name.setText(menuItem.getName());
        price.setText(menuItem.getPrice());

        if(menuItem.getCheckallergy()) allergie.setText("알레르기 주의!");

        if(!menuItem.getImage().equals("null")){
            Thread thread = new Thread() {
                @Override
                public void run(){
                    try {
                        URL url = new URL(menuItem.getImage());
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
        return view;
    }

    public void addRItem(int id, int restaurant, String category, String name, String price, String emotion, String weather, String image, boolean chechallergy){
        MenuItem item = new MenuItem();
        item.setId(id);
        item.setRestaurant(restaurant);
        item.setCategory(category);
        item.setName(name);
        item.setPrice(price);
        item.setEmotion(emotion);
        item.setWeather(weather);
        item.setImage(image);
        item.setCheckallergy(chechallergy);
        menuItems.add(item);
    }
}

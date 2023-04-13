package com.example.menuapp;

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

public class RAdapter extends BaseAdapter {
    private ArrayList<ListItem> listItems = new ArrayList<ListItem>();
    private Bitmap bitmap;
    public RAdapter(){
    }

    @Override
    public int getCount(){
        return listItems.size();
    }
    @Override
    public long getItemId(int position) {
        return position;
    }
    @Override
    public Object getItem(int position) {
        return listItems.get(position);
    }
    @Override
    public View getView(int position, View view, ViewGroup parent){
        Context context = parent.getContext();

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.item_list, parent, false);

        ImageView imageView = (ImageView) view.findViewById(R.id.img_list);
        TextView name = (TextView) view.findViewById(R.id.rname_list);
        TextView address = (TextView) view.findViewById(R.id.address_item_list);
        TextView category_name = (TextView) view.findViewById(R.id.category_list);

        ListItem listItem = listItems.get(position);

        name.setText(listItem.getName());
        address.setText(listItem.getAddress());
        category_name.setText(listItem.getCategory_name());

        Thread thread = new Thread() {
            @Override
            public void run(){
                try {
                    URL url = new URL(listItem.getImage());
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

        return view;
    }

    public void addRItem(int id, String name, String address, String category_name, String image){
        ListItem item = new ListItem();
        item.setId(id);
        item.setName(name);
        item.setAddress(address);
        item.setCategory_name(category_name);
        item.setImage(image);
        listItems.add(item);
    }

}

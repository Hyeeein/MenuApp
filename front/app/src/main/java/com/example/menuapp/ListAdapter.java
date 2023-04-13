package com.example.menuapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class ListAdapter extends BaseAdapter {
    Context mContext = null;
    LayoutInflater mLayoutInflater = null;
    ArrayList<ListItem> listItems;

    public ListAdapter(Context context, ArrayList<ListItem> item){
        mContext = context;
        listItems = item;
        mLayoutInflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() { return listItems.size(); }

    @Override
    public long getItemId(int position) { return position; }

    @Override
    public ListItem getItem(int position) { return listItems.get(position); }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = mLayoutInflater.inflate(R.layout.item_list, null);
        ImageView image = (ImageView) view.findViewById(R.id.img_list);
        TextView name = (TextView) view.findViewById(R.id.rname_list);
        TextView category = (TextView) view.findViewById(R.id.category_list);
        TextView address = (TextView) view.findViewById(R.id.address_item_list);

        image.setImageResource(listItems.get(position).getImage());
        name.setText(listItems.get(position).getName());
        category.setText(listItems.get(position).getCategory_name());
        address.setText(listItems.get(position).getAddress());

        return view;
    }
}

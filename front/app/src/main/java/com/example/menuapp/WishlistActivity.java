package com.example.menuapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class WishlistActivity extends AppCompatActivity {
    ImageButton back, heart;                     // 찜하기 버튼 어떻게 구현 ??

    private String TAG = ListActivity.class.getSimpleName();
    private ListView listview = null;
    private ListViewAdapter adapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wishlist);

        back = findViewById(R.id.imgbtn_wishlist);
        back.setOnClickListener(v -> {
            Intent intent = new Intent(this, MypageActivity.class);
            startActivity(intent);
        });

        listview = (ListView) findViewById(R.id.listv_wishlist);
        adapter = new ListViewAdapter();

        adapter.addItem(new WishItem(R.drawable.hamburger, "맘스터치 인천대점"));

        listview.setAdapter(adapter);
    }

    public class ListViewAdapter extends BaseAdapter {
        ArrayList<WishItem> items = new ArrayList<WishItem>();

        @Override
        public int getCount() {
            return items.size();
        }

        public void addItem(WishItem item) {
            items.add(item);
        }
        @Override
        public Object getItem(int position) {
            return items.get(position);
        }
        @Override
        public long getItemId(int position) {
            return position;
        }
        @Override
        public View getView(int position, View convertView, ViewGroup viewGroup) {
            final Context context = viewGroup.getContext();
            final WishItem wishitem = items.get(position);

            if(convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.item_wishlist, viewGroup, false);
            }
            else {
                View view = new View(context);
                view = (View) convertView;
            }

            ImageView iv_rimg = (ImageView) convertView.findViewById(R.id.img_wishlist);
            TextView tv_rname = (TextView) convertView.findViewById(R.id.rname_wishlist);

            iv_rimg.setImageResource(wishitem.getResId());
            tv_rname.setText(wishitem.getRname());
            Log.d(TAG, "getView() - ["+position+" ] "+wishitem.getRname());

            // 각 아이템 선택 이벤트 처리
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void OnClick(View view) {
                    Intent intent = new Intent(context, RestaurantActivity.class);
                    startActivity(intent);
                }
            });
            return convertView;
        }

    }
}


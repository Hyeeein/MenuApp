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

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class RecordActivity extends AppCompatActivity {
    ImageButton back, heart;                     // 찜하기 버튼 어떻게 구현 ??

    private String TAG = ListActivity.class.getSimpleName();
    private ListView listview = null;
    private RecordActivity.ListViewAdapterRecord adapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);

        back = findViewById(R.id.imgbtn_record);
        back.setOnClickListener(v -> {
            Intent intent = new Intent(this, MypageActivity.class);
            startActivity(intent);
        });

        listview = (ListView) findViewById(R.id.listv_record);
        adapter = new RecordActivity.ListViewAdapterRecord();

        adapter.addItem(new RecordItem(R.drawable.hamburger, "맘스터치 인천대점"));

        listview.setAdapter(adapter);
    }

    public class ListViewAdapterRecord extends BaseAdapter {
        ArrayList<RecordItem> items = new ArrayList<RecordItem>();

        @Override
        public int getCount() {
            return items.size();
        }
        public void addItem(RecordItem item) {
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
            final RecordItem recorditem = items.get(position);

            if(convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.item_record, viewGroup, false);
            }
            else {
                View view = new View(context);
                view = (View) convertView;
            }

            ImageView iv_rimg = (ImageView) convertView.findViewById(R.id.img_record);
            TextView tv_rname = (TextView) convertView.findViewById(R.id.rname_record);

            iv_rimg.setImageResource(recorditem.getResId());
            tv_rname.setText(recorditem.getRname());
            Log.d(TAG, "getView() - ["+position+" ] "+recorditem.getRname());

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

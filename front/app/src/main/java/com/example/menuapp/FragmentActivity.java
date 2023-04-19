package com.example.menuapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Binder;
import android.os.Bundle;
import android.renderscript.ScriptGroup;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.Arrays;
import java.util.List;

public class FragmentActivity extends AppCompatActivity {
    private ViewPager2 mViewPager;
    private FragmentAdapter pagerAdapter;
    private TabLayout mTabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);

        mTabLayout = (TabLayout) findViewById(R.id.tabs);
        mViewPager = (ViewPager2) findViewById(R.id.viewpager);

        FragmentAdapter fragmentAdapter = new FragmentAdapter(this);
        mViewPager.setAdapter(fragmentAdapter);

        final List<String> tabElement = Arrays.asList("정보", "메뉴", "리뷰");

        new TabLayoutMediator(mTabLayout, mViewPager, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                TextView textView = new TextView(FragmentActivity.this);
                textView.setText(tabElement.get(position));
                tab.setCustomView(textView);
            }
        }).attach();
    }
}
package com.example.menuapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
public class FragmentAdapter extends FragmentStateAdapter {

    private int mFragmentCount = 3;
    public FragmentAdapter(AppCompatActivity activity) {
        super(activity);
    }
    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0 :
                Fragment_1 fragment1 = new Fragment_1();
                return fragment1;
            case 1 :
                Fragment_2 fragment2 = new Fragment_2();
                return fragment2;
            case 2 :
                Fragment_3 fragment3 = new Fragment_3();
                return fragment3;
            default :
                return null;
        }
    }

    @Override
    public int getItemCount() {
        return mFragmentCount;
    }
}

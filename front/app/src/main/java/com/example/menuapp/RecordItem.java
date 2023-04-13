package com.example.menuapp;

// wishlist 아이템의 정보를 담기 위한 클래스
public class RecordItem {
    String rname;
    int resId;

    public RecordItem(int resId, String rname) {
        this.rname = rname;
        this.resId = resId;
    }

    public String getRname() {
        return rname;
    }
    public void setRname(String rname) {
        this.rname = rname;
    }
    public int getResId() {
        return resId;
    }
    public void setResId(int resId) {
        this.resId = resId;
    }
}

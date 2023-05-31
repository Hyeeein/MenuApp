package com.example.menuapp_test;

import java.io.Serializable;

public class ListItem implements Serializable {
    private int id;
    private String name;
    private String address;
    private String business_hours;
    private String phone_number;
    private String category_name;
    private String image;
    private String rating;
    private boolean wish;
    private int distance;
    private String count;
    private String latitude;
    private String longitude;

    public String getName(){
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCategory_name() {
        return category_name;
    }

    public void setCategory_name(String category_name) {
        this.category_name = category_name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getBusiness_hours() {
        return business_hours;
    }

    public void setBusiness_hours(String business_hours) {
        this.business_hours = business_hours;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public boolean getWish() {
        return wish;
    }

    public void setWish(boolean wish) {
        this.wish = wish;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }
}

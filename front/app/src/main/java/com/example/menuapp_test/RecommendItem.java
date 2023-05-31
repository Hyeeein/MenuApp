package com.example.menuapp_test;

import java.io.Serializable;

public class RecommendItem implements Serializable {
    private int id;
    private int restaurant;
    private String name;
    private String Rname;
    private String category;
    private int price;
    private String image;

    public RecommendItem(int id, int restaurant, String name, int price, String Rname, String image){
        this.id = id;
        this.restaurant = restaurant;
        this.name = name;
        this.price = price;
        this.Rname = Rname;
        this.image = image;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRestaurant() {
        return restaurant;
    }

    public void setRestaurant(int restaurant) {
        this.restaurant = restaurant;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getRname() {
        return Rname;
    }

    public void setRname(String rname) {
        Rname = rname;
    }

}

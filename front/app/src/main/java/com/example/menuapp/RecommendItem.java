package com.example.menuapp;

public class RecommendItem {
    private int id;
    private int restaurant;
    private String name;
    private String category;
    private String image;

    public RecommendItem(int id, int restaurant, String name, String category, String image){
        this.id = id;
        this.restaurant = restaurant;
        this.name = name;
        this.category = category;
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
}

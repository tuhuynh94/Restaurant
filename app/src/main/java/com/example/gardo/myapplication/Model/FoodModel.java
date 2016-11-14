package com.example.gardo.myapplication.Model;

public class FoodModel implements Comparable<FoodModel>{
    private Integer quantity;
    private String name;
    private Integer img;
    private Double price;
    private Long like;

    public FoodModel(String name, Integer img, Double price, Integer quantity) {
        this.name = name;
        this.img = img;
        this.price = price;
        this.quantity = quantity;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getImg() {
        return img;
    }

    public void setImg(Integer img) {
        this.img = img;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Long getLike() {
        return like;
    }

    public void setLike(Long like) {
        this.like = like;
    }

    @Override
    public int compareTo(FoodModel foodModel) {
        return foodModel.getLike().compareTo(this.getLike());
    }
}

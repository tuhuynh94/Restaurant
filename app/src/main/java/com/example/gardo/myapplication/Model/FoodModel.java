package com.example.gardo.myapplication.Model;

public class FoodModel implements Comparable<FoodModel>{
    private String catagory;
    private Integer quantity;
    private String name;
    private String img;
    private Double price;
    private Long like;

    public FoodModel(String name, String img, Double price, Integer quantity, String catagory) {
        this.name = name;
        this.img = img;
        this.price = price;
        this.quantity = quantity;
        this.catagory = catagory;
    }

    public String getCatagory() {
        return catagory;
    }

    public void setCatagory(String catagory) {
        this.catagory = catagory;
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

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
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

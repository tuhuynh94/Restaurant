package com.example.gardo.myapplication.Model;

import java.util.ArrayList;

/**
 * Created by gardo on 21/11/2016.
 */

public class CatagoryFood {
    public String Name;
    public ArrayList<FoodModel> food = new ArrayList<FoodModel>();

    public ArrayList<FoodModel> getFood() {
        return food;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public void setFood(ArrayList<FoodModel> food) {
        this.food = food;
    }

    public CatagoryFood(String Name){
        this.Name =Name;
    }

    @Override
    public String toString() {
        return Name;
    }
}

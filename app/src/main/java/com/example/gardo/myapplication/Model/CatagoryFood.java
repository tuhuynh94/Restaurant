package com.example.gardo.myapplication.Model;

import java.util.ArrayList;

/**
 * Created by gardo on 21/11/2016.
 */

public class CatagoryFood {
    public String Name;
    public ArrayList<FoodModel> food = new ArrayList<FoodModel>();

    public CatagoryFood(String Name){
        this.Name =Name;
    }

    @Override
    public String toString() {
        return Name;
    }
}

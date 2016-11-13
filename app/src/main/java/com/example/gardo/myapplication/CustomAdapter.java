package com.example.gardo.myapplication;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.example.gardo.myapplication.Model.FoodModel;

import java.util.ArrayList;

/**
 * Created by gardo on 12/11/2016.
 */

public class CustomAdapter extends BaseAdapter {
    Context context;
    ArrayList<FoodModel> foods;
    @Override
    public int getCount() {
        return foods.size();
    }

    @Override
    public Object getItem(int position) {
        return foods.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        return view;
    }
}

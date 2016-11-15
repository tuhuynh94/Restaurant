package com.example.gardo.myapplication.Model;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;

import com.example.gardo.myapplication.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

/**
 * Created by gardo on 14/11/2016.
 */
public class OrderGridModel extends ArrayAdapter<FoodModel> {
    private final Activity context;
    private final ArrayList<FoodModel> food;

    public OrderGridModel(Activity context, ArrayList<FoodModel> food) {
        super(context, R.layout.item_order_layout, food);
        this.context = context;
        this.food = food;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        final View rowView = inflater.inflate(R.layout.item_order_layout, null, true);
        GridView grid = (GridView) rowView.findViewById(R.id.order_single);
        FoodModel item = food.get(position);
        String array[] = {item.getName(), item.getQuantity() + "", (item.getPrice()*item.getQuantity()) + ""};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(rowView.getContext(), android.R.layout.simple_list_item_1, array);
        grid.setAdapter(adapter);
        return rowView;
    }
}

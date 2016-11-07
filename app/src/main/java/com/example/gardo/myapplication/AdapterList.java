package com.example.gardo.myapplication;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.ArrayList;

public class AdapterList extends ArrayAdapter<String> {
    private final Activity context;
    private final String[] food;
    private final Integer[] imageId;
    private Button increase, decrease;
    private ToggleButton like, favorite;
    private TextView number;
    private ArrayList<Integer> quantity = new ArrayList<>();
    public AdapterList(Activity context,
                      String[] food, Integer[] imageId) {
        super(context, R.layout.list_single_item, food);
        this.context = context;
        this.food = food;
        this.imageId = imageId;
        for (int i = 0; i < food.length; i++) {
            quantity.add(0);
        }

    }
    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        final View rowView= inflater.inflate(R.layout.list_single_item, null, true);
        TextView txtTitle = (TextView) rowView.findViewById(R.id.txt);
        number = (TextView) rowView.findViewById(R.id.quantity);
        like = (ToggleButton) rowView.findViewById(R.id.like);
        RelativeLayout item = (RelativeLayout) rowView.findViewById(R.id.single_item);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.img);
        txtTitle.setText(food[position]);
        imageView.setImageResource(imageId[position]);
        number.setText(Integer.toString(quantity.get(position)));
        item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(rowView.getContext(), "You Clicked at " + food[position], Toast.LENGTH_SHORT).show();
            }
        });
        return rowView;
    }
}

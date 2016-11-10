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
    private final Double[] price;
    private ArrayList<Integer> quantity = new ArrayList<>();
    public AdapterList(Activity context,
                      String[] food, Integer[] imageId, Double[] price) {
        super(context, R.layout.list_single_item, food);
        this.context = context;
        this.food = food;
        this.imageId = imageId;
        this.price = price;
        for (int i = 0; i < food.length; i++) {
            quantity.add(0);
        }

    }
    public class Holder {
        private Button increase, decrease;
        private ToggleButton like, favorite;
        private TextView number;
        RelativeLayout item;
        ImageView img;
        TextView txtTitle, price_text;
    }
    @Override
    public View getView(final int position, View view, final ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        final View rowView= inflater.inflate(R.layout.list_single_item, null, true);
        final Holder holder = new Holder();
        holder.txtTitle = (TextView) rowView.findViewById(R.id.txt);
        holder.number = (TextView) rowView.findViewById(R.id.quantity);
        holder.like = (ToggleButton) rowView.findViewById(R.id.like);
        holder.item = (RelativeLayout) rowView.findViewById(R.id.single_item);
        holder.img = (ImageView) rowView.findViewById(R.id.img);
        holder.price_text = (TextView) rowView.findViewById(R.id.price);
        holder.txtTitle.setText(food[position]);
        holder.img.setImageResource(imageId[position]);
        holder.price_text.setText("$" + price[position]);
        holder.number.setText(Integer.toString(quantity.get(position)));
        holder.item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(rowView.getContext(), "You Clicked at " + food[position], Toast.LENGTH_SHORT).show();
            }
        });
        holder.increase = (Button) rowView.findViewById(R.id.increase);
        holder.decrease = (Button) rowView.findViewById(R.id.decrease);
        holder.increase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                quantity.set(position, quantity.get(position)+1);
                holder.number.setText(Integer.toString(quantity.get(position)));
            }
        });
        holder.decrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                quantity.set(position, quantity.get(position)-1);
                holder.number.setText(Integer.toString(quantity.get(position)));
            }
        });
        return rowView;
    }
}

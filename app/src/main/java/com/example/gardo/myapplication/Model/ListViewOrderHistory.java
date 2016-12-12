package com.example.gardo.myapplication.Model;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.gardo.myapplication.R;

import java.util.ArrayList;

/**
 * Created by gardo on 11/12/2016.
 */

public class ListViewOrderHistory extends ArrayAdapter<String> {
    private final Activity context;
    private final ArrayList<String> order;
    public ListViewOrderHistory(Activity context, ArrayList<String> order) {
        super(context, R.layout.order_history_item, order);
        this.order = order;
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        final View rowView = inflater.inflate(R.layout.order_history_item, null, true);
        TextView name = (TextView) rowView.findViewById(R.id.name_order_history);
        TextView date = (TextView) rowView.findViewById(R.id.date_history);
        name.setText("Order");
        date.setText(order.get(position));
        return rowView;
    }
}

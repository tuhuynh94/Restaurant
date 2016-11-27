package com.example.gardo.myapplication.Model;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;

import com.example.gardo.myapplication.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

/**
 * Created by gardo on 14/11/2016.
 */
public class OrderGridModel extends ArrayAdapter<FoodModel> {
    private final Activity context;
    private final ArrayList<FoodModel> food;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    public OrderGridModel(Activity context, ArrayList<FoodModel> food) {
        super(context, R.layout.item_order_layout, food);
        this.context = context;
        this.food = food;
    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        final View rowView = inflater.inflate(R.layout.item_order_layout, null, true);
        GridView grid = (GridView) rowView.findViewById(R.id.order_single);
        FoodModel item = food.get(position);
        String array[] = {item.getName(), item.getQuantity() + "", (item.getPrice()*item.getQuantity()) + ""};
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(rowView.getContext(), R.layout.simple_text_view2, array);
        grid.setAdapter(adapter);
        Button delete = (Button) rowView.findViewById(R.id.delete_order);
        final FirebaseUser userFire = mAuth.getCurrentUser();
        final DatabaseReference userOrder = mDatabase.child("user").child(userFire.getUid()).child("order");
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userOrder.child(food.get(position).getName()).removeValue();
                food.remove(position);
                adapter.notifyDataSetChanged();
            }
        });
        return rowView;
    }
}

package com.example.gardo.myapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.gardo.myapplication.Model.DiaglogModel;
import com.example.gardo.myapplication.Model.FoodModel;
import com.example.gardo.myapplication.Model.OrderGridModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

public class OrderActivity extends AppCompatActivity {
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    ArrayList<String> list;
    ArrayAdapter<FoodModel> adapter;
    ArrayList<FoodModel> array;
    ListView list_order;
    double total_d;
    double discount_d;
    double grand_total_d;
    TextView total, grand_total, discount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_layout);
        list_order = (ListView) findViewById(R.id.list_order);
        total = (TextView) findViewById(R.id.total);
        grand_total = (TextView) findViewById(R.id.grand_total);
        discount = (TextView) findViewById(R.id.discount);
        GridView gridView = (GridView) findViewById(R.id.grid_description);
        String array1[] = {"Name", "Quantity", "Price"};
        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, array1);
        gridView.setAdapter(adapter1);
        total_d = 0;
        discount_d = 0;
        final HashSet<FoodModel> temp = new HashSet<>();
        list = new ArrayList<String>();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        DatabaseReference user = mDatabase.child("user").child(mAuth.getCurrentUser().getUid()).child("order");
        user.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterator iterator = dataSnapshot.getChildren().iterator();
                while (iterator.hasNext()) {
                    Map<String, Object> map = (Map<String, Object>) ((DataSnapshot) iterator.next()).getValue();
                    String name = (String) map.get("name");
                    String img = String.valueOf(map.get("img"));
                    Double price = Double.valueOf(String.valueOf(map.get("price")));
                    Integer quantity = Integer.valueOf(String.valueOf(map.get("quantity")));
                    String catagory = (String) map.get("catagory");
                    FoodModel item = new FoodModel(name, img, price, quantity, catagory);
                    total_d += price * quantity;
                    temp.add(item);
                }
                StringBuilder log = new StringBuilder();
                array = new ArrayList<FoodModel>(temp);
                adapter = new OrderGridModel(OrderActivity.this, array);
                grand_total_d = discount_d + total_d;
                total.setText(total_d + "");
                discount.setText(discount_d + "");
                grand_total.setText(grand_total_d + "");
                list_order.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        final Button confirm = (Button) findViewById(R.id.confirm_order);
        Button checkout = (Button) findViewById(R.id.checkout);
        checkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(OrderActivity.this, CheckoutActivity.class);
                startActivity(i);
            }
        });
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DiaglogModel myDiaglog = new DiaglogModel();
                myDiaglog.show(getFragmentManager(), "show_diaglod");
            }
        });
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Table");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                if(map!= null && map.containsValue(mAuth.getCurrentUser().getUid())){
                    confirm.setText("CHANGE TABLE");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}

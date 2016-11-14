package com.example.gardo.myapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.gardo.myapplication.Model.FoodModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

public class OrderActivity extends AppCompatActivity {
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    ArrayList<String> list;
    ArrayAdapter<String> adapter;
    String[] array;
    ListView list_order;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_layout);
        list_order = (ListView) findViewById(R.id.list_order);
        TextView total = (TextView) findViewById(R.id.total);
        TextView grand_total = (TextView) findViewById(R.id.grand_total);
        TextView discount = (TextView) findViewById(R.id.discount);
        double total_d = 0;
        double discount_d = 0;
        final HashSet<String> temp = new HashSet<>();
        list = new ArrayList<String>();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        DatabaseReference user = mDatabase.child("user").child(mAuth.getCurrentUser().getUid()).child("order");
        user.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterator iterator = dataSnapshot.getChildren().iterator();
                while (iterator.hasNext()){
                    Map<String, Object> map = (Map<String, Object>) ((DataSnapshot)iterator.next()).getValue();
                    String name = (String) map.get("name");
                    Integer img = Integer.valueOf(String.valueOf(map.get("img")));
                    Double price = Double.valueOf(String.valueOf(map.get("price")));
                    Integer quantity = Integer.valueOf(String.valueOf(map.get("quantity")));
                    temp.add(name + "\t" + quantity + "\t" + price);
                }
                StringBuilder log = new StringBuilder();
                array = new String[temp.size()];
                temp.toArray(array);
                Arrays.sort(array);
                adapter = new ArrayAdapter<String>(OrderActivity.this, R.layout.item_order_layout, array);
                list_order.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}

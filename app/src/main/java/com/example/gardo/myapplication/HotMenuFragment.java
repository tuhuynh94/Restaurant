package com.example.gardo.myapplication;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.gardo.myapplication.Model.FoodModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class HotMenuFragment extends Fragment {
    DatabaseReference mDatabase;
    FirebaseDatabase database;
    FirebaseAuth mAuth;
    private ArrayList<FoodModel> foodList;
    private ArrayList<FoodModel> hotList;
    private AdapterList adapter;
    ListView list_view;
    private int sizeHotMenu = 2;


    public HotMenuFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        View root = inflater.inflate(R.layout.fragment_hot_menu, container, false);
        final DatabaseReference foodRef = mDatabase.child("food");
        foodList = new ArrayList<>();
        hotList = new ArrayList<>();
        adapter = new AdapterList(this.getActivity(), hotList);
        list_view = (ListView) root.findViewById(R.id.list_hot_menu);
        list_view.setAdapter(adapter);
        foodRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterator iterator = dataSnapshot.getChildren().iterator();
                while (iterator.hasNext()) {
                    Map<String, Object> map = (Map<String, Object>) ((DataSnapshot) iterator.next()).getValue();
                    String name = (String) map.get("name");
                    Integer img = Integer.valueOf(String.valueOf(map.get("img")));
                    Double price = Double.valueOf(String.valueOf(map.get("price")));
                    Long like = Long.valueOf(String.valueOf(map.get("like")));
                    FoodModel item = new FoodModel(name, img, price, 0);
                    item.setLike(like);
                    foodList.add(item);
                }
                Collections.sort(foodList);
                StringBuilder res1 = new StringBuilder();
                StringBuilder res2 = new StringBuilder();
                for (FoodModel e : foodList) {
                    res1.append(e.getName() + "\n");
                }
                Log.v("Hot", res1.toString());
                for (int i = 0; i < sizeHotMenu; i++) {
                    hotList.add(foodList.get(i));
                }
                Log.v("Hot", res2.toString());
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return root;
    }
}

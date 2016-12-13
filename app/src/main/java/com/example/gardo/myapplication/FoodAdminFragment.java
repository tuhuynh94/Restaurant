package com.example.gardo.myapplication;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ListView;

import com.example.gardo.myapplication.Model.CatagoryFood;
import com.example.gardo.myapplication.Model.ExpandListAdapter;
import com.example.gardo.myapplication.Model.ExpandListAdapterAdmin;
import com.example.gardo.myapplication.Model.FoodModel;
import com.example.gardo.myapplication.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class FoodAdminFragment extends Fragment {
    ExpandableListView expandableListView;
    ArrayList<FoodModel> foodList;
    ArrayList<CatagoryFood> catagoryFoods;
    CatagoryFood main,dessert,drink;
    ExpandListAdapterAdmin adapter;
    private DatabaseReference mDatabase;
    Context mContext;

    public FoodAdminFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_food_admin, container, false);
        expandableListView = (ExpandableListView) root.findViewById(R.id.food_admin);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            int previousGroup = -1;

            @Override
            public void onGroupExpand(int groupPosition) {
                // Collapse previous parent if expanded.
                if ((previousGroup != -1) && (groupPosition != previousGroup)) {
                    expandableListView.collapseGroup(previousGroup);
                }
                previousGroup = groupPosition;
            }
        });
        final DatabaseReference foodRef = mDatabase.child("food");
        int width = getResources().getDisplayMetrics().widthPixels;
        expandableListView.setIndicatorBounds(width-GetDipsFromPixel(35), width-GetDipsFromPixel(5));
        catagoryFoods = new ArrayList<>();
        foodRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterator iterator = dataSnapshot.getChildren().iterator();
                catagoryFoods.clear();
                main.food.clear();
                dessert.food.clear();
                drink.food.clear();
                while (iterator.hasNext()){
                    Map<String, Object> map = (Map<String, Object>) ((DataSnapshot)iterator.next()).getValue();
                    String name = (String) map.get("name");
                    String img = String.valueOf(map.get("img"));
                    Double price = Double.valueOf(String.valueOf(map.get("price")));
                    String catagory = (String) map.get("catagory");
                    Long like = Long.valueOf(String.valueOf(map.get("like")));
                    FoodModel item = new FoodModel(name, img, price, 0, catagory);
                    item.setLike(like);
                    if(catagory.equals("main")){
                        main.food.add(item);
                    }
                    else if(catagory.equals("dessert")){
                        dessert.food.add(item);
                    }
                    else if(catagory.equals("drink")){
                        drink.food.add(item);
                    }
//                    foodList.add(item);
                }
                catagoryFoods.add(main);
                catagoryFoods.add(dessert);
                catagoryFoods.add(drink);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        adapter = new ExpandListAdapterAdmin(this.getActivity(), catagoryFoods);
        main = new  CatagoryFood("Main");
        dessert = new  CatagoryFood("Dessert");
        drink = new  CatagoryFood("Drink");
        expandableListView.setAdapter(adapter);
        return root;
    }
    public int GetDipsFromPixel(float pixels)
    {
        // Get the screen's density scale
        final float scale = getResources().getDisplayMetrics().density;
        // Convert the dps to pixels, based on density scale
        return (int) (pixels * scale + 0.5f);
    }
}

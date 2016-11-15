package com.example.gardo.myapplication;


import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.gardo.myapplication.Model.FoodModel;
import com.google.firebase.auth.FirebaseAuth;
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
public class FavoriteFragment extends Fragment {
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    AdapterList adapter;
    ArrayList<FoodModel> foodList;
    ListView list_view;
    SwipeRefreshLayout swipe;

    public FavoriteFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        View root = inflater.inflate(R.layout.fragment_favorite, container, false);
        foodList = new ArrayList<>();
        adapter = new AdapterList(this.getActivity(), foodList);
        list_view = (ListView) root.findViewById(R.id.list_favorite_menu);
        list_view.setAdapter(adapter);
        loadFavoriteFood();
        return root;

    }
    private void refreshContent() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                foodList.clear();
                loadFavoriteFood();
                swipe.setRefreshing(false);
            }
        }, 4000);
    }

    private void loadFavoriteFood() {
        DatabaseReference user = mDatabase.child("user").child(mAuth.getCurrentUser().getUid()).child("favorite");
        user.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterator iterator = dataSnapshot.getChildren().iterator();
                while (iterator.hasNext()){
                    Map<String, Object> map = (Map<String, Object>) ((DataSnapshot)iterator.next()).getValue();
                    String name = (String) map.get("name");
                    String img = String.valueOf(map.get("img"));
                    Double price = Double.valueOf(String.valueOf(map.get("price")));
                    FoodModel item = new FoodModel(name, img, price, 0);
                    foodList.add(item);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}

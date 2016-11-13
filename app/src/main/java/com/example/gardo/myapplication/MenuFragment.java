package com.example.gardo.myapplication;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.gardo.myapplication.Model.FoodModel;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class MenuFragment extends Fragment{
    ListView list_view;
    private List<String>originalData = null;
    private List<String>filteredData = null;
    private DatabaseReference mDatabase;
//    String[] food;
//    Integer[] imageId;
//    Double[] price;
    AdapterList adapter;
    List<HashMap<String,String>> aList;
    ArrayList<FoodModel> foodList;
    public MenuFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        foodList = new ArrayList<>();
//        foodList.add(new FoodModel("Bianca", R.drawable.bianca, 2.5, 0));
//        foodList.add(new FoodModel("Bruschetta", R.drawable.bruschetta, 10.5, 0));
//        foodList.add(new FoodModel("Piatto", R.drawable.piatto, 5.5, 0));
        View root = inflater.inflate(R.layout.fragment_menu, container, false);
        setHasOptionsMenu(true);
        final DatabaseReference foodRef = mDatabase.child("food");
        adapter = new AdapterList(this.getActivity(), foodList);
        list_view = (ListView) root.findViewById(R.id.list_food);
        list_view.setAdapter(adapter);
        foodRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterator iterator = dataSnapshot.getChildren().iterator();
                while (iterator.hasNext()){
                    Map<String, Object> map = (Map<String, Object>) ((DataSnapshot)iterator.next()).getValue();
                    String name = (String) map.get("name");
                    Integer img = Integer.valueOf(String.valueOf(map.get("img")));
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
        return root;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPrepareOptionsMenu(final Menu menu) {
        MenuItem mSearchMenuItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) mSearchMenuItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                adapter.getFilter().filter(s);
                return false;
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();

    }
}

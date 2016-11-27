package com.example.gardo.myapplication;


import android.app.Activity;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.MenuItemCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.gardo.myapplication.Model.CatagoryFood;
import com.example.gardo.myapplication.Model.ExpandListAdapter;
import com.example.gardo.myapplication.Model.FoodModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class MenuFragment extends Fragment implements SearchView.OnQueryTextListener, SearchView.OnCloseListener{
    ListView list_view;
    private List<String>originalData = null;
    private List<String>filteredData = null;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private DatabaseReference user;
//    String[] food;
//    Integer[] imageId;
//    Double[] price;
    ExpandListAdapter adapter;
    List<HashMap<String,String>> aList;
    ArrayList<FoodModel> foodList;
    ArrayList<CatagoryFood> catagoryFoods;
    CatagoryFood main,dessert,drink;
    ProgressDialog mProgress;
    Context mContext;
    ExpandableListView expandableListView;
    public MenuFragment() {
        // Required empty public constructor
    }
    public int GetDipsFromPixel(float pixels)
    {
        // Get the screen's density scale
        final float scale = getResources().getDisplayMetrics().density;
        // Convert the dps to pixels, based on density scale
        return (int) (pixels * scale + 0.5f);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mContext = container.getContext();
        foodList = new ArrayList<>();
//        foodList.add(new FoodModel("Bianca", R.drawable.bianca, 2.5, 0));
//        foodList.add(new FoodModel("Bruschetta", R.drawable.bruschetta, 10.5, 0));
//        foodList.add(new FoodModel("Piatto", R.drawable.piatto, 5.5, 0));
        View root = inflater.inflate(R.layout.fragment_menu_temp, container, false);
        setHasOptionsMenu(true);
        final DatabaseReference foodRef = mDatabase.child("food");
//        adapter = new AdapterList(this.getActivity(), foodList);
//        list_view = (ListView) root.findViewById(R.id.list_food);
//        list_view.setAdapter(adapter);
        expandableListView = (ExpandableListView) root.findViewById(R.id.expand_list);
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
        int width = getResources().getDisplayMetrics().widthPixels;
        expandableListView.setIndicatorBounds(width-GetDipsFromPixel(35), width-GetDipsFromPixel(5));
        catagoryFoods = new ArrayList<>();
        mProgress = new ProgressDialog(getActivity());
        mProgress.setMessage("Loading food....");
        mProgress.show();
        foodRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterator iterator = dataSnapshot.getChildren().iterator();
                while (iterator.hasNext()){
                    Map<String, Object> map = (Map<String, Object>) ((DataSnapshot)iterator.next()).getValue();
                    String name = (String) map.get("name");
                    String img = String.valueOf(map.get("img"));
                    Double price = Double.valueOf(String.valueOf(map.get("price")));
                    String catagory = (String) map.get("catagory");
                    FoodModel item = new FoodModel(name, img, price, 0, catagory);
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
                mProgress.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        adapter = new ExpandListAdapter(this.getActivity(), catagoryFoods);
        main = new  CatagoryFood("Main");
        dessert = new  CatagoryFood("Dessert");
        drink = new  CatagoryFood("Drink");
        expandableListView.setAdapter(adapter);
        return root;
    }

    @Override
    public void onPrepareOptionsMenu(final Menu menu) {
        String check = getActivity().getIntent().getStringExtra("staff");
        if(check == null || !check.equals("staff")) {
            MenuItem mSearchMenuItem = menu.findItem(R.id.action_search);
            SearchView searchView = (SearchView) mSearchMenuItem.getActionView();
            SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
            searchView.setOnQueryTextListener(this);
            searchView.setOnCloseListener(this);
        }
    }


    private void expandAll() {
        int count = adapter.getGroupCount();
        for (int i = 0; i < count; i++){
            expandableListView.expandGroup(i);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public boolean onClose() {
        adapter.filterData("");
        expandAll();
        return false;
    }

    @Override
    public boolean onQueryTextChange(String query) {
        adapter.filterData(query);
        expandAll();
        return false;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        adapter.filterData(query);
        expandAll();
        return false;
    }

    public class HolderMenu {
        private Button increase, decrease;
        private ToggleButton like, favorite;
        private TextView number;
        RelativeLayout item;
        ImageView img;
        TextView txtTitle, price_text;
        Button add;
    }

    @Override
    public void onDestroy() {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("order").removeValue();
        super.onDestroy();
    }
}

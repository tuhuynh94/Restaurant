package com.example.gardo.myapplication.Model;

import android.app.Activity;
import android.app.LauncherActivity;
import android.content.Context;
import android.database.DataSetObserver;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ExpandableListAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.bumptech.glide.Glide;
import com.example.gardo.myapplication.AdapterList;
import com.example.gardo.myapplication.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by gardo on 21/11/2016.
 */

public class ExpandListAdapter extends BaseExpandableListAdapter {
    private Activity context;
    private ArrayList<CatagoryFood> foodList;
    private ArrayList<CatagoryFood> food;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private FirebaseStorage storage = FirebaseStorage.getInstance();

    public ExpandListAdapter(Activity context, ArrayList<CatagoryFood> food) {
        this.context = context;
        this.food = food;
    }

    @Override
    public void notifyDataSetChanged() {
        if(foodList == null && food != null){
            foodList = (ArrayList<CatagoryFood>) food.clone();
        }
        super.notifyDataSetChanged();
    }

    @Override
    public int getGroupCount() {
        return food.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return food.get(groupPosition).food.size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return food.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return food.get(groupPosition).food.get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        final View row = inflater.inflate(R.layout.catagory_layout, null, true);
        CatagoryFood group = (CatagoryFood) getGroup(groupPosition);
        TextView text = (TextView) row.findViewById(R.id.catagory);
        String name = group.Name;
        text.setText(name);
        return row;
    }
    DatabaseReference user;
    @Override
    public View getChildView(final int groupPosition,final int childPosition, boolean isLastChild, View convertView,final ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        final View rowView = inflater.inflate(R.layout.list_single_item, null, true);
        final HolderExpand holder = new HolderExpand();
        final FoodModel child = (FoodModel) food.get(groupPosition).food.get(childPosition);
        holder.txtTitle = (TextView) rowView.findViewById(R.id.txt);
        holder.number = (TextView) rowView.findViewById(R.id.quantity);
        holder.like = (ToggleButton) rowView.findViewById(R.id.like);
        holder.item = (RelativeLayout) rowView.findViewById(R.id.single_item);
        holder.img = (ImageView) rowView.findViewById(R.id.img);
        holder.price_text = (TextView) rowView.findViewById(R.id.price);
        holder.txtTitle.setText(child.getName());
        String path = "Menu/" + child.getImg();
        StorageReference foodRef = storage.getReference(path);
        Glide.with(rowView.getContext()).load(child.getImg()).into(holder.img);
        holder.price_text.setText("$" + child.getPrice());
        holder.number.setText(Integer.toString(child.getQuantity()));
        holder.price_text.setText("$" + child.getPrice());
        holder.number.setText(Integer.toString(child.getQuantity()));
        holder.item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(rowView.getContext(), "You Clicked at " + child.getName(), Toast.LENGTH_SHORT).show();
            }
        });
        holder.increase = (Button) rowView.findViewById(R.id.increase);
        holder.decrease = (Button) rowView.findViewById(R.id.decrease);
        holder.increase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                child.setQuantity(child.getQuantity() + 1);
                holder.number.setText(Integer.toString(child.getQuantity()));
            }
        });
        holder.decrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                child.setQuantity(child.getQuantity() - 1);
                if (child.getQuantity() < 0) {
                    child.setQuantity(0);
                }
                holder.number.setText(Integer.toString(child.getQuantity()));
            }
        });
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        final FirebaseUser userFire = mAuth.getCurrentUser();
        user = mDatabase.child("user").child(userFire.getUid()).child("favorite");
        final DatabaseReference foodLike = mDatabase.child("food").child(child.getName()).child("like");
        holder.favorite = (ToggleButton) rowView.findViewById(R.id.favorite);
        holder.favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FoodModel item = new FoodModel(child.getName(), child.getImg(), child.getPrice(), 0, child.getCatagory());
                if(holder.favorite.isChecked()){
                    user.child(item.getName()).setValue(item);
                }
                else{
                    user.child(item.getName()).removeValue();
                }
            }
        });
        final DatabaseReference favorite = mDatabase.child("user").child(userFire.getUid()).child("favorite");
        favorite.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, String> map = (Map<String, String>) dataSnapshot.getValue();
                if(map != null && map.containsKey(child.getName())){
                    holder.favorite.setChecked(true);
                }
                else{
                    holder.favorite.setChecked(false);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        holder.like = (ToggleButton) rowView.findViewById(R.id.like);
        final DatabaseReference like = mDatabase.child("user").child(userFire.getUid()).child("like");
        like.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, String> map = (Map<String, String>) dataSnapshot.getValue();
                if(map != null && map.containsKey(child.getName())){
                    holder.like.setChecked(true);
                }
                else{
                    holder.like.setChecked(false);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        holder.like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                foodLike.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        FoodModel item = new FoodModel(child.getName(), child.getImg(), child.getPrice(), 0, child.getCatagory());
                        if(holder.like.isChecked()){
                            foodLike.setValue(((Long)dataSnapshot.getValue()) + 1);
                            like.child(item.getName()).setValue(true);
                        }
                        else{
                            if((Long)dataSnapshot.getValue() > 0) {
                                foodLike.setValue(((Long) dataSnapshot.getValue()) - 1);
                                like.child(item.getName()).removeValue();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });
        holder.add = (Button) rowView.findViewById(R.id.btn_add);
        final DatabaseReference userOrder = mDatabase.child("user").child(userFire.getUid()).child("order");
        holder.add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.add.getText().toString().equals("ADD")) {
                    FoodModel item = new FoodModel(child.getName(), child.getImg(), child.getPrice(), child.getQuantity(), child.getCatagory());
                    if(child.getQuantity() > 0) {
                        userOrder.child(child.getName()).setValue(item);
                        holder.add.setText("REMOVE");
                    }
                }
                else{
                    userOrder.child(child.getName()).removeValue();
                    holder.add.setText("ADD");
                }
            }
        });
        holder.number.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(holder.add.getText().toString().equals("REMOVE")) {
                    userOrder.child(child.getName()).removeValue();
                    holder.add.setText("ADD");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        return rowView;
    }
    public void filterData(String query){

        query = query.toLowerCase();
        Log.v("MyListAdapter", String.valueOf(food.size()));
        food.clear();

        if(query.isEmpty()){
            food.addAll(foodList);
        }
        else {

            for(CatagoryFood food_item: foodList){

                ArrayList<FoodModel> foodModels = food_item.getFood();
                ArrayList<FoodModel> newList = new ArrayList<FoodModel>();
                for(FoodModel foodModel: foodModels){
                    if(foodModel.getName().toLowerCase().contains(query) || foodModel.getCatagory().toLowerCase().contains(query)){
                        newList.add(foodModel);
                    }
                }
                if(newList.size() > 0){
                    CatagoryFood nFood = new CatagoryFood(food_item.getName());
                    nFood.setFood(newList);
                    food.add(nFood);
                }
            }
        }
        Log.v("MyListAdapter", String.valueOf(food.size()));
        notifyDataSetChanged();
    }
    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
    public class HolderExpand {
        private Button increase, decrease;
        private ToggleButton like, favorite;
        private TextView number;
        RelativeLayout item;
        ImageView img;
        TextView txtTitle, price_text;
        Button add;
    }
}

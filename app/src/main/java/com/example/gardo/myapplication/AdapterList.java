package com.example.gardo.myapplication;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.util.LogWriter;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.bumptech.glide.Glide;
import com.example.gardo.myapplication.Model.FoodModel;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.api.model.StringList;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

public class AdapterList extends ArrayAdapter<FoodModel> {
    private final Activity context;
    private ArrayList<FoodModel> food;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private FirebaseStorage storage = FirebaseStorage.getInstance();

    public AdapterList(Activity context, ArrayList<FoodModel> food) {
        super(context, R.layout.list_single_item, food);
        this.context = context;
        this.food = food;
    }

    public class Holder {
        private Button increase, decrease;
        private ToggleButton like, favorite;
        private TextView number;
        RelativeLayout item;
        ImageView img;
        TextView txtTitle, price_text;
        Button add;
    }
    DatabaseReference user;
    ArrayList<FoodModel> order_item;
    @Override
    public View getView(final int position, View view, final ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        final View rowView = inflater.inflate(R.layout.list_single_item, null, true);
        final Holder holder = new Holder();
        holder.txtTitle = (TextView) rowView.findViewById(R.id.txt);
        holder.number = (TextView) rowView.findViewById(R.id.quantity);
        holder.like = (ToggleButton) rowView.findViewById(R.id.like);
        holder.item = (RelativeLayout) rowView.findViewById(R.id.single_item);
        holder.img = (ImageView) rowView.findViewById(R.id.img);
        holder.price_text = (TextView) rowView.findViewById(R.id.price);
        holder.txtTitle.setText(food.get(position).getName());
        String path = "Menu/" + food.get(position).getImg();
        StorageReference foodRef = storage.getReference(path);
        Glide.with(rowView.getContext()).load(food.get(position).getImg()).into(holder.img);
        holder.price_text.setText("$" + food.get(position).getPrice());
        holder.number.setText(Integer.toString(food.get(position).getQuantity()));
        holder.item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(rowView.getContext(), "You Clicked at " + food.get(position).getName(), Toast.LENGTH_SHORT).show();
            }
        });
        holder.increase = (Button) rowView.findViewById(R.id.increase);
        holder.decrease = (Button) rowView.findViewById(R.id.decrease);
        holder.increase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                food.get(position).setQuantity(food.get(position).getQuantity() + 1);
                holder.number.setText(Integer.toString(food.get(position).getQuantity()));
            }
        });
        holder.decrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                food.get(position).setQuantity(food.get(position).getQuantity() - 1);
                if (food.get(position).getQuantity() < 0) {
                    food.get(position).setQuantity(0);
                }
                holder.number.setText(Integer.toString(food.get(position).getQuantity()));
            }
        });
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        final FirebaseUser userFire = mAuth.getCurrentUser();
        user = mDatabase.child("user").child(userFire.getUid()).child("favorite");
        final DatabaseReference foodLike = mDatabase.child("food").child(food.get(position).getName()).child("like");
        holder.favorite = (ToggleButton) rowView.findViewById(R.id.favorite);
        holder.favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FoodModel item = new FoodModel(food.get(position).getName(), food.get(position).getImg(), food.get(position).getPrice(), 0);
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
                if(map != null && map.containsKey(food.get(position).getName())){
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
                if(map != null && map.containsKey(food.get(position).getName())){
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
                        FoodModel item = new FoodModel(food.get(position).getName(), food.get(position).getImg(), food.get(position).getPrice(), 0);
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
                    FoodModel item = new FoodModel(food.get(position).getName(), food.get(position).getImg(), food.get(position).getPrice(), food.get(position).getQuantity());
                    if(food.get(position).getQuantity() > 0) {
                        userOrder.child(food.get(position).getName()).setValue(item);
                        holder.add.setText("REMOVE");
                    }
                }
                else{
                    userOrder.child(food.get(position).getName()).removeValue();
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
                    userOrder.child(food.get(position).getName()).removeValue();
                    holder.add.setText("ADD");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        return rowView;
    }

//    @NonNull
//    @Override
//    public Filter getFilter() {
//        return new Filter() {
//            @SuppressWarnings("unchecked")
//            @Override
//            protected void publishResults(CharSequence constraint, FilterResults results) {
//                Log.d("Filter", "**** PUBLISHING RESULTS for: " + constraint);
//                food = (ArrayList<FoodModel>) results.values;
//                notifyDataSetChanged();
//            }
//
//            @Override
//            protected FilterResults performFiltering(CharSequence constraint) {
//                Log.d("Filter", "**** PERFORM FILTERING for: " + constraint);
//                ArrayList<FoodModel> filteredResults = getFilteredResults(constraint);
//
//                FilterResults results = new FilterResults();
//                results.values = filteredResults;
//                results.count = filteredResults.size();
//
//                return results;
//            }
//        };
//    }
//
//    public ArrayList<FoodModel> getFilteredResults(CharSequence text) {
//        ArrayList<FoodModel> result = new ArrayList<>();
//        if (text == null) {
//            result = food;
//        } else {
//            if (food != null && food.size() > 0) {
//                for (FoodModel e : food) {
//                    if (e.getName().toLowerCase().contains(text.toString().toLowerCase())) {
//                        result.add(e);
//                    }
//                }
//            }
//        }
//        return result;
//    }
}

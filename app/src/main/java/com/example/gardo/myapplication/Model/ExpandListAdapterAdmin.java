package com.example.gardo.myapplication.Model;

import android.app.Activity;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.bumptech.glide.Glide;
import com.example.gardo.myapplication.EditActivityAdmin;
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

public class ExpandListAdapterAdmin extends BaseExpandableListAdapter {
    private Activity context;
    private ArrayList<CatagoryFood> foodList;
    private ArrayList<CatagoryFood> food;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private FirebaseStorage storage = FirebaseStorage.getInstance();

    public ExpandListAdapterAdmin(Activity context, ArrayList<CatagoryFood> food) {
        this.context = context;
        this.food = food;
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
        final View rowView = inflater.inflate(R.layout.list_item_admin, null, true);
        final HolderExpand holder = new HolderExpand();
        final FoodModel child = (FoodModel) food.get(groupPosition).food.get(childPosition);
        holder.txtTitle = (TextView) rowView.findViewById(R.id.name_admin);
        holder.img = (ImageView) rowView.findViewById(R.id.img_food);
        holder.price_text = (TextView) rowView.findViewById(R.id.price_admin);
        holder.txtTitle.setText(child.getName());
        String path = "Menu/" + child.getImg();
        StorageReference foodRef = storage.getReference(path);
        Glide.with(rowView.getContext()).load(child.getImg()).into(holder.img);
        holder.price_text.setText("$" + child.getPrice());
        holder.edit = (Button) rowView.findViewById(R.id.edit_admin);
        holder.remove = (Button) rowView.findViewById(R.id.remove_admin);
        holder.remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference foodRef = FirebaseDatabase.getInstance().getReference().child("food").child(child.getName());
                foodRef.removeValue();
            }
        });
        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(rowView.getContext(), EditActivityAdmin.class);
                i.putExtra("name", child.getName());
                i.putExtra("price", child.getPrice());
                i.putExtra("img", child.getImg());
                i.putExtra("catagory", child.getCatagory());
                i.putExtra("like", child.getLike());
                rowView.getContext().startActivity(i);
            }
        });
        return rowView;
    }
    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
    public class HolderExpand {
        private Button edit, remove;
        ImageView img;
        TextView txtTitle, price_text;
    }
}

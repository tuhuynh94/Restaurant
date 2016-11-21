package com.example.gardo.myapplication.Model;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.example.gardo.myapplication.AdapterList;
import com.example.gardo.myapplication.LoginMainActivity;
import com.example.gardo.myapplication.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by gardo on 16/11/2016.
 */

public class TableModel extends ArrayAdapter<Table> {
    private ArrayList<Table> tables;
    private Activity context;
    private FirebaseAuth mAuth;
    String id;

    public TableModel(Activity context, ArrayList<Table> tables) {
        super(context, R.layout.table_list_item, tables);
        this.context = context;
        this.tables = tables;
    }
    public class TableHolder {
        TextView table_name;
        TextView status;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        final View rowView = inflater.inflate(R.layout.table_list_item, null, true);
        TableHolder holder = new TableHolder();
        holder.table_name = (TextView) rowView.findViewById(R.id.table_name);
        holder.status = (TextView) rowView.findViewById(R.id.table_status);
        Log.v("table", tables.get(position).getTable_name());
        holder.table_name.setText(tables.get(position).getTable_name());
        mAuth = FirebaseAuth.getInstance();
        if(!tables.get(position).getStatus()){
            holder.status.setText("Available");
            holder.status.setTextColor(Color.GREEN);
        }
        else {
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Table").child(tables.get(position).getTable_name());
            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    id = (String) dataSnapshot.getValue();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            if(id == mAuth.getCurrentUser().getUid()){
                holder.status.setText("Using");
                holder.status.setTextColor(Color.YELLOW);
            }
            else {
                holder.status.setText("Not Available");
                holder.status.setTextColor(Color.RED);
            }
        }
        return rowView;
    }
}

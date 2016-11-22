package com.example.gardo.myapplication.Model;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.ButtonBarLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.gardo.myapplication.OrderActivity;
import com.example.gardo.myapplication.R;
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
 * Created by gardo on 16/11/2016.
 */

public class DiaglogModel extends DialogFragment {
    LayoutInflater inflater;
    View v;
    ListView list;
    FirebaseAuth mAuth;
    ArrayList<Table> tables;
    TableModel adapter;
    boolean hasTable;
    String table_check;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        inflater = getActivity().getLayoutInflater();
        v = inflater.inflate(R.layout.diaglog_table, null);
        mAuth = FirebaseAuth.getInstance();
        tables = new ArrayList<>();
        adapter = new TableModel(getActivity(), tables);
        loadTable();
        list = (ListView) v.findViewById(R.id.list_table);
        Log.v("list", list.toString());
        list.setAdapter(adapter);
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        boolean check = false;
        if(!check) {
            builder.setView(v).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Table table = (Table) list.getItemAtPosition(list.getCheckedItemPosition());
                    if (!table.getStatus()) {
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("user").child(mAuth.getCurrentUser().getUid()).child("Table");
                        if(!hasTable) {
                            FirebaseDatabase.getInstance().getReference().child("Table").child(table.getTable_name()).setValue(mAuth.getCurrentUser().getUid());
                            ref.setValue(table.getTable_name());
                            Button confirm = (Button) getActivity().findViewById(R.id.confirm_order);
                            confirm.setText("CHANGE TABLE");
                        }
                        else{
                            FirebaseDatabase.getInstance().getReference().child("Table").child(table_check).removeValue();
                            FirebaseDatabase.getInstance().getReference().child("Table").child(table.getTable_name()).setValue(mAuth.getCurrentUser().getUid());
                            ref.setValue(table.getTable_name());
                        }
                    } else {
                        Toast.makeText(v.getContext(), "This table is using", Toast.LENGTH_SHORT).show();
                    }
                }
            }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });
        }
        else{

        }
        return builder.create();
    }

    private void loadTable() {
        FirebaseDatabase.getInstance().getReference().child("Table").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterator iterator = dataSnapshot.getChildren().iterator();
                while (iterator.hasNext()) {
                    DataSnapshot data = (DataSnapshot) iterator.next();
                    String val = (String) data.getValue();
                    if(val.equals("")){
                        Table table = new Table(data.getKey(), false);
                        tables.add(table);
                    }
                    else{
                        Table table = new Table(data.getKey(), true);
                        tables.add(table);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}

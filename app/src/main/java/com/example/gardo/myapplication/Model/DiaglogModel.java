package com.example.gardo.myapplication.Model;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.gardo.myapplication.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

/**
 * Created by gardo on 16/11/2016.
 */

public class DiaglogModel extends DialogFragment {
    LayoutInflater inflater;
    View v;
    ListView list;
    FirebaseAuth mAuth;
    ArrayList<Table> tables;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        inflater = getActivity().getLayoutInflater();
        v = inflater.inflate(R.layout.diaglog_table, null);
        mAuth = FirebaseAuth.getInstance();
        tables = new ArrayList<>();
        tables.add(new Table("Table 1", false));
        tables.add(new Table("Table 2", false));
        tables.add(new Table("Table 3", false));
        tables.add(new Table("Table 4", false));
        tables.add(new Table("Table 5", false));
        tables.add(new Table("Table 6", false));
        final TableModel adapter = new TableModel(getActivity(), tables);
        list = (ListView) v.findViewById(R.id.list_table);
        Log.v("list", list.toString());
        list.setAdapter(adapter);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Table table = (Table) list.getItemAtPosition(position);
                if(!table.getStatus()){
                    FirebaseDatabase.getInstance().getReference().child("Table").child(table.getTable_name()).setValue(mAuth.getCurrentUser().getUid());
                    tables.set(position, new Table(table.getTable_name(), true));
                    adapter.notifyDataSetChanged();
                }
                else{
                    Toast.makeText(v.getContext(), "This table is using", Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setView(v).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        return builder.create();
    }
}

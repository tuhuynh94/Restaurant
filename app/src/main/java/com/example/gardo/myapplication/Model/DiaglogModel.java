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

import com.example.gardo.myapplication.LoginActivity;
import com.example.gardo.myapplication.MainActivity;
import com.example.gardo.myapplication.OrderActivity;
import com.example.gardo.myapplication.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

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
    boolean hasTable = false;
    String table_check;
    Table table;
    boolean check;

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
        check = false;
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Table");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                if (map != null) {
                    table_check = getKeysByValue(map, mAuth.getCurrentUser().getUid()).toString();
                    if(table_check != null && !table_check.equals("")){
                        hasTable = true;
                    }
                }
                check = true;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(v.getContext(), "The read failed: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        builder.setView(v).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(check) {
                    Table table = (Table) list.getItemAtPosition(list.getCheckedItemPosition());
                    if (table != null && !table.getStatus()) {
                        if (!hasTable) {
                            FirebaseDatabase.getInstance().getReference().child("Table").child(table.getTable_name()).child("Customer").setValue(mAuth.getCurrentUser().getUid());
                            FirebaseDatabase.getInstance().getReference().child("Table").child(table.getTable_name()).child("Status").setValue("Choosen table");
                            FirebaseDatabase.getInstance().getReference().child("Table").child(table.getTable_name()).child("check").removeValue();
                            Intent i = new Intent(getActivity(), MainActivity.class);
                            startActivity(i);
//                            Button confirm = (Button) getActivity().findViewById(R.id.confirm_order);
//                            confirm.setText("CHANGE TABLE");
                        } else {
                            FirebaseDatabase.getInstance().getReference().child("Table").child(table.getTable_name()).child("Customer").setValue(mAuth.getCurrentUser().getUid());
                            FirebaseDatabase.getInstance().getReference().child("Table").child(table.getTable_name()).child("Status").setValue("Changed Table");
                            FirebaseDatabase.getInstance().getReference().child("Table").child(table_check).child("Customer").setValue("");
                            FirebaseDatabase.getInstance().getReference().child("Table").child(table_check).child("Status").setValue("Empty");
                            FirebaseDatabase.getInstance().getReference().child("Table").child(table_check).child("check").removeValue();
                            Intent i = new Intent(getActivity(), MainActivity.class);
                            startActivity(i);
                        }
                    } else if(table != null && table.getStatus()){
                        Toast.makeText(v.getContext(), "This table is using", Toast.LENGTH_SHORT).show();
                        DiaglogModel myDiaglog = new DiaglogModel();
                        myDiaglog.show(getFragmentManager(), "show_diaglod");
                    }
                }
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(v.getContext(), "Please choose table before order", Toast.LENGTH_SHORT).show();
                dialog.cancel();
                FirebaseAuth.getInstance().signOut();
            }
        });
        builder.setTitle("Please choose table before order");
        return builder.create();
    }

    private void loadTable() {
        FirebaseDatabase.getInstance().getReference().child("Table").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterator iterator = dataSnapshot.getChildren().iterator();
                while (iterator.hasNext()) {
                    DataSnapshot tab = (DataSnapshot) iterator.next();
                    Map<String, Object> map = (Map<String, Object>) tab.getValue();
                    String customer = (String) map.get("Customer");
                    String status = (String ) map.get("Status");
                    if (customer.equals("")) {
                        Table table = new Table(tab.getKey(), false);
                        tables.add(table);
                    } else {
                        Table table = new Table(tab.getKey(), true);
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

    public static String getKeysByValue(Map<String, Object> map, String value) {
        String keys = "";
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            Map<String, Object> child = (Map<String, Object>) entry.getValue();
            if (Objects.equals(value, child.get("Customer"))) {
                keys = entry.getKey().toString();
                break;
            }
        }
        return keys;
    }
}

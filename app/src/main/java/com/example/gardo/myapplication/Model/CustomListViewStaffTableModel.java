package com.example.gardo.myapplication.Model;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.example.gardo.myapplication.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class CustomListViewStaffTableModel extends ArrayAdapter<Table> {
    private final Activity context;
    private ArrayList<Table> table;


    public CustomListViewStaffTableModel(Activity context, ArrayList<Table> table) {
        super(context,R.layout.fragment_table, table);
        this.context = context;
        this.table = table;
    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        final View rowView = inflater.inflate(R.layout.table_staff, null, true);
        final TextView status, table_text;
        status = (TextView) rowView.findViewById(R.id.status);
        table_text = (TextView) rowView.findViewById(R.id.table);
        Button check_order = (Button) rowView.findViewById(R.id.check_order);
        status.setText(table.get(position).getStatus_Staff());
        table_text.setText(table.get(position).getTable_name());
        check_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Table").child(table.get(position).getTable_name());
                new AlertDialog.Builder(getContext()).setTitle("Are you sure this table has paid?").setPositiveButton("YES", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ref.child("Customer").setValue("");
                        ref.child("Status").setValue("Empty");
                    }
                }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).create().show();
            }
        });
        return rowView;
    }
}
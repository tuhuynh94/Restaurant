package com.example.gardo.myapplication.Model;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.example.gardo.myapplication.AdminActivity;
import com.example.gardo.myapplication.R;
import com.example.gardo.myapplication.StaffActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class CustomListViewStaffTableModel extends ArrayAdapter<Table> {
    private final Activity context;
    private ArrayList<Table> table;
    private Table table_item;
    private ProgressDialog mProgressDialog;
    Double spend = 0D, reward = 0D;


    public CustomListViewStaffTableModel(Activity context, ArrayList<Table> table) {
        super(context, R.layout.fragment_table, table);
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
        mProgressDialog = new ProgressDialog(rowView.getContext());
        mProgressDialog.setMessage("Processing...");
        check_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(getContext()).setTitle("Are you sure this table has paid?").setPositiveButton("YES", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                        Thread thread = new Thread(new Runnable() {
//                            @Override
//                            public void run() {
//                                table_item = table.get(position);
//                                task();
//                            }
//                        });
//                        try {
//                            thread.start();
//                            thread.sleep(2000);
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
                        Runnable get = new Runnable() {
                            @Override
                            public void run() {
                                table_item = table.get(position);
                                getPoint();
                            }
                        };
                        Runnable run = new Runnable() {
                            @Override
                            public void run() {
                                mProgressDialog.show();
                                task();
                            }
                        };
                        Handler handler = new Handler();
                        handler.postDelayed(get, 1000);
                        handler.postDelayed(run, 2000);
                    }
                }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).create().show();
            }
        });
        Button listen = (Button) rowView.findViewById(R.id.listen_customer);
        listen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                table_item = table.get(position);
                final DatabaseReference userOrder = FirebaseDatabase.getInstance().getReference().child("user").child(table_item.getCustomer_name());
                final DatabaseReference OrderRef = FirebaseDatabase.getInstance().getReference().child("Order");
                final DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Table").child(table_item.getTable_name());
                ref.child("Status").setValue("USING");
                userOrder.child("order").child("listened").setValue("Accept");
            }
        });
        return rowView;
    }

    static class Order {

        public final HashMap<String, Object> map;
        public final String format;

        public Order(HashMap<String, Object> map, String format) {
            this.map = map;
            this.format = format;
        }
    }

    private void task() {
        final DatabaseReference userOrder = FirebaseDatabase.getInstance().getReference().child("user").child(table_item.getCustomer_name());
        final DatabaseReference OrderRef = FirebaseDatabase.getInstance().getReference().child("Order");
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Table").child(table_item.getTable_name());
        userOrder.child("order").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterator iterator = dataSnapshot.getChildren().iterator();
                Map<String, FoodModel> food = new HashMap<String, FoodModel>();
                double total = 0;
                DatabaseReference id = userOrder.child("OrderHistory").push();
                while (iterator.hasNext()) {
                    DataSnapshot item = (DataSnapshot) iterator.next();
                    Map<String, Object> map = (Map<String, Object>) item.getValue();
                    String name = (String) map.get("name");
                    String img = String.valueOf(map.get("img"));
                    Double price = Double.valueOf(String.valueOf(map.get("price")));
                    String catagory = (String) map.get("catagory");
                    Integer quantity = Integer.valueOf(String.valueOf(map.get("quantity")));
                    total += price * quantity;
                    FoodModel child = new FoodModel(name, img, price, quantity, catagory);
                    id.child(name).setValue(child);
                }
                DecimalFormat format = new DecimalFormat("0.00");
                userOrder.child("Information").child("Total Spend").setValue(format.format(spend + total));
                userOrder.child("Information").child("Reward Points").setValue(format.format(reward + total * 0.05));
                userOrder.child("order").removeValue();
                DatabaseReference childOrder = OrderRef.push();
                DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                Calendar cal = Calendar.getInstance();
                HashMap<String, Object> map1 = new HashMap<String, Object>();
                map1.put("total", total);
                map1.put("date", dateFormat.format(cal.getTime()));
                id.child("Date").setValue(dateFormat.format(cal.getTime()));
                childOrder.setValue(map1);
                ref.child("Customer").setValue("");
                ref.child("Status").setValue("Empty");
                mProgressDialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getPoint() {
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("user").child(table_item.getCustomer_name()).child("Information");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                if (map.get("Total Spend") != null && map.get("Reward Points") != null) {
                    spend = Double.valueOf(String.valueOf(map.get("Total Spend")));
                    reward = Double.valueOf(String.valueOf(map.get("Reward Points")));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
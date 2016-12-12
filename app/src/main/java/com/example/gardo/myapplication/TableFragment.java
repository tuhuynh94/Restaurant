package com.example.gardo.myapplication;


import android.app.NotificationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.NotificationCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.gardo.myapplication.Model.CustomListViewStaffTableModel;
import com.example.gardo.myapplication.Model.Table;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class TableFragment extends Fragment {
    CustomListViewStaffTableModel adapter;
    ArrayList<Table> tables;
    String check = "Empty";
    NotificationCompat.Builder mBuilder;
    NotificationManager mNotificationManager;
    private static final int MY_NOTIFICATION_ID = 12345;

    public TableFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_table, container, false);
        tables = new ArrayList<>();
        ListView list = (ListView) root.findViewById(R.id.list_table_staff);
        adapter = new CustomListViewStaffTableModel(getActivity(), tables);
        mBuilder = new NotificationCompat.Builder(getActivity());
        mNotificationManager =
                (NotificationManager) getActivity().getSystemService(getContext().NOTIFICATION_SERVICE);
        loadTable();
        list.setAdapter(adapter);
        return root;
    }
    private void loadTable() {
        FirebaseDatabase.getInstance().getReference().child("Table").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                tables.clear();
                Iterator iterator = dataSnapshot.getChildren().iterator();
                while (iterator.hasNext()) {
                    DataSnapshot tab = (DataSnapshot) iterator.next();
                    Map<String, Object> map = (Map<String, Object>) tab.getValue();
                    String customer = (String) map.get("Customer");
                    String status = (String ) map.get("Status");
                   if (customer.equals("")) {
                        Table table = new Table(tab.getKey(), false);
                        table.setStatus_Staff(status);
                        tables.add(table);
                    } else {
                        Table table = new Table(tab.getKey(), true);
                        table.setStatus_Staff(status);
                        table.setCustomer_name(customer);
                       if(!status.equals(check)){
                           mBuilder.setSmallIcon(R.drawable.icon)
                                   .setContentTitle(table.getTable_name())
                                   .setContentText(status);
                           mNotificationManager.notify(MY_NOTIFICATION_ID, mBuilder.build());
                       }
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

package com.example.gardo.myapplication;


import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.gardo.myapplication.Model.CouponItemModel;
import com.example.gardo.myapplication.Model.ListViewCoupon;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import pl.rspective.voucherify.android.client.VoucherifyAndroidClient;


/**
 * A simple {@link Fragment} subclass.
 */
public class CouponFragment extends Fragment {
    ArrayList<CouponItemModel> coupon;


    public CouponFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_coupon, container, false);
        ListView list = (ListView) root.findViewById(R.id.list_coupon);
        coupon = new ArrayList<>();
        final ListViewCoupon adapter = new ListViewCoupon(this.getActivity(), coupon);
        list.setAdapter(adapter);
        DatabaseReference couponRef = FirebaseDatabase.getInstance().getReference().child("Coupon");
        couponRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterator iterator = dataSnapshot.getChildren().iterator();
                while (iterator.hasNext()) {
                    DataSnapshot i = (DataSnapshot)iterator.next();
                    Map<String, Object> map = (Map<String, Object>) i.getValue();
                    String name = i.getKey();
                    Double point = Double.valueOf(String.valueOf(map.get("points")));
                    CouponItemModel couponItemModel = new CouponItemModel(name, point);
                    coupon.add(couponItemModel);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                new AlertDialog.Builder(getActivity()).setTitle("This activity will update soon").setPositiveButton("DONE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).create().show();
            }
        });
        return root;
    }

}

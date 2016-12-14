package com.example.gardo.myapplication;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;
import java.util.Objects;

public class CheckoutActivity extends AppCompatActivity {
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.checkout_layout);
        Button cash = (Button) findViewById(R.id.pay_cash);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        cash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(CheckoutActivity.this).setTitle("Sent payment request").setPositiveButton("DONE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Table");
                        ref.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                                String table = getKeysByValue(map, mAuth.getCurrentUser().getUid());
                                if (table != null && !table.equals("")) {
                                    ref.child(table).child("Status").setValue("Payment");
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                }).create().show();
            }
        });
        Button pay_credit = (Button) findViewById(R.id.pay_credit);
        pay_credit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(CheckoutActivity.this).setTitle("This activity will update soon").setPositiveButton("DONE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).create().show();
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

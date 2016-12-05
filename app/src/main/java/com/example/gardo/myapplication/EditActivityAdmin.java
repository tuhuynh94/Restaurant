package com.example.gardo.myapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static com.example.gardo.myapplication.R.id.price_food_edit;

public class EditActivityAdmin extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_admin);
        final String name_food = getIntent().getExtras().getString("name");
        final Double price_food = (Double) getIntent().getExtras().get("price");
        final Long like = (Long) getIntent().getExtras().get("like");
        final String catagory = getIntent().getExtras().getString("catagory");
        final String img = getIntent().getExtras().getString("img");
        ImageView imgView = (ImageView) findViewById(R.id.img_edit);
        final TextView name = (TextView) findViewById(R.id.name_food_edit);
        final TextView price = (TextView) findViewById(price_food_edit);
        name.setText(name_food);
        price.setText(price_food + "");
        Glide.with(this).load(img).into(imgView);
        Button save = (Button) findViewById(R.id.save_edit);
        final ProgressDialog mProgress = new ProgressDialog(this);
        mProgress.setMessage("Updating...");
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("food").child(name_food);
                        ref.removeValue();
                        ref = FirebaseDatabase.getInstance().getReference().child("food").child(name.getText().toString());
                        ref.child("name").setValue(name.getText().toString());
                        ref.child("price").setValue(Double.valueOf(price.getText().toString()));
                        ref.child("catagory").setValue(catagory);
                        ref.child("like").setValue(like);
                        ref.child("img").setValue(img);
                    }
                });
                try {
                    thread.start();
                    thread.sleep(1000);
                    Intent i = new Intent(getApplicationContext(), AdminActivity.class);
                    startActivity(i);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        Button cancle = (Button) findViewById(R.id.cancel_edit);
        cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}

package com.example.gardo.myapplication.Firebase;

import com.example.gardo.myapplication.Model.FoodModel;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

public class FirebaseHelper {
    DatabaseReference db;
    Boolean saved;
    ArrayList<FoodModel> foods = new ArrayList<>();

    public FirebaseHelper(DatabaseReference db) {
        this.db = db;
    }
    public Boolean save(FoodModel food){
        if(food == null){
            saved = false;
        }
        else{
            try{
                db.child("food").push().setValue(food);
                saved = true;

            }
            catch (DatabaseException e){
                e.printStackTrace();
                saved = false;
            }
        }
        return saved;
    }

    private void fetchData(DataSnapshot dataSnapshot){
        foods.clear();
        for(DataSnapshot ds : dataSnapshot.getChildren()){
            FoodModel food = ds.getValue(FoodModel.class);
            foods.add(food);
        }
    }

    public ArrayList<FoodModel> retrive(){
        db.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                fetchData(dataSnapshot);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                fetchData(dataSnapshot);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return foods;
    }
}

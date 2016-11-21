package com.example.gardo.myapplication;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.gardo.myapplication.Model.FoodModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class HotMenuFragment extends Fragment{
    DatabaseReference mDatabase;
    FirebaseDatabase database;
    FirebaseAuth mAuth;
    private ArrayList<FoodModel> foodList;
    private ArrayList<FoodModel> hotList;
    private AdapterList adapter;
    ListView list_view;
    private int sizeHotMenu = 2;
    SwipeRefreshLayout swipe;


    public HotMenuFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        View root = inflater.inflate(R.layout.fragment_hot_menu, container, false);
        foodList = new ArrayList<>();
        hotList = new ArrayList<>();
        adapter = new AdapterList(this.getActivity(), hotList);
        list_view = (ListView) root.findViewById(R.id.list_hot_menu);
        swipe = (SwipeRefreshLayout) root.findViewById(R.id.refresh_hot_menu);
//        String path  = ("https://firebasestorage.googleapis.com/v0/b/restaurant-d8ad0.appspot.com/o/Menu%2Fbianca.jpg?alt=media&token=32111f26-8ad0-4a6b-8209-0d116ca45460");
//        new DownLoadImageTask(list_view).execute(path);

        list_view.setAdapter(adapter);
        loadHotFood();
        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshContent();
            }
        });
        return root;
    }

    private void refreshContent() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                hotList.clear();
                loadHotFood();
                swipe.setRefreshing(false);
            }
        }, 4000);
    }

    private void loadHotFood() {
        final DatabaseReference foodRef = mDatabase.child("food");
        foodRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterator iterator = dataSnapshot.getChildren().iterator();
                foodList.clear();
                hotList.clear();
                while (iterator.hasNext()) {
                    Map<String, Object> map = (Map<String, Object>) ((DataSnapshot) iterator.next()).getValue();
                    String name = (String) map.get("name");
                    String img = String.valueOf(map.get("img"));
                    Double price = Double.valueOf(String.valueOf(map.get("price")));
                    Long like = Long.valueOf(String.valueOf(map.get("like")));
                    String catagory = (String) map.get("catagory");
                    FoodModel item = new FoodModel(name, img, price, 0, catagory);
                    item.setLike(like);
                    foodList.add(item);
                }
                Collections.sort(foodList);
                StringBuilder res1 = new StringBuilder();
                StringBuilder res2 = new StringBuilder();
                for (FoodModel e : foodList) {
                    res1.append(e.getName() + "\n");
                }
                Log.v("Hot", res1.toString());
                for (int i = 0; i < sizeHotMenu; i++) {
                    hotList.add(foodList.get(i));
                }
                for (FoodModel e : hotList) {
                    res2.append(e.getName() + "\n");
                }
                Log.v("Hot1", res2.toString());
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
//    private class DownLoadImageTask extends AsyncTask<String,Void,Bitmap>{
//        ListView imageView;
//
//        public DownLoadImageTask(ListView imageView){
//            this.imageView = imageView;
//        }
//        protected Bitmap doInBackground(String...urls){
//            String urlOfImage = urls[0];
//            Bitmap logo = null;
//            try{
//                InputStream is = new URL(urlOfImage).openStream();
//                logo = BitmapFactory.decodeStream(is);
//            }catch(Exception e){
//                e.printStackTrace();
//            }
//            return logo;
//        }
//
//        protected void onPostExecute(Bitmap result){
//            BitmapDrawable back = new BitmapDrawable(getResources(), result);
//            imageView.setBackground(back);
//        }
//    }
}

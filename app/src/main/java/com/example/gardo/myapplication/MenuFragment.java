package com.example.gardo.myapplication;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class MenuFragment extends Fragment{
    ListView list_view;
    String[] food = {
            "Bianca",
            "Bruschetta",
            "Margherita",
            "Napoletana",
            "Piatto",
            "Seafood Antipasti"
    } ;
    Integer[] imageId = {
            R.drawable.bianca,
            R.drawable.bruschetta,
            R.drawable.margherita,
            R.drawable.napoletana,
            R.drawable.piatto,
            R.drawable.seafood_antipasti
    };
    List<HashMap<String,String>> aList;
    public MenuFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_menu, container, false);
        AdapterList adapter = new AdapterList(this.getActivity(),  food, imageId);
        list_view = (ListView) root.findViewById(R.id.list_food);
        list_view.setAdapter(adapter);
        return root;
    }

    @Override
    public void onStart() {
        super.onStart();

    }
}

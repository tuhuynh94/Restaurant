package com.example.gardo.myapplication;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class MenuFragment extends Fragment{
    ListView list_view;
    private List<String>originalData = null;
    private List<String>filteredData = null;
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
    Double[] price = {
            12.5,
            3.5,
            6.5,
            8.5,
            10.0,
            15.5
    };
    AdapterList adapter;
    List<HashMap<String,String>> aList;
    public MenuFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_menu, container, false);
        setHasOptionsMenu(true);
        adapter = new AdapterList(this.getActivity(),  food, imageId, price);
        list_view = (ListView) root.findViewById(R.id.list_food);
        list_view.setAdapter(adapter);
        return root;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        MenuItem mSearchMenuItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) mSearchMenuItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                adapter.getFilter().filter(s);
                return false;
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();

    }
}

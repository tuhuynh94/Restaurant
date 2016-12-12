package com.example.gardo.myapplication;

import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.AppLaunchChecker;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuInflater;
import android.view.SubMenu;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Map;
import java.util.Objects;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    ShareActionProvider mActionProvider;
    TabLayout tabLayout;
    ViewPager viewPager;
    ViewPagerAdapter viewPagerAdapter;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private FirebaseUser user;
    ImageView account_circle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Toolbar toolbar_bottom = (Toolbar) findViewById(R.id.toolbar_bottom);
        toolbar_bottom.inflateMenu(R.menu.main_bottom);
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        String email = null;
        if (user != null) {
            email = user.getEmail();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        final ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View header = navigationView.getHeaderView(0);
        if (getIntent().getExtras() != null && getIntent().getExtras().get("admin").equals("admin")) {
            navigationView.getMenu().findItem(R.id.admin_title).setVisible(true);
        }
        account_circle = (ImageView) header.findViewById(R.id.account_circle);
        TextView name_info = (TextView) header.findViewById(R.id.name_info);
        TextView email_info = (TextView) header.findViewById(R.id.email_info);
        final TextView total_spend = (TextView) header.findViewById(R.id.total_spend);
        final TextView reward_points = (TextView) header.findViewById(R.id.reward_points);
        if (email == null) {
            account_circle.setImageResource(R.drawable.avtar_anonymous);
            account_circle.setMaxHeight(10);
            account_circle.setMaxWidth(10);
            total_spend.setVisibility(View.INVISIBLE);
            reward_points.setVisibility(View.INVISIBLE);
            name_info.setText("anonymous");
            email_info.setText("");
        } else {
//            FirebaseStorage storage = FirebaseStorage.getInstance();
//            StorageReference storageRef = storage.getReferenceFromUrl("gs://restaurant-d8ad0.appspot.com/");
//            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
//                    .setDisplayName("Gardon Zero")
//                    .setPhotoUri(Uri.parse(storageRef.child("panda.png").getDownloadUrl().getResult()))
//                    .build();
//            user.updateProfile(profileUpdates);
//            name_info.setText(user.getDisplayName().toString());
//            String uri = user.getPhotoUrl().toString();
//            Glide.with(this).using(new FirebaseImageLoader()).load(storageRef.child("panda.png")).into(account_circle);
            final DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("user").child(mAuth.getCurrentUser().getUid()).child("Information");
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    if(map != null && map.get("Total Spend") != null && map.get("Reward Points") != null) {
                        String spend = String.valueOf(map.get("Total Spend"));
                        String reward = String.valueOf(map.get("Reward Points"));
                        total_spend.setText("Total Spend: $" + spend);
                        reward_points.setText("Total Reward: " + reward);
                    }
                    else if(map == null){
                        ref.child("Email").setValue(mAuth.getCurrentUser().getEmail());
                        ref.child("Role").setValue("User");
                        ref.child("Total Spend").setValue(0);
                        ref.child("Reward Points").setValue(0);
                    }
                    else{
                        total_spend.setText("Total Spend: $0");
                        reward_points.setText("Total Reward:0");
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            email_info.setText(user.getEmail());
        }
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        Log.v("storage", storageRef.toString());
        navigationView.setNavigationItemSelectedListener(this);
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapter.addFragment(new MenuFragment(), "MENU");
        viewPagerAdapter.addFragment(new HotMenuFragment(), "HOT");
        viewPagerAdapter.addFragment(new FavoriteFragment(), "FAVORITE");
        viewPagerAdapter.addFragment(new CouponFragment(), "COUPON");
        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    SearchView searchView;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        // Associate searchable configuration with the SearchView
        searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_about) {
            return true;
        }
        if (id == R.id.action_order) {
            Intent i = new Intent(this, OrderActivity.class);
            startActivity(i);
        }
        if (id == R.id.action_waiter) {
            final DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Table");
            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    String table = getKeysByValue(map, mAuth.getCurrentUser().getUid());
                    if (table != null && !table.equals("")) {
                        ref.child(table).child("Status").setValue("Call Waiter");
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.action_assessment) {
            // Handle the camera action
        } else if (id == R.id.action_feedback) {

        } else if (id == R.id.action_about) {

        } else if (id == R.id.sign_out) {
            mAuth.getInstance().signOut();
            Intent i = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(i);
        } else if (id == R.id.update_menu) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onDestroy() {
        mDatabase = FirebaseDatabase.getInstance().getReference();
//        mDatabase.child("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("order").removeValue();
        super.onDestroy();
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

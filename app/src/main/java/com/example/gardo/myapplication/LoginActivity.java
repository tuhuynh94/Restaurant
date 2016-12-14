package com.example.gardo.myapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.Toast;

import com.example.gardo.myapplication.Model.DiaglogModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference mDatabase;
    private boolean check;
    private String role;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pre_login);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.hide();
        mAuth = FirebaseAuth.getInstance();
        Button pre_sign = (Button) findViewById(R.id.type_sign_in);
        mDatabase = FirebaseDatabase.getInstance().getReference();
//        final FirebaseUser userFire = mAuth.getCurrentUser();
//        final DatabaseReference like = mDatabase.child("user").child(userFire.getUid()).child("like");
        pre_sign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlphaAnimation buttonClick = new AlphaAnimation(1F, 0.8F);
                view.startAnimation(buttonClick);
                Intent i = new Intent(LoginActivity.this, LoginMainActivity.class);
                startActivity(i);
            }
        });
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
//                    if(mAuth.getCurrentUser().isAnonymous()){
//                        Toast.makeText(getApplicationContext(),"Sign with Anynomous", Toast.LENGTH_SHORT).show();
//                        mDatabase.child("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("order").removeValue();
//                        Intent i = new Intent(LoginActivity.this, MainActivity.class);
//                        startActivity(i);
//                    }
//                    else{
//                        DatabaseReference ref = mDatabase.child("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Information");
//                        mDatabase.child("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("order").removeValue();
//                        ref.child("Role").addListenerForSingleValueEvent(new ValueEventListener() {
//                            @Override
//                            public void onDataChange(DataSnapshot dataSnapshot) {
//                                role = (String) dataSnapshot.getValue();
//                                    if (role != null && role.equals("User")) {
//                                        Toast.makeText(LoginActivity.this, "Sign in " + mAuth.getCurrentUser().getDisplayName(), Toast.LENGTH_SHORT).show();
//                                        Intent i = new Intent(LoginActivity.this, MainActivity.class);
//                                        startActivity(i);
//                                    } else if (role != null && role.equals("Staff")) {
//                                        Toast.makeText(LoginActivity.this, "Signed in Staff", Toast.LENGTH_SHORT).show();
//                                        Intent i = new Intent(LoginActivity.this, StaffActivity.class);
//                                        i.putExtra("staff", "staff");
//                                        startActivity(i);
//                                    }
//                            }
//
//                            @Override
//                            public void onCancelled(DatabaseError databaseError) {
//
//                            }
//                        });
//                    }
                }
            }
        };
        Button sign_any = (Button) findViewById(R.id.type_sign_anonymous);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(mAuthListener);
    }

    public void sign_anonymous(View view) {
        final AlphaAnimation buttonClick = new AlphaAnimation(1F, 0.8F);
        view.startAnimation(buttonClick);
        mAuth.signOut();
        final ProgressDialog mProgress = new ProgressDialog(this);
        mProgress.setMessage("Sign with Anonymous");
        mProgress.show();
        mAuth.signInAnonymously().addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser user = mAuth.getCurrentUser();
                    mDatabase = FirebaseDatabase.getInstance().getReference();
                    DatabaseReference ref = mDatabase.child("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Information");
                    ref.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            check = dataSnapshot.hasChildren();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                    if(!check){
                        if(mAuth.getCurrentUser().isAnonymous()){
                            ref.child("Display Name").setValue("");
                            ref.child("Email").setValue("");
                            ref.child("Role").setValue("Anynomous");
                        }
                    }
                    if(user != null) {
                        String userID = user.getUid().toString();
                        Toast.makeText(getApplicationContext(), "guest-" + userID.substring(userID.length() - 4, userID.length()), Toast.LENGTH_SHORT).show();
                    }
                    mDatabase.child("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("order").removeValue();
                    mProgress.dismiss();
//                    Intent i = new Intent(LoginActivity.this, MainActivity.class);
//                    startActivity(i);
                    DiaglogModel myDiaglog = new DiaglogModel();
                    myDiaglog.show(getFragmentManager(), "show_diaglod");
                }
            }
        });
    }
}

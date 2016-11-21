package com.example.gardo.myapplication;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginMainActivity extends AppCompatActivity {
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private EditText email, password;
    private Button sign_in;
    private boolean check;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d("Auth", "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d("Auth", "onAuthStateChanged:signed_out");
                }
            }
        };
        TextView register = (TextView) findViewById(R.id.register_account);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(LoginMainActivity.this, Register.class);
                startActivity(i);
            }
        });
        email = (EditText) findViewById(R.id.email_input);
        password = (EditText) findViewById(R.id.password_input);
        sign_in = (Button) findViewById(R.id.sign_in);
            sign_in.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(email.getText().toString().equals("admin@123.com") && password.getText().toString().equals("admin123")){
                        Toast.makeText(LoginMainActivity.this, "Signed with Adminstrator", Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(LoginMainActivity.this, MainActivity.class);
                        i.putExtra("admin", "admin");
                        startActivity(i);
                    }
                    else {
                        mAuth.signInWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                                .addOnCompleteListener(LoginMainActivity.this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
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
                                            String displayName = mAuth.getCurrentUser().getDisplayName();
                                            String email = mAuth.getCurrentUser().getEmail();
                                            if(displayName != null) {
                                                ref.child("Display Name").setValue(displayName);
                                            }
                                            else{
                                                ref.child("Display Name").setValue("");
                                            }
                                            if(email != null) {
                                                ref.child("Email").setValue(email);
                                            }
                                            else{
                                                ref.child("Email").setValue("");
                                            }
                                            if(!mAuth.getCurrentUser().isAnonymous()){
                                                ref.child("Role").setValue("User");
                                            }
                                        }
                                        mDatabase.child("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("order").removeValue();
                                        if (!task.isSuccessful()) {
                                            Toast.makeText(LoginMainActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(LoginMainActivity.this, "Signed in", Toast.LENGTH_SHORT).show();
                                            Intent i = new Intent(LoginMainActivity.this, MainActivity.class);
                                            startActivity(i);
                                        }
                                    }
                                });
                    }
                }
            });
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
}

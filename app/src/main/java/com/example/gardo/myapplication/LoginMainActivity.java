package com.example.gardo.myapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
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

import java.util.List;

public class LoginMainActivity extends AppCompatActivity {
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private EditText email, password;
    private Button sign_in;
    private boolean check;
    private String role;
    DatabaseReference ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.hide();
        final ProgressDialog mProgress = new ProgressDialog(this);
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
                    final AlphaAnimation buttonClick = new AlphaAnimation(1F, 0.8F);
                    view.startAnimation(buttonClick);
                    String e = email.getText().toString();
                    String p = password.getText().toString();
                    if (!email.getText().toString().equals("") && !password.getText().toString().equals("")) {
                        if (email.getText().toString().equals("admin@123.com") && password.getText().toString().equals("admin123")) {
                            Toast.makeText(LoginMainActivity.this, "Signed with Adminstrator", Toast.LENGTH_SHORT).show();
                            Intent i = new Intent(LoginMainActivity.this, AdminActivity.class);
                            i.putExtra("admin", "admin");
                            startActivity(i);
                        } else {
                            mProgress.setMessage("Sign with " + email.getText().toString());
                            mProgress.show();
                            mAuth.signInWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                                    .addOnCompleteListener(LoginMainActivity.this, new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull final Task<AuthResult> task) {
                                            if(task.isSuccessful()) {
                                                mDatabase = FirebaseDatabase.getInstance().getReference();
                                                ref = mDatabase.child("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Information");
                                                mDatabase.child("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("order").removeValue();
                                                ref.child("Role").addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                                        role = (String) dataSnapshot.getValue();
                                                        if (!task.isSuccessful()) {
                                                            Toast.makeText(LoginMainActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                        } else {
                                                            if (role != null && role.equals("User")) {
                                                                Toast.makeText(LoginMainActivity.this, "Signed in", Toast.LENGTH_SHORT).show();
                                                                Intent i = new Intent(LoginMainActivity.this, MainActivity.class);
                                                                startActivity(i);
                                                            } else if (role != null && role.equals("Staff")) {
                                                                Toast.makeText(LoginMainActivity.this, "Signed in Staff", Toast.LENGTH_SHORT).show();
                                                                Intent i = new Intent(LoginMainActivity.this, StaffActivity.class);
                                                                i.putExtra("staff", "staff");
                                                                startActivity(i);
                                                            }
                                                            mProgress.dismiss();
                                                        }
                                                    }

                                                    @Override
                                                    public void onCancelled(DatabaseError databaseError) {

                                                    }
                                                });
                                            }
                                            else{
                                                Toast.makeText(getApplicationContext(), task.getException().getMessage().toString(), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        }
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

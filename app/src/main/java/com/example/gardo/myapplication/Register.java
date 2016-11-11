package com.example.gardo.myapplication;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
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

public class Register extends AppCompatActivity {
    private Button sign_up;
    private EditText email, user_name, password, confirm_password;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        sign_up = (Button) findViewById(R.id.sign_up);
        email = (EditText) findViewById(R.id.email_sign_up);
        user_name = (EditText) findViewById(R.id.user_name);
        password = (EditText) findViewById(R.id.password_sign_up);
        confirm_password = (EditText) findViewById(R.id.confirm_password);
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
                // ...
            }
        };
        sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean check = true;
                if (user_name.getText().toString().length() == 0) {
                    user_name.setError("Field cannot empty");
                    check = false;
                }
                if (email.getText().toString().length() == 0) {
                    email.setError("Field cannot empty");
                    check = false;
                }
                if (password.getText().toString().length() == 0) {
                    password.setError("Field cannot empty");
                    check = false;
                }
                if (confirm_password.getText().toString().length() == 0) {
                    confirm_password.setError("Field cannot empty");
                    check = false;
                }
                if (!password.getText().toString().equals(confirm_password.getText().toString())) {
                    confirm_password.setError("Password confirmation must match");
                    check = false;
                }
                if (!isValidEmail(email.getText().toString())) {
                    email.setError("This not email address");
                    check = false;
                }
                if (check) {
                    mAuth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                            .addOnCompleteListener(Register.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (!task.isSuccessful()) {
                                        Toast.makeText(Register.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                    else{
                                        Toast.makeText(Register.this, "Successful registration", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                    Intent i = new Intent(Register.this, LoginActivity.class);
                    startActivity(i);
                }
            }
        });
    }
    public final static boolean isValidEmail(CharSequence target) {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
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

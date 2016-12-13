package com.example.gardo.myapplication;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gardo.myapplication.Model.DiaglogModel;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookActivity;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;
import java.util.List;

public class LoginMainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private EditText email, password;
    private Button sign_in;
    private boolean check;
    private String role;
    DatabaseReference ref;
    private static final int RC_SIGN_IN = 9001;
    private GoogleApiClient mGoogleApiClient;
    private CallbackManager mCallbackManager;
    ProgressDialog mProgress_google;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(LoginMainActivity.this.getApplicationContext());
        setContentView(R.layout.activity_login);
        FirebaseAuth.getInstance().signOut();
        facebookSign();
        mProgress_google = new ProgressDialog(this);
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
                                        if (task.isSuccessful()) {
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
                                                            Toast.makeText(LoginMainActivity.this, "Signed in " + email.getText().toString(), Toast.LENGTH_SHORT).show();
                                                            DiaglogModel myDiaglog = new DiaglogModel();
                                                            myDiaglog.show(getFragmentManager(), "show_diaglod");
//                                                            Intent i = new Intent(LoginMainActivity.this, MainActivity.class);
//                                                            startActivity(i);
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
                                        } else {
                                            Toast.makeText(getApplicationContext(), task.getException().getMessage().toString(), Toast.LENGTH_SHORT).show();
                                            mProgress.dismiss();
                                        }
                                    }
                                });
                    }
                }
            }
        });
        TextView forgot_password = (TextView) findViewById(R.id.forgot_password);
        final EditText input = new EditText(this);
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        forgot_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.setTitle("Forgot Password");
                alertDialog.setMessage("Enter your email to reset password");
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
                input.setLayoutParams(lp);
                input.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                alertDialog.setView(input);
                alertDialog.setPositiveButton("OK", null);
                alertDialog.setNegativeButton("Cancel", null);
                AlertDialog dialog = alertDialog.create();
                dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(final DialogInterface dialog) {
                        Button possitive = ((AlertDialog)dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                        Button negative = ((AlertDialog)dialog).getButton(AlertDialog.BUTTON_NEGATIVE);
                        possitive.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String email_forgot = input.getText().toString();
                                if(isValidEmail(email_forgot)) {
                                    FirebaseAuth auth = FirebaseAuth.getInstance();

                                    auth.sendPasswordResetEmail(email_forgot)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Toast.makeText(getApplicationContext(), "Email forgot password sent", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                    dialog.dismiss();
                                }
                                else{
                                    input.setError("This not Email Address");
                                    Toast.makeText(getApplicationContext(), "Email Address in invalid format", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                        negative.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });
                    }
                });
                dialog.show();
            }
        });
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        googleSign();
    }

    private void facebookSign() {

        mCallbackManager = CallbackManager.Factory.create();
        LoginButton loginButton = (LoginButton) findViewById(R.id.facebook_sign);
        loginButton.setReadPermissions("email", "public_profile");
        loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                // ...
            }

            @Override
            public void onError(FacebookException error) {
                // ...
            }
        });
    }

    private void handleFacebookAccessToken(AccessToken token) {

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Toast.makeText(LoginMainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                        else{
//                            Intent i = new Intent(LoginMainActivity.this, MainActivity.class);
//                            startActivity(i);
                            DiaglogModel myDiaglog = new DiaglogModel();
                            myDiaglog.show(getFragmentManager(), "show_diaglod");
                        }
                    }
                });
    }

    private void googleSign() {
        Button google_sign = (Button) findViewById(R.id.google_sign);
        google_sign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProgress_google.setMessage("Sign in with gmail");
                mProgress_google.show();
                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                // Google Sign In failed, update UI appropriately
                // ...
            }
        }
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            Toast.makeText(LoginMainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        } else {
//                            Intent i = new Intent(LoginMainActivity.this, MainActivity.class);
//                            startActivity(i);
                            mProgress_google.dismiss();
                            DiaglogModel myDiaglog = new DiaglogModel();
                            myDiaglog.show(getFragmentManager(), "show_diaglod");
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

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
    public final static boolean isValidEmail(CharSequence target) {
        if (target == null) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }
}

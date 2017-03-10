package com.microsoft.track_my_task;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.auth.api.*;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInApi;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

/**
 * Created by Ayshu on 09-Mar-17.
 * hgctgvhyv
 */

public class Google_signin extends FragmentActivity {
    private static final int RC_SIGN_IN = 1 ;
    SignInButton signInButton;

    String TAG = "info";
    GoogleApiClient mGoogleApiClient;
    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthListner;
    ProgressDialog progressDialog;
    Button skip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.google_signin);
        Log.i(TAG, "onCreate: jus entered");
        progressDialog = new ProgressDialog(Google_signin.this);
        progressDialog.setMessage("redirecting please wait....");
        progressDialog.setProgress(100);
        progressDialog.setCancelable(true);
        mAuth = FirebaseAuth.getInstance();
        skip = (Button) findViewById(R.id.skip);


        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Google_signin.this, HomeActivity.class);
                startActivity(intent);
            }
        });



        mAuthListner = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser() != null) {
                    Log.i(TAG, "onAuthStateChanged: user is " + firebaseAuth.getCurrentUser());
                    progressDialog.dismiss();
                    Toast.makeText(getBaseContext(), "auth done!!! ", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(Google_signin.this, HomeActivity.class));
                }
                else{
                    Log.i(TAG, "onAuthStateChanged: user is null");
                    Toast.makeText(Google_signin.this,  "onAuthStateChanged: user is null", Toast.LENGTH_LONG).show();
                }
            }
        };

        signInButton = (SignInButton)findViewById(R.id.google_button);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                .enableAutoManage(Google_signin.this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Log.i(TAG, "onConnectionFailed: unable to connect");
                        Toast.makeText(getBaseContext(), "Connection failed", Toast.LENGTH_SHORT).show();
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();


        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "onClick: clicked the button");
                progressDialog.show();
                signIn();
            }
        });

    }
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        // progressDialog.hide();
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.i(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.i(TAG, "signInWithCredential", task.getException());
                            Toast.makeText(Google_signin.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Toast.makeText(Google_signin.this, "Authentication successss!!!.",
                                    Toast.LENGTH_SHORT).show();
                        }
                        // ...
                    }
                });
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i(TAG, "onActivityResult: in activity result completed");
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
       // Log.i(TAG, "onActivityResult: " + (requestCode );
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
                Log.i(TAG, "onActivityResult: " + account);
                Toast.makeText(this,"successs signing in!!!", Toast.LENGTH_LONG).show();

            } else {
                // Google Sign In failed, update UI appropriately
                // ...
                Toast.makeText(this,"Google singn ehbduken", Toast.LENGTH_LONG).show();
            }
        }
    }
    private void signIn() {
        //progressDialog.hide();
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListner);
    }
}

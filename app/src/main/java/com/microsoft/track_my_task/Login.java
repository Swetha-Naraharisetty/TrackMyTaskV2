package com.microsoft.track_my_task;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

/**
 * Created by Ayshu on 21-Feb-17.
 * hgctgvhyv
 */

public class Login extends Activity {
    EditText email, pass;
    Button register, login, skip;
    FirebaseAuth firebaseAuth ;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.email_login);
        email = (EditText) findViewById(R.id.email);
        pass = (EditText) findViewById(R.id.pass);
        register = (Button)findViewById(R.id.register);
        login = (Button)findViewById(R.id.signin);
        skip = (Button)findViewById(R.id.skip);

        email.setText("ayshwaryasree@gmail.com");
        pass.setText("edwardcullen");
        progressDialog = new ProgressDialog(Login.this);

        firebaseAuth = FirebaseAuth.getInstance();

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registeruser();
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userLogin();
            }
        });

        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Login.this, HomeActivity.class);
                finish();
                startActivity(intent);
            }
        });
    }



    void registeruser(){
        String emailid = email.getText().toString();
        String password = pass.getText().toString();
        if(emailid.equals("")){
            Toast.makeText(Login.this, "please enter user name", Toast.LENGTH_LONG).show();
        }
        else if(password.equals("")){
            Toast.makeText(Login.this, "please enter password", Toast.LENGTH_LONG).show();
        }
        progressDialog.setMessage("Please wait");
        progressDialog.show();

        firebaseAuth.createUserWithEmailAndPassword(emailid, password)
                .addOnCompleteListener(Login.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();
                        if(task.isSuccessful()){
                            Intent intent = new Intent(Login.this, HomeActivity.class);
                            Toast.makeText(Login.this, "registration sucessful", Toast.LENGTH_LONG).show();
                            finish();
                            startActivity(intent);

                        }
                        else{
                            Toast.makeText(Login.this, "registration uncessful please try again", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    void userLogin(){
        String emailid = email.getText().toString();
        String password = pass.getText().toString();
        if(emailid.equals("")){
            Toast.makeText(Login.this, "please enter user name", Toast.LENGTH_LONG).show();
        }
        else if(password.equals("")){
            Toast.makeText(Login.this, "please enter password", Toast.LENGTH_LONG).show();
        }
        progressDialog.setMessage("Please wait");
        progressDialog.show();

        firebaseAuth.signInWithEmailAndPassword(emailid, password)
                .addOnCompleteListener(Login.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();
                        if(task.isSuccessful()){
                            Intent intent = new Intent(Login.this, HomeActivity.class);
                            Toast.makeText(Login.this, "Login sucessful", Toast.LENGTH_LONG).show();
                            finish();
                            startActivity(intent);

                        }
                        else{
                            Toast.makeText(Login.this, "Login unsucessful..please try again", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}

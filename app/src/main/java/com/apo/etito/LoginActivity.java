package com.apo.etito;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


import Model.Users;
import Prevalent.Prevalent;

public class LoginActivity extends AppCompatActivity {

    private EditText inputEmail, inputPassword;
    private FirebaseAuth auth;
    private Button btnSigIn, btnChangePassword;
    private ProgressDialog loadingBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loadingBar = new ProgressDialog(this);
        loadingBar.setMessage("Loading.....");
        loadingBar.setCancelable(true);
        loadingBar.setCanceledOnTouchOutside(false);

        auth = FirebaseAuth.getInstance();

        inputEmail = (EditText) findViewById(R.id.login_input_email);
        inputPassword = (EditText) findViewById(R.id.login_input_password);
        btnSigIn = (Button) findViewById(R.id.sign_in_btn);
        btnChangePassword =(Button) findViewById(R.id.forgot_or_change__password_btn);




        btnSigIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String email = inputEmail.getText().toString();
                final String password = inputPassword.getText().toString();
                try {

                    if (password.length()> 0 && email.length()> 0){
                        loadingBar.show();
                        auth.signInWithEmailAndPassword(email,password).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (!task.isSuccessful()){
                                    Toast.makeText(LoginActivity.this, "Authentication Failed", Toast.LENGTH_SHORT).show();
                                    Log.v("error", task.getResult().toString());
                                } else{
                                    Toast.makeText(LoginActivity.this,"You logged in successfully",Toast.LENGTH_LONG).show();
                                    loadingBar.dismiss();
                                    Intent intent = new Intent(LoginActivity.this,HomeActivity.class);
                                    Users users = new Users(email,password);
                                    Prevalent.currentOnLineUsers = users;
                                    startActivity(intent);
                                    finish();
                                }

                                loadingBar.dismiss();

                            }
                        });
                    } else{
                        Toast.makeText(LoginActivity.this,"All fields are mandatory",Toast.LENGTH_LONG).show();
                    }



                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        });


        btnChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this,ForgotAndChangePasswordActivity.class);
                startActivity(intent);
            }
        });


    }
}

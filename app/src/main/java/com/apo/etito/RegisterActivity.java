package com.apo.etito;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import Model.Users;

public class RegisterActivity extends AppCompatActivity {
    private EditText inputEmail, inputPassword;
    private FirebaseAuth auth;
    private ProgressDialog loadingBar;
    private Button btnSignUp;
    private Button btnAdminSignUp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        loadingBar = new ProgressDialog(this);
        loadingBar.setMessage("Loading....");
        loadingBar.setCancelable(true);
        loadingBar.setCanceledOnTouchOutside(false);

        auth = FirebaseAuth.getInstance();

//        if (auth.getCurrentUser()!= null){
//            startActivity(new Intent(RegisterActivity.this,MainActivity.class));
//            finish();
//        }

        inputEmail = (EditText) findViewById(R.id.register_input_email);
        inputPassword = (EditText) findViewById(R.id.register_input_password);
        btnSignUp = (Button) findViewById(R.id.sign_up_btn);
        btnAdminSignUp = (Button) findViewById(R.id.admin_register_btn);


        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String email = inputEmail.getText().toString();
                final String password = inputPassword.getText().toString();
                try {

                    if (password.length() > 0 && email.length() > 0) {
                        loadingBar.show();
                        auth.createUserWithEmailAndPassword(email, password)
                                .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
//                                        if (!task.isSuccessful()) {
//                                            Toast.makeText(RegisterActivity.this, "Authentication Failed", Toast.LENGTH_LONG
//                                            ).show();
//                                            Log.v("error", task.getResult().toString());
//                                        } else {
//                                            Toast.makeText(RegisterActivity.this,"Congratulations your account has been created successfully",Toast.LENGTH_LONG).show();
//                                            loadingBar.dismiss();
//                                            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
//                                            startActivity(intent);
//                                            finish();
//                                        }
                                        if (task.isSuccessful()){
                                            Users users = new Users(email,password);
                                            FirebaseDatabase.getInstance().getReference("Users")
                                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                    .setValue(users).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()){
                                                       Toast.makeText(RegisterActivity.this,"Congratulations your account has been created successfully",Toast.LENGTH_LONG).show();
                                                       loadingBar.dismiss();
                                                       Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                                       startActivity(intent);
                                                    } else {
                                                        Toast.makeText(RegisterActivity.this,task.getException().getMessage(),Toast.LENGTH_LONG).show();
                                                    }

                                                }
                                            });

                                        }

                                        loadingBar.dismiss();
                                    }
                                });
                    } else {
                        Toast.makeText(RegisterActivity.this, "All Fields are mandatory", Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        btnAdminSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String email = inputEmail.getText().toString();
                final String password = inputPassword.getText().toString();

                try {
                    if (password.length() > 0 && email.length() > 0) {
                        loadingBar.show();
                        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Users users = new Users(email, password);
                                    FirebaseDatabase.getInstance().getReference("Admins")
                                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                            .setValue(users).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {

                                                Toast.makeText(RegisterActivity.this, "Congratulations, Admin Account has been created successfully", Toast.LENGTH_LONG).show();
                                                loadingBar.dismiss();
                                                Intent intent = new Intent(RegisterActivity.this, AdminLoginActivity.class);
                                                startActivity(intent);

                                            } else {
                                                Toast.makeText(RegisterActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();

                                            }
                                        }
                                    });
                                }

                                loadingBar.dismiss();
                            }
                        });

                    } else {
                        Toast.makeText(RegisterActivity.this, "All Fields are mandatory", Toast.LENGTH_LONG).show();
                    }

                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        });


    }
}



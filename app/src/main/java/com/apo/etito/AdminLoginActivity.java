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



public class AdminLoginActivity extends AppCompatActivity {

    private EditText AdminLoginInputEmail, AdminLoginInputPassword;
    private FirebaseAuth auth;
    private Button btnAdminSignIn;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_login);

        loadingBar = new ProgressDialog(this);
        loadingBar.setMessage("Loading.....");
        loadingBar.setCancelable(true);
        loadingBar.setCanceledOnTouchOutside(false);

        auth = FirebaseAuth.getInstance();

        AdminLoginInputEmail = (EditText) findViewById(R.id.admin_login_input_email);
        AdminLoginInputPassword = (EditText) findViewById(R.id.admin_login_input_password);
        btnAdminSignIn = (Button) findViewById(R.id.admin_sign_in_btn);

        btnAdminSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String email = AdminLoginInputEmail.getText().toString();
                final String password = AdminLoginInputPassword.getText().toString();
                try {

                    if (password.length()> 0 && email.length()> 0){
                        loadingBar.show();
                        auth.signInWithEmailAndPassword(email,password).addOnCompleteListener(AdminLoginActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (!task.isSuccessful()){
                                    Toast.makeText(AdminLoginActivity.this, "Authentication Failed", Toast.LENGTH_SHORT).show();
                                    Log.v("error", task.getResult().toString());
                                } else{
                                    Toast.makeText(AdminLoginActivity.this,"You logged in successfully",Toast.LENGTH_LONG).show();
                                    loadingBar.dismiss();
                                    Intent intent = new Intent(AdminLoginActivity.this,AdminCategoryActivity.class);
//                                    Users users = new Users(email,password);
//                                    Prevalent.currentOnLineUsers = users;
                                    startActivity(intent);

                                }

                                loadingBar.dismiss();

                            }
                        });
                    } else{
                        Toast.makeText(AdminLoginActivity.this,"All fields are mandatory",Toast.LENGTH_LONG).show();
                    }



                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

    }
}

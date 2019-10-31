package com.apo.etito;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.HashMap;

import Prevalent.Prevalent;
import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {

    private CircleImageView profileImageView;
    private EditText emailEditText, addressEditText;
    private TextView profileChangeTextBtn, closeTextBtn, saveTextButton;

    private Uri imageUri;
    private String myUrl = "";
    private StorageTask uploadTask;
    private StorageReference storageProfilePictureRef;
    private String checker = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        storageProfilePictureRef = FirebaseStorage.getInstance().getReference("Profile pictures");

        profileImageView = (CircleImageView) findViewById(R.id.settings_profile_image);
        emailEditText = (EditText) findViewById(R.id.settings_email);
        addressEditText = (EditText) findViewById(R.id.settings_address);
        profileChangeTextBtn = (TextView) findViewById(R.id.profile_image_change_btn);
        closeTextBtn = (TextView) findViewById(R.id.close_settings_btn);
        saveTextButton = (TextView) findViewById(R.id.update_account_settings_btn);

        userInfoDisplay(profileImageView,emailEditText,addressEditText);

        closeTextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        saveTextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checker.equals("clicked")){

                    userInfoSaved();

                } else{

                    updateOnlyUserInfo();

                }
            }
        });

        profileChangeTextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checker = "clicked";
                CropImage.activity(imageUri)
                        .setAspectRatio(1,1)
                        .start(SettingsActivity.this);


            }
        });





    }

    private void updateOnlyUserInfo() {

        HashMap<String,Object> userMap = new HashMap<>();
        userMap.put("email",emailEditText.getText().toString());
        userMap.put("address",addressEditText.getText().toString());
         FirebaseDatabase.getInstance().getReference("SettingsUsersInfo")
        .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).updateChildren(userMap);







        startActivity(new Intent(SettingsActivity.this,HomeActivity.class));
        Toast.makeText(SettingsActivity.this, "Profile info updated successfully", Toast.LENGTH_SHORT).show();
        finish();


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode==RESULT_OK && data!=null){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            imageUri = result.getUri();
            profileImageView.setImageURI(imageUri);
        }
        else{
            Toast.makeText(this, "Error, Try again", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(SettingsActivity.this,SettingsActivity.class));
            finish();
        }
    }

    private void userInfoSaved() {

        if (TextUtils.isEmpty(emailEditText.getText().toString())){
            Toast.makeText(this, "Name is mandatory", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(addressEditText.getText().toString())){
            Toast.makeText(this, "Address is mandatory", Toast.LENGTH_SHORT).show();
        } else if (checker.equals("clicked")){
            uploadImage();
        }
    }

    private void uploadImage() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Update Profile");
        progressDialog.setMessage("Please wait, while we are updating your account information");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        if (imageUri != null){
            final StorageReference fileRef = storageProfilePictureRef
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid()+ ".jpg");
            uploadTask = fileRef.putFile(imageUri);
            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {

                    if (!task.isSuccessful()){
                        throw task.getException();
                    }
                    return fileRef.getDownloadUrl();
                }
            })
            .addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()){
                        Uri downloadUrl = task.getResult();
                        myUrl = downloadUrl.toString();


                        HashMap<String,Object> userMap = new HashMap<>();
                        userMap.put("email",emailEditText.getText().toString());
                        userMap.put("address",addressEditText.getText().toString());
                        userMap.put("image",myUrl);
                        FirebaseDatabase.getInstance().getReference("SettingsUsersInfo")
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).updateChildren(userMap);


                        progressDialog.dismiss();



                        startActivity(new Intent(SettingsActivity.this,HomeActivity.class));
                        Toast.makeText(SettingsActivity.this, "Profile info updated successfully", Toast.LENGTH_SHORT).show();
                        finish();

                    }

                    else {
                        progressDialog.dismiss();
                        Toast.makeText(SettingsActivity.this, "Error", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }
        else{
            Toast.makeText(SettingsActivity.this, "Image is not selected", Toast.LENGTH_SHORT).show();

        }


    }

    private void userInfoDisplay(final CircleImageView profileImageView, final EditText emailEditText, final EditText addressEditText) {
        FirebaseDatabase.getInstance().getReference("SettingsUsersInfo")
         .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
        .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists())
                {
                    if (dataSnapshot.child("image").exists())
                    {
                        String image = dataSnapshot.child("image").getValue().toString();
                        String email = dataSnapshot.child("email").getValue().toString();
                        String address = dataSnapshot.child("address").getValue().toString();
                        Picasso.get().load(image).into(profileImageView);

                        emailEditText.setText(email);
                        addressEditText.setText(address);





                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
   }
}

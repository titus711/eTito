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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;


public class AdminAddNewProductActivity extends AppCompatActivity {

    private String categoryName, description, price, pname,saveCurrentDate, saveCurrentTime;
    private Button addNewProductButton;
    private ImageView inputProductImage;
    private EditText inputProductName, inputProductDescription, inputProductPrice;
    private static final int GalleryPick = 1;
    private Uri imageUri;
    private String productRandomKey,downLoadImageUrl;
    private StorageReference productImagesRef;
    private DatabaseReference productsRef;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_new_product);

        categoryName = getIntent().getExtras().get("category").toString();
        productImagesRef = FirebaseStorage.getInstance().getReference().child("Product Images");
        productsRef = FirebaseDatabase.getInstance().getReference().child("Products");
        addNewProductButton = (Button) findViewById(R.id.add_new_product);
        inputProductImage = (ImageView) findViewById(R.id.select_product_image);
        inputProductName = (EditText) findViewById(R.id.product_name);
        inputProductDescription = (EditText) findViewById(R.id.product_description);
        inputProductPrice = (EditText) findViewById(R.id.product_price);
        loadingBar = new ProgressDialog(this);


        inputProductImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery();
            }
        });

        addNewProductButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                validateProductData();

            }
        });


    }

    private void openGallery() {

        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, GalleryPick);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GalleryPick && resultCode == RESULT_OK && data != null) {

            imageUri = data.getData();
            inputProductImage.setImageURI(imageUri);
        }
    }

    private void validateProductData() {

        description = inputProductDescription.getText().toString();
        price = inputProductPrice.getText().toString();
        pname = inputProductName.getText().toString();

        if (imageUri == null) {

            Toast.makeText(this, "Product image is mandatory...", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(description)) {

            Toast.makeText(this, "Please write product description...", Toast.LENGTH_SHORT).show();


        } else if (TextUtils.isEmpty(price)) {

            Toast.makeText(this, "Please write product price...", Toast.LENGTH_SHORT).show();


        } else if (TextUtils.isEmpty(pname)) {

            Toast.makeText(this, "Please write product name...", Toast.LENGTH_SHORT).show();


        }

        else{
           storeProductInformation();
        }
    }

    private void storeProductInformation() {

        loadingBar.setTitle("Add New Product");
        loadingBar.setMessage("Dear Admin , Please wait while we are adding the new product.");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();

        Calendar calender = Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd,yyyy");
        saveCurrentDate = currentDate.format(calender.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime = currentTime.format(calender.getTime());

        productRandomKey = saveCurrentDate + saveCurrentTime;

        final StorageReference filePath = productImagesRef.child(imageUri.getLastPathSegment() + productRandomKey + ".jpg");
        final UploadTask uploadTask = filePath.putFile(imageUri);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                String message = e.toString();
                Toast.makeText(AdminAddNewProductActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                loadingBar.dismiss();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(AdminAddNewProductActivity.this, "Product Image uploaded Successfully....", Toast.LENGTH_SHORT).show();

                Task<Uri> uriTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()){
                           throw task.getException();

                        }

                        downLoadImageUrl = filePath.getDownloadUrl().toString();
                        return  filePath.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {

                        if (task.isSuccessful()){
                            downLoadImageUrl = task.getResult().toString();
                            Toast.makeText(AdminAddNewProductActivity.this, "got the Product image Url Successfully...", Toast.LENGTH_SHORT).show();

                            saveProductInfoToDatabase();
                        }

                    }
                });
            }
        });


    }

    private void saveProductInfoToDatabase() {

        HashMap<String, Object>  productMap  = new HashMap<>();
        productMap.put("pid" , productRandomKey);
        productMap.put("date" , saveCurrentDate);
        productMap.put("time" , saveCurrentTime);
        productMap.put("description" , description);
        productMap.put("image" , downLoadImageUrl);
        productMap.put("category" , categoryName);
        productMap.put("price" , price);
        productMap.put("pname" , pname);

        productsRef.child(productRandomKey).updateChildren(productMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Intent intent = new Intent(AdminAddNewProductActivity.this,AdminCategoryActivity.class);
                            startActivity(intent);
                            loadingBar.dismiss();
                            Toast.makeText(AdminAddNewProductActivity.this, "Product is added successfully...", Toast.LENGTH_SHORT).show();
                        }

                        else{
                            loadingBar.dismiss();
                            String message = task.getException().toString();
                            Toast.makeText(AdminAddNewProductActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

}
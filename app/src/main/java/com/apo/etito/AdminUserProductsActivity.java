package com.apo.etito;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.apo.etito.ViewHolder.CartViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import Model.Cart;

public class AdminUserProductsActivity extends AppCompatActivity {

    private RecyclerView productsList;
    RecyclerView.LayoutManager layoutManager;
    private String userId = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_user_products);
        userId =  getIntent().getStringExtra("uid");
        productsList = findViewById(R.id.products_list);
        productsList.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        productsList.setLayoutManager(layoutManager);



        FirebaseDatabase.getInstance()
                .getReference("CartList").child("AdminView").child(userId).child("Products");
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Cart> options =
                new  FirebaseRecyclerOptions.Builder<Cart>()
                .setQuery(FirebaseDatabase.getInstance()
                        .getReference("CartList").child("AdminView").child(userId).child("Products"),Cart.class)
                .build();

        FirebaseRecyclerAdapter<Cart, CartViewHolder> adapter = new FirebaseRecyclerAdapter<Cart, CartViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull CartViewHolder cartViewHolder, int position, @NonNull Cart model) {
                cartViewHolder.txtProductQuantity.setText("Quantity = " + model.getQuantity());
                cartViewHolder.txtProductPrice.setText("Price = R" + model.getPrice());
                cartViewHolder.txtProductName.setText(model.getPname());


            }

            @NonNull
            @Override
            public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int position) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_items_layout,parent,false);
                CartViewHolder holder = new CartViewHolder(view);
                return holder;

            }
        };

        productsList.setAdapter(adapter);
        adapter.startListening();
    }
}

package com.apo.etito;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.apo.etito.ViewHolder.CartViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import Model.Cart;
import Prevalent.Prevalent;

public class CartActivity extends AppCompatActivity {

     private RecyclerView recyclerView;
     private RecyclerView.LayoutManager layoutManager;
     private Button nextProcessBtn;
     private TextView txtTotalAmount, txtMsg1 ;
     private int overTotalPrice = 0;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        recyclerView = findViewById(R.id.cart_list);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        nextProcessBtn = (Button) findViewById(R.id.next_process_btn);
        txtTotalAmount = (TextView) findViewById(R.id.total_price);
        txtMsg1 = (TextView) findViewById(R.id.msg1);

        nextProcessBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    txtTotalAmount.setText("Total Price = R" + String.valueOf(overTotalPrice));

                } catch (NumberFormatException e){
                    return;
                }
//                txtTotalAmount.setText("Total Price = R" + String.valueOf(overTotalPrice));
                Intent intent = new Intent(CartActivity.this,ConfirmFinalOrderActivity.class);
                intent.putExtra("Total Price",String.valueOf(overTotalPrice));
                startActivity(intent);
                finish();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        checkOrderState();

        final DatabaseReference cartListRef = FirebaseDatabase.getInstance().getReference("CartList");
        FirebaseRecyclerOptions<Cart> options =
                new FirebaseRecyclerOptions.Builder<Cart>()
                .setQuery(cartListRef.child("UserView")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .child("Products"),Cart.class)
                        .build();

        FirebaseRecyclerAdapter<Cart, CartViewHolder> adapter =
                new FirebaseRecyclerAdapter<Cart, CartViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull CartViewHolder cartViewHolder, int position, @NonNull final Cart model) {
                        try {


                            cartViewHolder.txtProductQuantity.setText("Quantity = " + model.getQuantity());
                            cartViewHolder.txtProductPrice.setText("Price = R" + model.getPrice());
                            cartViewHolder.txtProductName.setText(model.getPname());


                            int oneTypeTotalProduct = (Integer.valueOf(model.getPrice())) * Integer.valueOf(model.getQuantity());
                            overTotalPrice = overTotalPrice + oneTypeTotalProduct;
                        } catch(NumberFormatException e){
                            return;
                        }





                        cartViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                CharSequence options[] = new CharSequence[]
                                        {

                                                "Edit",
                                                "Delete"
                                };

                                AlertDialog.Builder builder = new AlertDialog.Builder(CartActivity.this);
                                builder.setTitle("Cart Options:");

                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if (which==0){
                                            Intent intent = new Intent(CartActivity.this,ProductDetailsActivity.class);
                                            intent.putExtra("pid",model.getPid());
                                            startActivity(intent);
                                        }

                                        if (which==1){
                                            cartListRef.child("UserView")
                                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                    .child("Products")
                                                    .child(model.getPid())
                                                    .removeValue()
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {

                                                            if (task.isSuccessful()){
                                                                Toast.makeText(CartActivity.this, "Item deleted successfully.", Toast.LENGTH_SHORT).show();
                                                                Intent intent = new Intent(CartActivity.this,HomeActivity.class);
                                                                startActivity(intent);
                                                            }

                                                        }
                                                    });
                                        }

                                    }
                                });
                                builder.show();
                            }
                        });





                    }

                    @NonNull
                    @Override
                    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_items_layout,parent,false);
                        CartViewHolder holder = new CartViewHolder(view);
                        return holder;
                    }
                };

        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    private void checkOrderState(){
        FirebaseDatabase.getInstance().getReference("Orders")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()){
                            String shippingState = dataSnapshot.child("state").getValue().toString();
                            String userName = dataSnapshot.child("name").getValue().toString();

                            if (shippingState.equals("shipped")){

                                txtTotalAmount.setText("Dear " + userName + "\n order is shipped successfully.");
                                recyclerView.setVisibility(View.GONE);
                                txtMsg1.setVisibility(View.VISIBLE);
                                txtMsg1.setText("Congratulations, your final order has been Shipped successfully.Soon you will receive your order by your door step ");
                                nextProcessBtn.setVisibility(View.GONE);
                                Toast.makeText(CartActivity.this, "You can purchase more products once you receive your first order", Toast.LENGTH_SHORT).show();

                            } else if (shippingState.equals("not shipped")){
                                txtTotalAmount.setText("Shipping State = not shipped");
                                recyclerView.setVisibility(View.GONE);
                                txtMsg1.setVisibility(View.VISIBLE);
                                nextProcessBtn.setVisibility(View.GONE);
                                Toast.makeText(CartActivity.this, "You can purchase more products once you receive your first order", Toast.LENGTH_SHORT).show();


                            }

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

    }

}

package com.apo.etito.ViewHolder;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.apo.etito.Interface.ItemClickListener;
import com.apo.etito.R;

public class CartViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView txtProductName, txtProductPrice, txtProductQuantity;
    private ItemClickListener itemClickListener;

    public CartViewHolder(@NonNull View itemView) {
        super(itemView);

        txtProductName = itemView.findViewById(R.id.cart_product_name);
        txtProductPrice = itemView.findViewById(R.id.cart_product_price);
        txtProductQuantity = itemView.findViewById(R.id.cart_product_quantity);

    }

    @Override
    public void onClick(View view) {

        itemClickListener.onClick(view, getAdapterPosition(),false);


    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }
}

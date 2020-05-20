package com.berhabzakarya.storedz.ViewHolder;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.berhabzakarya.storedz.Interface.ItemClickListener;
import com.berhabzakarya.storedz.R;

public class ProductViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public TextView name_product,description_product,price_product;
    public ImageView image_product;
    public  ItemClickListener listener;

    public ProductViewHolder(@NonNull View itemView) {
        super(itemView);
        name_product = itemView.findViewById(R.id.name_product_items);
        description_product = itemView.findViewById(R.id.description_product_items);
        image_product = itemView.findViewById(R.id.image_product_items);
        price_product = itemView.findViewById(R.id.price_product_items);
    }
    public void setOnClickListener(ItemClickListener listener){
        this.listener = listener;
    }
    @Override
    public void onClick(View v) {
        this.listener.onItemClick(v,getAdapterPosition(),false);
    }
}

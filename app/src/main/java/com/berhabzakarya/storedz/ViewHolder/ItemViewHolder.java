package com.berhabzakarya.storedz.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.berhabzakarya.storedz.Interface.ItemClickListener;
import com.berhabzakarya.storedz.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView name_product, description_product, price_product, state_approved;
    public ImageView image_product;
    public ItemClickListener listener;

    public ItemViewHolder(@NonNull View itemView) {
        super(itemView);
        name_product = itemView.findViewById(R.id.name_seller_items);
        description_product = itemView.findViewById(R.id.description_seller_items);
        image_product = itemView.findViewById(R.id.image_seller_items);
        price_product = itemView.findViewById(R.id.price_seller_items);
        state_approved = itemView.findViewById(R.id.state_approved_seller_items);
    }

    public void setOnClickListener(ItemClickListener listener) {
        this.listener = listener;
    }

    @Override
    public void onClick(View v) {
        this.listener.onItemClick(v, getAdapterPosition(), false);
    }
}

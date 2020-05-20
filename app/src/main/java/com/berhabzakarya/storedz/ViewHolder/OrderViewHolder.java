package com.berhabzakarya.storedz.ViewHolder;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.berhabzakarya.storedz.Interface.ItemClickListener;
import com.berhabzakarya.storedz.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class OrderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
    public TextView orderUserName,orderPhoneNumber,orderPrice,orderAddress,orderTimeAddess;
    public Button show_all_product;
    ItemClickListener itemClickListener;
    public OrderViewHolder(@NonNull View itemView) {
        super(itemView);
        orderUserName = itemView.findViewById(R.id.order_user_name);
        orderPhoneNumber = itemView.findViewById(R.id.order_phone_number);
        orderPrice = itemView.findViewById(R.id.order_product_price);
        orderTimeAddess = itemView.findViewById(R.id.order_date_time);
        orderAddress = itemView.findViewById(R.id.order_address);
        show_all_product = itemView.findViewById(R.id.show_all_products);
    }
    private void setOnClickListener(ItemClickListener itemClickListener){
        this.itemClickListener = itemClickListener;
    }
    @Override
    public void onClick(View v) {

    }
}

package com.berhabzakarya.storedz.Admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.berhabzakarya.storedz.Model.AdminOrder;
import com.berhabzakarya.storedz.Model.Cart;
import com.berhabzakarya.storedz.R;
import com.berhabzakarya.storedz.ViewHolder.CartViewHolder;
import com.berhabzakarya.storedz.ViewHolder.OrderViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AdminUserProductActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    DatabaseReference db;
    private String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_user_product);
        initViews();
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recycler_products_activity);
        layoutManager = new LinearLayoutManager(this);
        uid = getIntent().getExtras().getString("UID");
        db = FirebaseDatabase.getInstance().getReference().child("Cart List")
                .child("Admin View").child(uid).child("Products");
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<Cart> options =
                new FirebaseRecyclerOptions.Builder<Cart>().setQuery(db, Cart.class).build();

        FirebaseRecyclerAdapter<Cart, CartViewHolder> adapter =
                new FirebaseRecyclerAdapter<Cart, CartViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull CartViewHolder cartViewHolder, int i, @NonNull Cart cart) {
                        cartViewHolder.txtProductName.setText(cart.getName());
                        cartViewHolder.txtProductQuantity.setText(cart.getQuantity());
                        cartViewHolder.txtProductPrice.setText(cart.getPrice());
                    }
                    @NonNull
                    @Override
                    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_cart_activity, parent, false);
                        return new CartViewHolder(view);
                    }
                };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }
}

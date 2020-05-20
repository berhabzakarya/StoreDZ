package com.berhabzakarya.storedz.Buyers;

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

import com.berhabzakarya.storedz.Model.Cart;
import com.berhabzakarya.storedz.Prevalent.Prevalent;
import com.berhabzakarya.storedz.R;
import com.berhabzakarya.storedz.ViewHolder.CartViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CartActivity extends AppCompatActivity {
    TextView price,msg1;
    Button next;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    private double finalPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        initViews();
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        next.setEnabled(false);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CartActivity.this, ConfirmFinalOrderActivity.class).putExtra("FINAL_PRICE", String.valueOf(finalPrice)));
            }
        });
        checkOrders();
    }

    private void initViews() {
        msg1 = findViewById(R.id.msg1);
        price = findViewById(R.id.price_cart_view);
        next = findViewById(R.id.next_proccess_btn);
        recyclerView = findViewById(R.id.recycler_cart_view);
        layoutManager = new LinearLayoutManager(this);
    }

    private void checkOrders(){
        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        db.child("Orders").child(Prevalent.currentOnlineUser.getPhone()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    String user_name = dataSnapshot.child("name").getValue(String.class);
                    String state = dataSnapshot.child("state").getValue(String.class);
                    if(state.equals("not shipped")){
                        price.setText("State shipped = no shipped");
                        Toast.makeText(CartActivity.this, "You can puchase more product, once you received your orders ", Toast.LENGTH_SHORT).show();
                        recyclerView.setVisibility(View.GONE);
                        msg1.setVisibility(View.VISIBLE);
                        next.setVisibility(View.GONE);
                    }else if(state.equals("shipped")){
                        price.setText("Dear "+user_name +" order is shipped successfully.");
                        recyclerView.setVisibility(View.GONE);
                        msg1.setVisibility(View.VISIBLE);
                        msg1.setText("Congratulation ,your final order has been shipped successfully , Soon you will received your order at your door step.");
                        next.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        finalPrice = 0;
        final DatabaseReference cartListRef = FirebaseDatabase.getInstance().getReference().child("Cart List");
        FirebaseRecyclerOptions<Cart> options = new FirebaseRecyclerOptions.Builder<Cart>()
                .setQuery(cartListRef.child("User View")
                        .child(Prevalent.currentOnlineUser.getPhone())
                        .child("Products"), Cart.class).build();
        FirebaseRecyclerAdapter<Cart, CartViewHolder> adapter =
                new FirebaseRecyclerAdapter<Cart, CartViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull CartViewHolder cartViewHolder, int i, @NonNull final Cart cart) {
                        cartViewHolder.txtProductPrice.setText("Price " + cart.getPrice() + " $");
                        cartViewHolder.txtProductName.setText("" + cart.getName());
                        cartViewHolder.txtProductQuantity.setText("Quantity =   " + cart.getQuantity());
                        double oneTypePrice = (Double.parseDouble(cart.getPrice())) * Double.parseDouble(cart.getQuantity());
                        finalPrice = finalPrice + oneTypePrice;
                        price.setText("Price : $ "+ finalPrice);
                        cartViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                CharSequence[] charSequence = {
                                        "Edit", "Remove"
                                };
                                AlertDialog.Builder builder = new AlertDialog.Builder(CartActivity.this);
                                builder.setTitle("Cart Options");
                                builder.setItems(charSequence, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        switch (which) {
                                            case 0:
                                                startActivity(new Intent(CartActivity.this, ProductDetailsActivity.class).putExtra("pid", cart.getPid()));
                                                break;
                                            case 1:
                                                cartListRef.child("User View").child(Prevalent.currentOnlineUser.getPhone())
                                                        .child("Products")
                                                        .child(cart.getPid())
                                                        .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            Toast.makeText(CartActivity.this, "Item removed successfully.", Toast.LENGTH_SHORT).show();
                                                            startActivity(new Intent(CartActivity.this, HomeActivity.class));
                                                            finish();
                                                        } else {
                                                            Toast.makeText(CartActivity.this, "" + task.getException(), Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                                break;
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
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_cart_activity, parent, false);
                        return new CartViewHolder(view);
                    }
                };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
        next.setEnabled(true);
    }

}

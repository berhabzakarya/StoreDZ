package com.berhabzakarya.storedz.Admin;

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
import android.widget.TextView;

import com.berhabzakarya.storedz.Model.AdminOrder;
import com.berhabzakarya.storedz.R;
import com.berhabzakarya.storedz.ViewHolder.OrderViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AdminAddNewOrderActivity extends AppCompatActivity {
    TextView txtOrder;
    DatabaseReference db;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_order);
        initViews();
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<AdminOrder> options = new FirebaseRecyclerOptions.Builder<AdminOrder>()
                .setQuery(db, AdminOrder.class).build();
        FirebaseRecyclerAdapter<AdminOrder, OrderViewHolder> adapter =
                new FirebaseRecyclerAdapter<AdminOrder, OrderViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull OrderViewHolder orderViewHolder, final int i, @NonNull final AdminOrder adminOrder) {
                        orderViewHolder.orderUserName.setText("Name: " + adminOrder.getName());
                        orderViewHolder.orderAddress.setText("Shipping AAddress: " + adminOrder.getAddress() + ", " + adminOrder.getCity());
                        orderViewHolder.orderTimeAddess.setText("Order at: " + adminOrder.getDate() + " " + adminOrder.getTime());
                        orderViewHolder.orderPrice.setText("Total Amount = $" + adminOrder.getTotalAmount());
                        orderViewHolder.orderPhoneNumber.setText("Phone:" + adminOrder.getPhone());
                        orderViewHolder.show_all_product.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String uid = getRef(i).getKey();
                                startActivity(new Intent(AdminAddNewOrderActivity.this, AdminUserProductActivity.class)
                                        .putExtra("UID", uid));
                            }
                        });
                        orderViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                CharSequence[] charSequence = {
                                        "Yes", "No"
                                };
                                AlertDialog.Builder builder = new AlertDialog.Builder(AdminAddNewOrderActivity.this);
                                builder.setTitle("Have you shipped this order ?");
                                builder.setItems(charSequence, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        switch (which) {
                                            case 0:
                                                String uid = getRef(0).getKey();
                                                removeOrder(uid);
                                                break;
                                            case 1:
                                                finish();
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
                    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.order_layout, parent, false);
                        return new OrderViewHolder(view);
                    }
                };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    private void removeOrder(String uid) {
        db.child(uid).removeValue();
    }

    private void initViews() {
        txtOrder = findViewById(R.id.txt_new_order);
        layoutManager = new LinearLayoutManager(this);
        recyclerView = findViewById(R.id.recycler_view_add_new_order);
        db = FirebaseDatabase.getInstance().getReference().child("Orders");
    }
}

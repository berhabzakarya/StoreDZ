package com.berhabzakarya.storedz.Admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.berhabzakarya.storedz.Interface.ItemClickListener;
import com.berhabzakarya.storedz.Model.Product;
import com.berhabzakarya.storedz.R;
import com.berhabzakarya.storedz.ViewHolder.ProductViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class AdminApproveProductActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_approve_product);
        initViews();
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Product> options =
                new FirebaseRecyclerOptions.Builder<Product>().setQuery(databaseReference.orderByChild("productState").equalTo("Not Approved"), Product.class).build();
        FirebaseRecyclerAdapter<Product, ProductViewHolder> adapter =
                new FirebaseRecyclerAdapter<Product, ProductViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull ProductViewHolder productViewHolder, int i, @NonNull Product product) {
                        String price = getString(R.string.price_text) + product.getPrice() + getString(R.string.dollar);
                        final Product itemClick = product;
                        productViewHolder.name_product.setText(product.getPname());
                        productViewHolder.description_product.setText(product.getDescription());
                        productViewHolder.price_product.setText(price);
                        productViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(AdminApproveProductActivity.this);
                                builder.setTitle("Approve Products");
                                CharSequence charSequence[] = {
                                        "Yes", "No"
                                };
                                builder.setItems(charSequence, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        switch (which) {
                                            case 0:
                                                approveThisProduct(itemClick.getPid());
                                                break;
                                            case 1:
                                                break;
                                        }
                                    }
                                });
                                builder.show();
                            }
                        });

                        Picasso.get().load(product.getImage()).into(productViewHolder.image_product);
                    }

                    @NonNull
                    @Override
                    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_items_layout, parent, false);
                        return new ProductViewHolder(view);
                    }
                };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
        adapter.notifyDataSetChanged();


    }
    private void initViews() {
        recyclerView = findViewById(R.id.recycler_view_approve_activity);
        layoutManager = new LinearLayoutManager(this);
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Products");
    }
    private void approveThisProduct(String id) {
        databaseReference.child(id).child("productState").setValue("Approved").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(AdminApproveProductActivity.this, "That item has been approved, and it is available for sale from the seller.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}

package com.berhabzakarya.storedz.Seller;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.berhabzakarya.storedz.Buyers.MainActivity;
import com.berhabzakarya.storedz.Model.Product;
import com.berhabzakarya.storedz.R;
import com.berhabzakarya.storedz.ViewHolder.ItemViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

public class HomeSellerActivity extends AppCompatActivity {
    DatabaseReference database;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    return true;
                case R.id.navigation_add:
                    startActivity(new Intent(HomeSellerActivity.this, SellerProductCategoryActivity.class));
                    return true;
                case R.id.navigation_log_out:
                    final FirebaseAuth auth = FirebaseAuth.getInstance();
                    AlertDialog.Builder builder = new AlertDialog.Builder(HomeSellerActivity.this);
                    builder.setTitle("You want to log out ?");
                    CharSequence charSequence[] = {
                            "Yes", "No"
                    };
                    builder.setItems(charSequence, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case 0:
                                    auth.signOut();
                                    startActivity(new Intent(HomeSellerActivity.this,MainActivity.class));
                                    finish();
                                    break;
                                case 1:
                                    break;
                            }
                        }
                    });
                    builder.show();

                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_seller);
        initViews();
        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
    }

    @Override
    protected void onStart() {
        super.onStart();
        String sid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseRecyclerOptions<Product> options =
                new FirebaseRecyclerOptions.Builder<Product>().setQuery(database.orderByChild("sid").equalTo(sid), Product.class).build();
        FirebaseRecyclerAdapter<Product, ItemViewHolder> adapter =
                new FirebaseRecyclerAdapter<Product, ItemViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull ItemViewHolder itemViewHolder, int i, @NonNull final Product product) {
                        itemViewHolder.name_product.setText(product.getPname());
                        itemViewHolder.description_product.setText(product.getDescription());
                        itemViewHolder.state_approved.setText(product.getState());
                        itemViewHolder.price_product.setText(product.getPrice());
                        Picasso.get().load(product.getImage()).into(itemViewHolder.image_product);
                        itemViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(HomeSellerActivity.this);
                                builder.setTitle("Do you want to delete this item");
                                CharSequence charSequence[] = {"Yes", "No"};
                                builder.setItems(charSequence, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        switch (which) {
                                            case 0:
                                                deleteProduct(product.getPid());
                                                break;
                                            case 1:
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
                    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.seller_item_view, parent, false);
                        return new ItemViewHolder(view);
                    }
                };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    private void deleteProduct(String pid) {
        database.child(pid).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(HomeSellerActivity.this, "That item has been removed successfully.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(HomeSellerActivity.this, "Error :" + task.getException(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recycler_view_home_seller);
        layoutManager = new LinearLayoutManager(this);
        database = FirebaseDatabase.getInstance().getReference().child("Products");
    }

    @Override
    public void onBackPressed() {

    }
}

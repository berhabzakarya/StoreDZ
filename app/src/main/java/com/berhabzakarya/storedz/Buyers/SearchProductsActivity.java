package com.berhabzakarya.storedz.Buyers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.berhabzakarya.storedz.Model.Product;
import com.berhabzakarya.storedz.R;
import com.berhabzakarya.storedz.ViewHolder.ProductViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

public class SearchProductsActivity extends AppCompatActivity {
    private EditText searchEditText;
    private Button search;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private String inputText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_products);
        initViews();
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputText = searchEditText.getText().toString();
                onStart();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("Products");
        FirebaseRecyclerOptions<Product> options
                = new FirebaseRecyclerOptions.Builder<Product>()
                .setQuery(db.orderByChild("pname").startAt(inputText), Product.class).build();
        FirebaseRecyclerAdapter<Product, ProductViewHolder> adapter =
                new FirebaseRecyclerAdapter<Product, ProductViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull ProductViewHolder productViewHolder, int i, @NonNull final Product product) {
                        String price = getString(R.string.price_text) + product.getPrice() + getString(R.string.dollar);
                        productViewHolder.name_product.setText(product.getPname());
                        productViewHolder.description_product.setText(product.getDescription());
                        productViewHolder.price_product.setText(price);
                        Picasso.get().load(product.getImage()).into(productViewHolder.image_product);
                        productViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                startActivity(new Intent(SearchProductsActivity.this, ProductDetailsActivity.class)
                                        .putExtra("pid", product.getPid()));
                            }
                        });
                    }

                    @NonNull
                    @Override
                    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view =
                                LayoutInflater.from(SearchProductsActivity.this).inflate(R.layout.product_items_layout, parent, false);
                        return new ProductViewHolder(view);
                    }
                };
        recyclerView.setAdapter(adapter);
        adapter.startListening();


    }
    private void initViews() {
        searchEditText = findViewById(R.id.search_edit_text);
        search = findViewById(R.id.search_btn);
        recyclerView = findViewById(R.id.recycler_view_search);
        layoutManager = new LinearLayoutManager(this);
    }
}

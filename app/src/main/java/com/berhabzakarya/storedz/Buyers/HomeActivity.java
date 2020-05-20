package com.berhabzakarya.storedz.Buyers;

import android.content.Intent;
import android.os.Bundle;

import com.berhabzakarya.storedz.Admin.AdminMaintainActivity;
import com.berhabzakarya.storedz.Model.Product;
import com.berhabzakarya.storedz.Prevalent.Prevalent;
import com.berhabzakarya.storedz.R;
import com.berhabzakarya.storedz.ViewHolder.ProductViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import android.view.LayoutInflater;
import android.view.View;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;
import io.paperdb.Paper;

import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private DatabaseReference databaseReference;
    private RecyclerView recyclerView;
    private TextView user_profile_name;
    private CircleImageView user_profile_image;
    private String type = "";
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            type = bundle.getString("ADMIN");
        }
        Paper.init(this);
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Products");
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Home");
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this, CartActivity.class));
            }
        });

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        if (type.equals("ADMIN")) {
            fab.setVisibility(View.GONE);
            navigationView.setVisibility(View.GONE);
        }
        View headerView = navigationView.getHeaderView(0);
        progressBar = findViewById(R.id.progress_bar_home_user);
        progressBar.setVisibility(View.VISIBLE);
        user_profile_name = headerView.findViewById(R.id.user_profile_name);
        user_profile_image = headerView.findViewById(R.id.user_profile_image);
        recyclerView = findViewById(R.id.recycler_view_home);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<Product> options
                = new FirebaseRecyclerOptions.Builder<Product>()
                .setQuery(databaseReference.orderByChild("productState").equalTo("Approved"), Product.class).build();
        FirebaseRecyclerAdapter<Product, ProductViewHolder> adapter =
                new FirebaseRecyclerAdapter<Product, ProductViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull ProductViewHolder productViewHolder, int i, @NonNull final Product product) {
                        String price = getString(R.string.price_text) + product.getPrice() + getString(R.string.dollar);
                        productViewHolder.name_product.setText(product.getPname());
                        productViewHolder.description_product.setText(product.getDescription());
                        productViewHolder.price_product.setText(price);
                        Picasso.get().load(product.getImage()).into(productViewHolder.image_product);
                        if (type.equals("ADMIN")) {
                            productViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    startActivity(new Intent(HomeActivity.this, AdminMaintainActivity.class)
                                            .putExtra("pid", product.getPid()));
                                }
                            });
                        } else {
                            productViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    startActivity(new Intent(HomeActivity.this, ProductDetailsActivity.class)
                                            .putExtra("pid", product.getPid()));
                                }
                            });
                        }

                    }

                    @NonNull
                    @Override
                    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view =
                                LayoutInflater.from(HomeActivity.this).inflate(R.layout.product_items_layout, parent, false);
                        return new ProductViewHolder(view);
                    }
                };
        progressBar.setVisibility(View.GONE);
        recyclerView.setAdapter(adapter);
        adapter.startListening();
        if (!type.equals("ADMIN")) {
            user_profile_name.setText(Prevalent.currentOnlineUser.getName());
            Picasso.get().load(Prevalent.currentOnlineUser.getImage()).placeholder(R.drawable.profile).into(user_profile_image);
        }


    }

    @Override
    public void onBackPressed() {
        if(!type.equals("ADMIN")) {
            DrawerLayout drawer = findViewById(R.id.drawer_layout);
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            }
        }else{
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
//        int id = item.getItemId();
//        if (id == R.id.action_settings) {
//            return true;
//        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_cart) {
            startActivity(new Intent(HomeActivity.this, CartActivity.class));
        } else if (id == R.id.nav_search) {
            startActivity(new Intent(HomeActivity.this, SearchProductsActivity.class));
        } else if (id == R.id.nav_categories) {

        } else if (id == R.id.nav_settings) {
            startActivity(new Intent(HomeActivity.this, SettingsActivity.class));
        } else if (id == R.id.nav_logout) {
            Paper.book().destroy();
            startActivity(new Intent(HomeActivity.this, MainActivity.class)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
            finish();
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}

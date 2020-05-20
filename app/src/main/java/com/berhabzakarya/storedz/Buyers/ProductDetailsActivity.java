package com.berhabzakarya.storedz.Buyers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.berhabzakarya.storedz.Model.Product;
import com.berhabzakarya.storedz.Prevalent.Prevalent;
import com.berhabzakarya.storedz.R;
import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class ProductDetailsActivity extends AppCompatActivity {
    TextView product_name, product_price, product_description;
    ElegantNumberButton number_btn;
    FloatingActionButton fab;
    ImageView product_img;
    String pid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);
        initViews();
        getProductsDetails();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addingToCartList();
            }
        });
    }

    private void addingToCartList() {
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance()
                .getReference().child("Cart List");
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        String date = currentDate.format(calendar.getTime());
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        String time = currentTime.format(calendar.getTime());
        final HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("pid", pid);
        hashMap.put("name", product_name.getText().toString());
        hashMap.put("description", product_description.getText().toString());
        hashMap.put("price", product_price.getText().toString());
        hashMap.put("time", time);
        hashMap.put("date", date);
        hashMap.put("quantity", number_btn.getNumber());
        hashMap.put("discount", "");

        databaseReference.child("User View").child(Prevalent.currentOnlineUser.getPhone()).child("Products")
                .child(pid).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    databaseReference.child("Admin View").child(Prevalent.currentOnlineUser.getPhone()).child("Products")
                            .child(pid).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(ProductDetailsActivity.this, "Successful adding product to cart.", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(ProductDetailsActivity.this, HomeActivity.class));
                                finish();
                            }
                        }
                    });
                }
            }
        });
    }

    private void getProductsDetails() {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("Products");
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Product product = dataSnapshot.child(pid).getValue(Product.class);
                    product_name.setText(product.getPname());
                    product_description.setText(product.getDescription());
                    product_price.setText(product.getPrice());
                    Picasso.get().load(product.getImage()).into(product_img);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void initViews() {
        product_img = findViewById(R.id.image_product_details);
        product_name = findViewById(R.id.name_product_details);
        product_price = findViewById(R.id.price_product_details);
        product_description = findViewById(R.id.description_product_details);
        fab = findViewById(R.id.add_product);
        number_btn = findViewById(R.id.number_btn);
        pid = getIntent().getExtras().getString("pid");

    }
}

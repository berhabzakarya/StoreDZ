package com.berhabzakarya.storedz.Seller;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.berhabzakarya.storedz.R;

public class SellerProductCategoryActivity extends AppCompatActivity {
    private ImageView tshirt, thshirt_sport, female_drasses, sweathers;
    private ImageView glasses, hats, purses, shoes;
    private ImageView headphones, laptops, watches, mobile_phone;
    static final String CATEGORY = "category";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_product_category_activity);
        initViews();
        tshirt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SellerProductCategoryActivity.this, SellerAddNewProductActivity.class)
                        .putExtra(CATEGORY, "tShirts"));
            }
        });

        thshirt_sport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SellerProductCategoryActivity.this, SellerAddNewProductActivity.class)
                        .putExtra(CATEGORY, "Sport tShirt"));
            }
        });

        female_drasses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SellerProductCategoryActivity.this, SellerAddNewProductActivity.class)
                        .putExtra(CATEGORY, "Dresses Female"));
            }
        });

        sweathers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SellerProductCategoryActivity.this, SellerAddNewProductActivity.class)
                        .putExtra(CATEGORY,"Sweather"));
            }
        });

        glasses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SellerProductCategoryActivity.this, SellerAddNewProductActivity.class)
                        .putExtra(CATEGORY, "Glasses"));
            }
        });
        hats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SellerProductCategoryActivity.this, SellerAddNewProductActivity.class)
                        .putExtra(CATEGORY, "hats"));
            }
        });

        purses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SellerProductCategoryActivity.this, SellerAddNewProductActivity.class)
                        .putExtra(CATEGORY, "Purses Bags"));
            }
        });
        shoes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SellerProductCategoryActivity.this, SellerAddNewProductActivity.class)
                        .putExtra(CATEGORY, "Shoes"));

            }
        });
        headphones.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SellerProductCategoryActivity.this, SellerAddNewProductActivity.class)
                        .putExtra(CATEGORY, "HeadPhones"));
            }
        });
        laptops.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SellerProductCategoryActivity.this, SellerAddNewProductActivity.class)
                        .putExtra(CATEGORY, "Laptops"));
            }
        });
        watches.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SellerProductCategoryActivity.this, SellerAddNewProductActivity.class)
                        .putExtra(CATEGORY, "Watches"));
            }
        });
        mobile_phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SellerProductCategoryActivity.this, SellerAddNewProductActivity.class)
                        .putExtra(CATEGORY, "Mobile Phone"));
            }
        });

    }

    private void initViews() {
        tshirt = findViewById(R.id.tshirts);
        thshirt_sport = findViewById(R.id.tshitt_sport);
        female_drasses = findViewById(R.id.female_dresses);
        sweathers = findViewById(R.id.sweather);

        glasses = findViewById(R.id.glasses);
        hats = findViewById(R.id.hats);
        purses = findViewById(R.id.purses_bags);
        shoes = findViewById(R.id.shoes);

        headphones = findViewById(R.id.headphoness);
        laptops = findViewById(R.id.laptops);
        watches = findViewById(R.id.watches);
        mobile_phone = findViewById(R.id.mobiles);


    }
}

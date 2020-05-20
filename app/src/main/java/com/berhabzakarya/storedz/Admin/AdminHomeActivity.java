package com.berhabzakarya.storedz.Admin;

import androidx.appcompat.app.AppCompatActivity;
import io.paperdb.Paper;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.berhabzakarya.storedz.Buyers.HomeActivity;
import com.berhabzakarya.storedz.Buyers.MainActivity;
import com.berhabzakarya.storedz.R;

public class AdminHomeActivity extends AppCompatActivity {
    private Button checkNewOrder, logOut, mainTainProducts,checkAndApprouveNewProduct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);
        initViews();

        checkNewOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminHomeActivity.this, AdminAddNewOrderActivity.class));
            }
        });
        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Paper.book().destroy();
                startActivity(new Intent(AdminHomeActivity.this, MainActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
                finish();
            }
        });
        mainTainProducts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminHomeActivity.this, HomeActivity.class).putExtra("ADMIN", "ADMIN"));
            }
        });
        checkAndApprouveNewProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminHomeActivity.this,AdminApproveProductActivity.class));
            }
        });

    }

    private void initViews() {
        logOut = findViewById(R.id.log_out_admin);
        checkNewOrder = findViewById(R.id.add_new_order_activity_btn);
        mainTainProducts = findViewById(R.id.main_tain_btn_admin);
        checkAndApprouveNewProduct = findViewById(R.id.add_new_approve);
    }

    @Override
    public void onBackPressed() {

    }
}

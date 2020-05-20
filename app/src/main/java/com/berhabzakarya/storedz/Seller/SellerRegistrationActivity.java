package com.berhabzakarya.storedz.Seller;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.berhabzakarya.storedz.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;



import java.util.HashMap;

public class SellerRegistrationActivity extends AppCompatActivity {
    private EditText name, phone_number, email, password, shop_bussiness_address;
    private Button link_seller, register_seller;
    private String input_name, input_phone_number, input_email, input_password, input_shop_bussiness_address;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_registration);
        initViews();
        link_seller.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SellerRegistrationActivity.this, SellerLoginActivity.class));
            }
        });
        register_seller.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                input_name = name.getText().toString();
                input_phone_number = phone_number.getText().toString();
                input_email = email.getText().toString();
                input_password = password.getText().toString();
                input_shop_bussiness_address = shop_bussiness_address.getText().toString();
                loadingBar.setTitle("Registration Seller.");
                loadingBar.setMessage("Please wait ...");
                loadingBar.setCanceledOnTouchOutside(false);
                loadingBar.show();
                createUser(input_email, input_password);


            }
        });
    }

    private void createUser(String email, String password) {
        if (TextUtils.isEmpty(input_email)
                && TextUtils.isEmpty(input_name)
                && TextUtils.isEmpty(input_password)
                && TextUtils.isEmpty(input_shop_bussiness_address)
                && TextUtils.isEmpty(input_phone_number)) {
            Toast.makeText(this, "Write all information", Toast.LENGTH_SHORT).show();
        } else {
            final FirebaseAuth auth = FirebaseAuth.getInstance();
            auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        DatabaseReference database = FirebaseDatabase.getInstance().getReference().child("Sellers");
                        String sid = auth.getCurrentUser().getUid();
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("sid", sid);
                        hashMap.put("name", input_name);
                        hashMap.put("email", input_email);
                        hashMap.put("phone", input_phone_number);
                        hashMap.put("shop", input_shop_bussiness_address);
                        database.child(sid).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                loadingBar.dismiss();
                                startActivity(new Intent(SellerRegistrationActivity.this, HomeSellerActivity.class)
                                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
                                Toast.makeText(SellerRegistrationActivity.this, "Your account has been created succssfully.", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        });

                    } else {
                        loadingBar.dismiss();
                        Toast.makeText(SellerRegistrationActivity.this, "Error : " + task.getException(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

    }

    private void initViews() {
        loadingBar = new ProgressDialog(this);
        name = findViewById(R.id.name_seller_register);
        phone_number = findViewById(R.id.phone_seller_register);
        email = findViewById(R.id.email_seller_register);
        password = findViewById(R.id.password_seller_register);
        link_seller = findViewById(R.id.link_login_seller);
        register_seller = findViewById(R.id.register_seller);
        shop_bussiness_address = findViewById(R.id.shop_bussines_address_seller_register);
    }

    @Override
    protected void onStop() {
        super.onStop();
        loadingBar.dismiss();
    }
}

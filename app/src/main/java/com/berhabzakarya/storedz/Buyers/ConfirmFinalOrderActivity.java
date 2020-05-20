package com.berhabzakarya.storedz.Buyers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.berhabzakarya.storedz.Prevalent.Prevalent;
import com.berhabzakarya.storedz.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class ConfirmFinalOrderActivity extends AppCompatActivity {
    private EditText nameEditText, phoneNumberEditText, addressEditText, cityNameEditText;
    private Button confirmShippment;
    private String total_price;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_final_order);
        initViews();
        confirmShippment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkOrder();
            }
        });
    }


    private void checkOrder() {
        if (TextUtils.isEmpty(nameEditText.getText().toString())) {
            Toast.makeText(this, "Please . Write your name .", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(phoneNumberEditText.getText().toString())) {
            Toast.makeText(this, "Please . Write your phone number .", Toast.LENGTH_SHORT).show();

        } else if (TextUtils.isEmpty(addressEditText.getText().toString())) {
            Toast.makeText(this, "Please . Write your address .", Toast.LENGTH_SHORT).show();

        } else if (TextUtils.isEmpty(cityNameEditText.getText().toString())) {
            Toast.makeText(this, "Please . Write your city name .", Toast.LENGTH_SHORT).show();

        } else {
            finishOrder();
        }


    }

    private void finishOrder() {

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        String date = currentDate.format(calendar.getTime());
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        String time = currentTime.format(calendar.getTime());


        final DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        final HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("totalAmount", total_price);
        hashMap.put("name", nameEditText.getText().toString());
        hashMap.put("phone", phoneNumberEditText.getText().toString());
        hashMap.put("address", addressEditText.getText().toString());
        hashMap.put("time", time);
        hashMap.put("date", date);
        hashMap.put("city", cityNameEditText.getText().toString());
        hashMap.put("state", "not shipped");
        db.child("Orders").child(Prevalent.currentOnlineUser.getPhone()).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    db.child("Cart List")
                            .child("User View")
                            .child(Prevalent.currentOnlineUser.getPhone()).removeValue()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(ConfirmFinalOrderActivity.this, "Your final order has been placed successful.", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(ConfirmFinalOrderActivity.this, HomeActivity.class)
                                                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
                                        finish();
                                    }
                                }
                            });
                }
            }
        });

    }


    private void initViews() {
        nameEditText = findViewById(R.id.shippment_name);
        addressEditText = findViewById(R.id.shippment_address);
        cityNameEditText = findViewById(R.id.shippment_city_name);
        phoneNumberEditText = findViewById(R.id.shippment_phone_number);
        confirmShippment = findViewById(R.id.confirm_shippment);
        total_price = getIntent().getExtras().getString("FINAL_PRICE");
    }

}

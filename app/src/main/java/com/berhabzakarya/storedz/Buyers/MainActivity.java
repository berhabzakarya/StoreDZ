package com.berhabzakarya.storedz.Buyers;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.berhabzakarya.storedz.Admin.AdminHomeActivity;
import com.berhabzakarya.storedz.Seller.SellerProductCategoryActivity;
import com.berhabzakarya.storedz.Model.Users;
import com.berhabzakarya.storedz.Prevalent.Prevalent;
import com.berhabzakarya.storedz.R;
import com.berhabzakarya.storedz.Seller.SellerRegistrationActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity {
    private Button login, join;
    private String user_phone, user_password;
    private ProgressDialog loadingBar;
    private TextView link_to_seller;
    private static boolean isConnected(){
        return false;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Paper.init(this);
        Paper.book().destroy();
        initViews();
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
            }
        });
        join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, RegisterActivity.class));
            }
        });
        link_to_seller.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,SellerRegistrationActivity.class));
            }
        });
        user_phone = Paper.book().read(Prevalent.USER_PHONE);
        user_password = Paper.book().read(Prevalent.USER_PASSWORD);
        if (user_phone != "" && user_password != "") {
            if (!TextUtils.isEmpty(user_phone) && !TextUtils.isEmpty(user_password)) {
                loadingBar.setTitle("Already Logged In ...");
                loadingBar.setMessage("Please wait , while we are checking the credantials.  ");
                loadingBar.setCanceledOnTouchOutside(false);
                loadingBar.show();
                loginAccount(user_phone, user_password);

            }
        }

    }

    private void loginAccount(final String phone, final String password) {
        final DatabaseReference databaseReference;
        databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if ((dataSnapshot.child("Users").child(phone)).exists()) {
                    Users dataUser = dataSnapshot.child("Users").child(phone).getValue(Users.class);
                    if (dataUser.getPhone().equals(phone)) {
                        if (dataUser.getPassword().equals(password)) {
                            Toast.makeText(MainActivity.this, "Please wait , You are already logged in ...", Toast.LENGTH_SHORT).show();
                            loadingBar.dismiss();
                            Prevalent.currentOnlineUser = dataUser;
                            startActivity(new Intent(MainActivity.this, HomeActivity.class));
                        } else {
                            Toast.makeText(MainActivity.this, "Password is incorrect ", Toast.LENGTH_SHORT).show();
                            loadingBar.dismiss();
                        }
                    }
                } else if (dataSnapshot.child("Admins").child(phone).exists()) {
                    Users dataUser = dataSnapshot.child("Admins").child(phone).getValue(Users.class);
                    if (dataUser.getPhone().equals(phone)) {
                        if (dataUser.getPassword().equals(password)) {
                            Toast.makeText(MainActivity.this, "Please wait , You are already logged in ...", Toast.LENGTH_SHORT).show();
                            loadingBar.dismiss();
                            Prevalent.currentOnlineUser = dataUser;
                            startActivity(new Intent(MainActivity.this, AdminHomeActivity.class));
                        } else {
                            Toast.makeText(MainActivity.this, "Password is incorrect ", Toast.LENGTH_SHORT).show();
                            loadingBar.dismiss();
                        }
                    }
                } else {
                    loadingBar.dismiss();
                    Toast.makeText(MainActivity.this, "Account with phone number : " + phone + " don't exists", Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void initViews() {
        login = findViewById(R.id.main_button_login);
        join = findViewById(R.id.main_button_join_now);
        link_to_seller = findViewById(R.id.link_to_seller);
        loadingBar = new ProgressDialog(this);
        Paper.init(this);

    }
}

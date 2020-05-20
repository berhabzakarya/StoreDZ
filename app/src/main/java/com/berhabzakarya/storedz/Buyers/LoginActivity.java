package com.berhabzakarya.storedz.Buyers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import io.paperdb.Paper;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.berhabzakarya.storedz.Admin.AdminHomeActivity;
import com.berhabzakarya.storedz.Seller.SellerProductCategoryActivity;
import com.berhabzakarya.storedz.Model.Users;
import com.berhabzakarya.storedz.Prevalent.Prevalent;
import com.berhabzakarya.storedz.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rey.material.widget.CheckBox;

public class LoginActivity extends AppCompatActivity {
    CheckBox checkBox;
    ProgressDialog loadingBar;
    EditText edit_phone, edit_password;
    Button login;
    TextView link_admin_panel, link_not_admin_panel, reset_password_activity;
    private String databaseRoot = "Users";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initViews();
        Paper.init(this);
        Paper.book().destroy();
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
        link_admin_panel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login.setText(R.string.login_admin);
                link_admin_panel.setVisibility(View.INVISIBLE);
                link_not_admin_panel.setVisibility(View.VISIBLE);
                databaseRoot = "Admins";
            }
        });
        link_not_admin_panel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login.setText(R.string.login_admin);
                link_admin_panel.setVisibility(View.VISIBLE);
                link_not_admin_panel.setVisibility(View.INVISIBLE);
                databaseRoot = "Users";
            }
        });
        reset_password_activity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, ResetPasswordActivity.class)
                        .putExtra("check", "login"));
            }
        });
    }

    private void login() {
        String phone = edit_phone.getText().toString();
        String password = edit_password.getText().toString();
        loadingBar.setTitle("Login Account");
        loadingBar.setMessage("Please wait , while we are checking the credentials.  ");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();
        loginAccount(phone, password);
    }

    private void loginAccount(final String phone, final String password) {
        if (checkBox.isChecked()) {
            Paper.book().write(Prevalent.USER_PHONE, phone);
            Paper.book().write(Prevalent.USER_PASSWORD, password);
        }
        final DatabaseReference databaseReference;
        databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if ((dataSnapshot.child(databaseRoot).child(phone)).exists()) {
                    Users dataUser = dataSnapshot.child(databaseRoot).child(phone).getValue(Users.class);
                    if (dataUser.getPhone().equals(phone)) {
                        if (dataUser.getPassword().equals(password)) {
                            if (databaseRoot.equals("Admins")) {
                                Toast.makeText(LoginActivity.this, "Successfully logged ", Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                                startActivity(new Intent(LoginActivity.this, AdminHomeActivity.class));
                                finish();
                            } else if (databaseRoot.equals("Users")) {
                                Toast.makeText(LoginActivity.this, "Successfully logged ", Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                                Prevalent.currentOnlineUser = dataUser;
                                startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                                finish();
                            }
                        } else {
                            Toast.makeText(LoginActivity.this, "Password is incorrect ", Toast.LENGTH_SHORT).show();
                            loadingBar.dismiss();
                        }
                    }

                } else {
                    loadingBar.dismiss();
                    Toast.makeText(LoginActivity.this, "Account with phone number : " + phone + " don't exists", Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void initViews() {
        edit_phone = findViewById(R.id.login_input_phone);
        edit_password = findViewById(R.id.login_input_password);
        login = findViewById(R.id.login_btn);
        link_admin_panel = findViewById(R.id.link_i_am_admin_panel);
        link_not_admin_panel = findViewById(R.id.link_not_admin_panel);
        reset_password_activity = findViewById(R.id.forget_password_link);
        loadingBar = new ProgressDialog(this);
        checkBox = findViewById(R.id.remember_me_checkbox);
    }
}

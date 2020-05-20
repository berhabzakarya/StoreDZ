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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SellerLoginActivity extends AppCompatActivity {
    private EditText email, password;
    private Button login;
    private String input_email, input_password;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_login);
        initViews();
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                input_email = email.getText().toString();
                input_password = password.getText().toString();
                if(TextUtils.isEmpty(input_email)){
                    Toast.makeText(SellerLoginActivity.this, "Please write your email.", Toast.LENGTH_SHORT).show();
                }else if(TextUtils.isEmpty(input_password)){
                    Toast.makeText(SellerLoginActivity.this, "Please write your password ", Toast.LENGTH_SHORT).show();
                }else if(input_password.length()<6){
                    Toast.makeText(SellerLoginActivity.this, "Your password is court ", Toast.LENGTH_SHORT).show();
                }else {
                    loadingBar.setTitle("Login Seller.");
                    loadingBar.setMessage("Please wait ...");
                    loadingBar.setCanceledOnTouchOutside(false);
                    loadingBar.show();
                    loginUser(input_email, input_password);
                }
            }
        });
    }

    private void loginUser(String email, String password) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    loadingBar.dismiss();
                    startActivity(new Intent(SellerLoginActivity.this, HomeSellerActivity.class)
                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
                    finish();
                } else {
                    loadingBar.dismiss();
                    Toast.makeText(SellerLoginActivity.this, "Error " + task.getException().toString(), Toast.LENGTH_SHORT).show();
                }
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        loadingBar.dismiss();
                        Toast.makeText(SellerLoginActivity.this, "Error : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void initViews() {
        email = findViewById(R.id.email_seller_login);
        password = findViewById(R.id.password_seller_login);
        login = findViewById(R.id.login_seller);
        loadingBar = new ProgressDialog(this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onStop() {
        super.onStop();
        loadingBar.dismiss();
    }
}

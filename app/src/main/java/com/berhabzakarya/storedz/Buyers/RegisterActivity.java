package com.berhabzakarya.storedz.Buyers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import io.paperdb.Paper;

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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {
    EditText edit_user_name, edit_password, edit_phone_number;
    Button create_account;
    ProgressDialog loadingBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Paper.init(this);
        Paper.book().destroy();
        initViews();
        create_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAccount();
            }
        });
    }

    private void createAccount() {
        String name = edit_user_name.getText().toString();
        String phone = edit_phone_number.getText().toString();
        String password = edit_password.getText().toString();

        if(TextUtils.isEmpty(phone)){
            Toast.makeText(this, "Please write your phone number ...", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(name)){
            Toast.makeText(this, "Please write your name ...", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(password)){
            Toast.makeText(this, "Please write your password ...", Toast.LENGTH_SHORT).show();
        }
        else{
            loadingBar.setTitle("Create Account ");
            loadingBar.setMessage("Please wait , while we are checking the credantials.  ");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();
            validate(name,phone,password);

        }
    }

    private void validate(final String name, final String phone, final String password) {
        final DatabaseReference databaseReference;
        databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!(dataSnapshot.child("Users").child(phone).exists())){
                    HashMap<String , Object>hashMap = new HashMap<>();
                    hashMap.put("name",name);
                    hashMap.put("phone",phone);
                    hashMap.put("password",password);
                    databaseReference.child("Users").child(phone).setValue(hashMap)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(RegisterActivity.this, "Congratulation , Your Account is successful created.", Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                                startActivity(new Intent(RegisterActivity.this,LoginActivity.class));
                                finish();
                            }else{
                                Toast.makeText(RegisterActivity.this, "Network Error", Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                                startActivity(new Intent(RegisterActivity.this,MainActivity.class));
                                finish();
                            }
                        }
                    });
                }else{
                    Toast.makeText(RegisterActivity.this, "This phone number already exists.", Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                    Toast.makeText(RegisterActivity.this, "Try another phone number", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void initViews() {
        edit_user_name = findViewById(R.id.register_input_user_name);
        edit_phone_number = findViewById(R.id.register_input_phone);
        edit_password = findViewById(R.id.register_input_password);
        create_account = findViewById(R.id.register_btn);
        loadingBar = new ProgressDialog(this);
    }

}

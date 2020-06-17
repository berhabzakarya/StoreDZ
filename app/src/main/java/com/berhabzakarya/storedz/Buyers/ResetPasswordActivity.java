package com.berhabzakarya.storedz.Buyers;

import android.app.ProgressDialog;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class ResetPasswordActivity extends AppCompatActivity {
    private EditText editTextPhoneNumber, editTextAnswer1, editTextAnswer2, editTextNewPassword;
    private String textPhoneNumber, textAnswer1, textAnswer2;
    private Button verify, update;
    private DatabaseReference databaseReference;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        initViews();
        verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textAnswer1 = editTextAnswer1.getText().toString();
                textAnswer2 = editTextAnswer2.getText().toString();
                textPhoneNumber = editTextPhoneNumber.getText().toString();

                if (TextUtils.isEmpty(textPhoneNumber)) {
                    Toast.makeText(ResetPasswordActivity.this, "Please write phone number.", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(textAnswer1)) {
                    Toast.makeText(ResetPasswordActivity.this, "Please write answer 1.", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(textAnswer2)) {
                    Toast.makeText(ResetPasswordActivity.this, "Please write answer 2.", Toast.LENGTH_SHORT).show();
                } else {
                    checkPhone(textPhoneNumber);
                }

            }
        });
    }
    private void checkPhone(final String phone){
        databaseReference.child(phone).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    resetPassword(phone);
                }else {
                    Toast.makeText(ResetPasswordActivity.this, phone +" does'n  exist", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ResetPasswordActivity.this, "Error : "+databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }
    private void resetPassword(String phone) {
        databaseReference.child(phone).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String mAnswer1 = dataSnapshot.child("SQ").child("answer1").getValue(String.class);
                    String mAnswer2 = dataSnapshot.child("SQ").child("answer2").getValue(String.class);
                    if (textAnswer1.equals(mAnswer1)) {
                        if (textAnswer2.equals(mAnswer2)) {
                            setContentView(R.layout.custom_view_new_password);
                            update = findViewById(R.id.set_new_password_btn);
                            editTextNewPassword = findViewById(R.id.edit_text_new_password);
                            update.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    progressDialog.show();
                                    setNewPassword();
                                }
                            });
                        } else {
                            Toast.makeText(ResetPasswordActivity.this, "Answers is incorrect", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(ResetPasswordActivity.this, "Answers is incorrect", Toast.LENGTH_SHORT).show();
                    }


                } else {
                    Toast.makeText(ResetPasswordActivity.this, "This phone number : " + textPhoneNumber + " \n Not exist !, try again ", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setNewPassword() {

        if (!TextUtils.isEmpty(editTextNewPassword.getText().toString()) && editTextNewPassword.getText().toString().length() > 6) {
            String password = editTextNewPassword.getText().toString();
            databaseReference.child(textPhoneNumber).child("password").setValue(password).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(ResetPasswordActivity.this, "Error : " + e.getMessage(), Toast.LENGTH_SHORT).show();

                }
            }).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        progressDialog.dismiss();
                        Toast.makeText(ResetPasswordActivity.this, "Success", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(ResetPasswordActivity.this, "Error : " + task.getException(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            Toast.makeText(ResetPasswordActivity.this, "Please write new password and >> 6 letter", Toast.LENGTH_SHORT).show();
        }
    }

    private void initViews() {
        editTextPhoneNumber = findViewById(R.id.find_phone_number);
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");
        editTextAnswer1 = findViewById(R.id.question_1);
        editTextAnswer2 = findViewById(R.id.question_2);
        verify = findViewById(R.id.verify_btn);
        progressDialog = new ProgressDialog(this);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setTitle("Updating password ... ");
        progressDialog.setMessage("Please wait , while we are updating ...");
    }


}

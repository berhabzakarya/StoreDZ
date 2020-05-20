package com.berhabzakarya.storedz.Buyers;

import android.app.ProgressDialog;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class SetQuestionActivity extends AppCompatActivity {

    private ProgressDialog progressDialog;
    private DatabaseReference databaseReference;
    private EditText edit_text_answer1_setting, edit_text_answer2_setting;
    private String textAnswer1, textAnswer2;
    private Button set;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_question);
        initViews();

        set.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textAnswer1 = edit_text_answer1_setting.getText().toString();
                textAnswer2 = edit_text_answer2_setting.getText().toString();
                if (TextUtils.isEmpty(textAnswer1)) {
                    Toast.makeText(SetQuestionActivity.this, "Please , Complete the form.", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(textAnswer2)) {
                    Toast.makeText(SetQuestionActivity.this, "Please , Complete the form.", Toast.LENGTH_SHORT).show();

                } else {
                    setSecurity();
                }
            }
        });

    }

    private void setSecurity() {
        progressDialog.setTitle("Setting answer");
        progressDialog.setMessage("Please wait , while we are updating ...");
        progressDialog.show();
        HashMap<String, Object> security = new HashMap<>();
        security.put("answer1", textAnswer1);
        security.put("answer2", textAnswer2);
        databaseReference.child(Prevalent.currentOnlineUser.getPhone()).child("SQ").updateChildren(security).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    progressDialog.dismiss();
                    Toast.makeText(SetQuestionActivity.this, "Success.", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(SetQuestionActivity.this, HomeActivity.class));
                    finish();
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(SetQuestionActivity.this, "Error : " + task.getException(), Toast.LENGTH_SHORT).show();

                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(SetQuestionActivity.this, "Error : " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void initViews() {
        edit_text_answer1_setting = findViewById(R.id.edit_text_answer1_setting);
        edit_text_answer2_setting = findViewById(R.id.edit_text_answer2_setting);
        set = findViewById(R.id.set_settings_answer);
        progressDialog = new ProgressDialog(this);
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(Prevalent.currentOnlineUser.getPhone());
    }

}

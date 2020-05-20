package com.berhabzakarya.storedz.Buyers;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import de.hdodenhof.circleimageview.CircleImageView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.berhabzakarya.storedz.Prevalent.Prevalent;
import com.berhabzakarya.storedz.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.HashMap;

public class SettingsActivity extends AppCompatActivity {
    private EditText edit_name, edit_phone, edit_address, edit_password;
    private CircleImageView circleImageView;
    private String checker = "";
    private StorageReference ref;
    private ProgressDialog progressDialog;
    TextView update_btn, close_btn, update_image;
    Toolbar toolbar;
    private Uri uriImage;
    private String image;
    private String downloadFileUrl;
    private Button setQuestionSecurity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        initViews();
        setSupportActionBar(toolbar);
        userInfoDisplay(circleImageView, edit_name, edit_phone, edit_address, edit_password);

        close_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        update_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checker.equals("clicked")) {
                    updateInfoSaved();
                } else {
                    updateOnlyInfo();
                }
            }
        });

        update_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checker = "clicked";
                CropImage.activity(uriImage)
                        .setAspectRatio(1, 1).start(SettingsActivity.this);
            }
        });

        setQuestionSecurity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingsActivity.this, SetQuestionActivity.class));
            }
        });

    }

    private void updateOnlyInfo() {
        HashMap<String, Object> user = new HashMap<>();
        user.put("name", edit_name.getText().toString());
        user.put("phone", edit_phone.getText().toString());
        user.put("password", edit_password.getText().toString());
        user.put("address", edit_address.getText().toString());
        user.put("image", downloadFileUrl);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");
        databaseReference.child(Prevalent.currentOnlineUser.getPhone()).updateChildren(user);
        finish();
    }

    private void updateInfoSaved() {
        if (TextUtils.isEmpty(edit_name.getText().toString()) && TextUtils.isEmpty(edit_address.getText().toString()) && TextUtils.isEmpty(edit_password.getText().toString()) && TextUtils.isEmpty(edit_phone.getText().toString()))
            Toast.makeText(this, "Write all information", Toast.LENGTH_SHORT).show();
        else if (checker.equals("clicked")) {
            uploadImage();
        }
    }

    private void uploadImage() {
        progressDialog.setTitle("Update Profile");
        progressDialog.setMessage("Please wait , while we are updating your account information...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        if (uriImage != null) {
            final StorageReference storageReference = ref.child(uriImage.getLastPathSegment());
            UploadTask uploadTask = storageReference.putFile(uriImage);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    String exception = e.getMessage();
                    Toast.makeText(SettingsActivity.this, "Error : " + exception, Toast.LENGTH_SHORT).show();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                }
            });
            Task<Uri> task = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    } else {
                        downloadFileUrl = storageReference.getDownloadUrl().toString();
                    }
                    return storageReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    downloadFileUrl = task.getResult().toString();
                    Toast.makeText(SettingsActivity.this, "Profile Updated  ...", Toast.LENGTH_SHORT).show();
                    saveToDatabase();
                    finish();
                }
            });
        }
    }

    private void saveToDatabase() {
        HashMap<String, Object> user = new HashMap<>();
        user.put("name", edit_name.getText().toString());
        user.put("phone", edit_phone.getText().toString());
        user.put("password", edit_password.getText().toString());
        user.put("address", edit_address.getText().toString());
        user.put("image", downloadFileUrl);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");
        databaseReference.child(Prevalent.currentOnlineUser.getPhone()).updateChildren(user);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            uriImage = result.getUri();
            circleImageView.setImageURI(uriImage);
        } else {
            Toast.makeText(this, "Error , Try again", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void initViews() {
        progressDialog = new ProgressDialog(this);
        ref = FirebaseStorage.getInstance().getReference().child("Pictures");
        toolbar = findViewById(R.id.toolbar_settings);
        edit_name = findViewById(R.id.name_profile_settings_edit);
        edit_phone = findViewById(R.id.phone_profile_settings_edit);
        edit_address = findViewById(R.id.address_profile_settings_edit);
        edit_password = findViewById(R.id.password_profile_settings_edit);
        update_btn = findViewById(R.id.update_settings);
        update_image = findViewById(R.id.update_image);
        close_btn = findViewById(R.id.close_settings);
        circleImageView = findViewById(R.id.profile_settings_image);
        toolbar = findViewById(R.id.toolbar_settings);
        setQuestionSecurity = findViewById(R.id.security_question_edit);
    }

    private void userInfoDisplay(final CircleImageView img, final EditText name, final EditText phone, final EditText address, final EditText password) {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("Users").child(Prevalent.currentOnlineUser.getPhone());
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String nameText = dataSnapshot.child("name").getValue(String.class);
                    String phoneText = dataSnapshot.child("phone").getValue(String.class);
                    String addressText = dataSnapshot.child("address").getValue(String.class);
                    String passwordText = dataSnapshot.child("password").getValue(String.class);
                    name.setText(nameText);
                    phone.setText(phoneText);
                    address.setText(addressText);
                    password.setText(passwordText);
                    if (dataSnapshot.child("image").exists()) {
                        image = dataSnapshot.child("image").getValue().toString();
                        Picasso.get().load(image).into(img);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onBackPressed() {

    }
}

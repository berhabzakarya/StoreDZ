package com.berhabzakarya.storedz.Seller;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.berhabzakarya.storedz.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class SellerAddNewProductActivity extends AppCompatActivity {
    private static final int GALLERY_PICK_UP = 1;
    private String name, description, price;
    private Button addNewProductButton;
    private EditText name_product, description_product, price_product;
    private ImageView inputProductImage;
    private Uri image_uri;
    private String saveCurrentData;
    private String saveCurrentTime;
    private String productRandomKey;
    private StorageReference ProductRef;
    private DatabaseReference ProductDat;
    private ProgressDialog progressDialog;
    private String downloadFileUrl;
    private String category;
    private String sName, sAddress, sPhone, sEmail, sID = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_new_product);
        initViews();
        inputProductImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, GALLERY_PICK_UP);
            }
        });
        addNewProductButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = name_product.getText().toString();
                description = description_product.getText().toString();
                price = price_product.getText().toString();
                if (TextUtils.isEmpty(name)) {
                    Toast.makeText(SellerAddNewProductActivity.this, "Please write product name", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(description)) {
                    Toast.makeText(SellerAddNewProductActivity.this, "Please write product description", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(price)) {
                    Toast.makeText(SellerAddNewProductActivity.this, "Please write product price", Toast.LENGTH_SHORT).show();
                } else if (image_uri == null) {
                    Toast.makeText(SellerAddNewProductActivity.this, "Please select product image", Toast.LENGTH_SHORT).show();
                } else {
                    progressDialog.setTitle("Adding The Product");
                    progressDialog.setMessage("Dear Seller , Please wait wile we are adding the new product ");
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show();
                    storeImageProduction();
                }

            }
        });

    }

    private void storeImageProduction() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentData = currentDate.format(calendar.getTime());
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime = currentTime.format(calendar.getTime());
        productRandomKey = saveCurrentData + saveCurrentTime;
        final StorageReference storageReference = ProductRef.child(image_uri.getLastPathSegment() + productRandomKey);
        UploadTask uploadTask = storageReference.putFile(image_uri);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                String exception = e.getMessage();
                Toast.makeText(SellerAddNewProductActivity.this, "Error : " + exception, Toast.LENGTH_SHORT).show();
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
                Toast.makeText(SellerAddNewProductActivity.this, "Image product added successful in storage", Toast.LENGTH_SHORT).show();
                saveToDatabase();
                finish();
            }
        });

    }

    private void saveToDatabase() {

        FirebaseAuth auth = FirebaseAuth.getInstance();
        sID = auth.getUid();
        FirebaseDatabase.getInstance().getReference().child("Sellers")
                .child(sID)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            sAddress = dataSnapshot.child("shop").getValue(String.class);
                            sEmail = dataSnapshot.child("email").getValue(String.class);
                            sName = dataSnapshot.child("name").getValue(String.class);
                            sPhone = dataSnapshot.child("phone").getValue(String.class);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("pid", productRandomKey);
        hashMap.put("date", saveCurrentData);
        hashMap.put("time", saveCurrentTime);
        hashMap.put("description", description);
        hashMap.put("image", downloadFileUrl);
        hashMap.put("category", category);
        hashMap.put("price", price);
        hashMap.put("pname", name);
        hashMap.put("sellerName", sName);
        hashMap.put("sellerAddress", sAddress);
        hashMap.put("sellerPhone", sPhone);
        hashMap.put("sellerEmail", sEmail);
        hashMap.put("sid", sID);
        hashMap.put("productState", "Not Approved");
        ProductDat.child(productRandomKey).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(SellerAddNewProductActivity.this, "Product Added Successfully", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(SellerAddNewProductActivity.this, "Error : " + task.getException().toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_PICK_UP || resultCode == RESULT_OK && data != null) {
            if (data.getData() != null) {
                image_uri = data.getData();
                inputProductImage.setImageURI(image_uri);
            } else {
                finish();
                Toast.makeText(this, "You don't selected any image", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void initViews() {
        ProductRef = FirebaseStorage.getInstance().getReference().child("ProductImages");
        ProductDat = FirebaseDatabase.getInstance().getReference().child("Products");
        category = getIntent().getExtras().get("category").toString();
        addNewProductButton = findViewById(R.id.add_new_product_btn);
        name_product = findViewById(R.id.name_product);
        description_product = findViewById(R.id.description_product);
        price_product = findViewById(R.id.price_product);
        inputProductImage = findViewById(R.id.select_product_image);
        progressDialog = new ProgressDialog(this);
    }
}

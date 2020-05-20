package com.berhabzakarya.storedz.Admin;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.berhabzakarya.storedz.Model.Product;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class AdminMaintainActivity extends AppCompatActivity {
    private static final int GALLERY_PICK_UP = 1;
    private EditText product_name, product_price, product_description;
    private ImageView image;
    private Button apply_changes;
    private String pid;
    private Uri image_uri = null;
    private String imageOriginal;
    private ProgressDialog progressDialog;
    private String saveCurrentData;
    private String saveCurrentTime;
    private String downloadFileUrl;
    private StorageReference ProductRef;
    private DatabaseReference ProductDat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_maintain);
        initViews();

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CharSequence[] charSequences = {
                        "Yes", "No"
                };
                AlertDialog.Builder builder = new AlertDialog.Builder(AdminMaintainActivity.this);
                builder.setTitle("Are you would to edit product image");
                builder.setItems(charSequences, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                Intent intent = new Intent();
                                intent.setAction(Intent.ACTION_GET_CONTENT);
                                intent.setType("image/*");
                                startActivityForResult(intent, GALLERY_PICK_UP);
                                break;
                            case 1:

                                break;
                        }
                    }
                });
                builder.show();
            }
        });
        apply_changes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (TextUtils.isEmpty(product_name.getText().toString()) && product_name.getText().toString().equals("")) {
                    Toast.makeText(AdminMaintainActivity.this, "Please write product name", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(product_description.getText().toString()) && product_description.getText().toString().equals("")) {
                    Toast.makeText(AdminMaintainActivity.this, "Please write product description", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(product_price.getText().toString()) && product_price.getText().toString().equals("")) {
                    Toast.makeText(AdminMaintainActivity.this, "Please write product price", Toast.LENGTH_SHORT).show();
                } else if (image_uri == null) {
                    Toast.makeText(AdminMaintainActivity.this, "Please select product image", Toast.LENGTH_SHORT).show();
                } else {
                    progressDialog.setTitle("Adding The Product");
                    progressDialog.setMessage("While we are adding the new product ");
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
        final StorageReference storageReference = ProductRef.child(image_uri.getLastPathSegment() + pid);
        UploadTask uploadTask = storageReference.putFile(image_uri);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                String exception = e.getMessage();
                Toast.makeText(AdminMaintainActivity.this, "Error : " + exception, Toast.LENGTH_SHORT).show();
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
                Toast.makeText(AdminMaintainActivity.this, "Image product added successful in storage", Toast.LENGTH_SHORT).show();
                saveToDatabase();
                finish();
            }
        });
    }

    private void saveToDatabase() {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("pid", pid);
        hashMap.put("date", saveCurrentData);
        hashMap.put("time", saveCurrentTime);
        hashMap.put("description", product_description.getText().toString());
        hashMap.put("image", downloadFileUrl);
        hashMap.put("price", product_price.getText().toString());
        hashMap.put("pname", product_name.getText().toString());
        ProductDat.child(pid).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(AdminMaintainActivity.this, "Product Updated Successfuly", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(AdminMaintainActivity.this, "Error : " + task.getException().toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_PICK_UP) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    if (data.getData() != null) {
                        image_uri = data.getData();
                        image.setImageURI(image_uri);
                    } else {
                        image.setImageURI(Uri.parse(imageOriginal));
                    }
                } else {
                    finish();
                    Toast.makeText(this, "You don't selected any image", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "You don't selected any image", Toast.LENGTH_SHORT).show();
            }
        } else {
            finish();
            Toast.makeText(this, "You don't selected any image", Toast.LENGTH_SHORT).show();
        }
    }


    private void initViews() {
        product_description = findViewById(R.id.description_edit_product);
        product_name = findViewById(R.id.name_edit_product);
        product_price = findViewById(R.id.price_edit_product);
        image = findViewById(R.id.img_edit_product);
        apply_changes = findViewById(R.id.apply_changes);
        progressDialog = new ProgressDialog(this);
        pid = getIntent().getExtras().getString("pid");
        ProductRef = FirebaseStorage.getInstance().getReference().child("ProductImages");
        ProductDat = FirebaseDatabase.getInstance().getReference().child("Products");
    }

    @Override
    protected void onStart() {
        super.onStart();
        getProductsDetails();
    }

    private void getProductsDetails() {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("Products");
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Product product = dataSnapshot.child(pid).getValue(Product.class);
                    product_name.setText(product.getPname());
                    product_description.setText(product.getDescription());
                    product_price.setText(product.getPrice());
                    if (image_uri == null) {
                        imageOriginal = product.getImage();
                        Picasso.get().load(imageOriginal).into(image);
                    } else {
                        Picasso.get().load(image_uri).into(image);
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
        startActivity(new Intent(AdminMaintainActivity.this, AdminHomeActivity.class));
        finish();
    }
}

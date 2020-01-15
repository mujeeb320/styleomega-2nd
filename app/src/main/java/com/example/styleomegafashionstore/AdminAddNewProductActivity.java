package com.example.styleomegafashionstore;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class AdminAddNewProductActivity extends AppCompatActivity {

    private String CategoryName, Description, Price, Pname, saveCurrentDate, saveCurrentTime;
    private Button AddNewProductButton;
    private EditText ProductName, ProductDescription, ProductPrice;
    private ImageView ProductImage;
    private static final int GalleryPic=1;
    private Uri ImageUri;
    private String productID, downloadImageURL;
    private StorageReference ProductImagesRef;
    private DatabaseReference productRef;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        CategoryName=getIntent().getExtras().get("category").toString();
        ProductImagesRef= FirebaseStorage.getInstance().getReference().child("Product Images");
        productRef= FirebaseDatabase.getInstance().getReference().child("Products");


        AddNewProductButton=(Button) findViewById(R.id.add_product_new);
        ProductImage=(ImageView) findViewById(R.id.product_select_image);
        ProductName=(EditText) findViewById(R.id.product_name);
        ProductDescription=(EditText) findViewById(R.id.product_description);
        ProductPrice=(EditText) findViewById(R.id.product_price);
        loadingBar = new ProgressDialog(this);

        ProductImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenGallery();
            }
        });

        AddNewProductButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ValidateProductData();
            }
        });
 }

    private void ValidateProductData() {

        Description=ProductDescription.getText().toString();
        Price=ProductPrice.getText().toString();
        Pname=ProductName.getText().toString();

        if(ImageUri==null){
            Toast.makeText(this, "Please Upload Product Image", Toast.LENGTH_SHORT).show();
        }else if(TextUtils.isEmpty(Description)){
            Toast.makeText(this, "Please Input Product Description", Toast.LENGTH_SHORT).show();
        }else if(TextUtils.isEmpty(Price)) {
            Toast.makeText(this, "Please include Product Price", Toast.LENGTH_SHORT).show();
        }else if(TextUtils.isEmpty(Pname)){
        Toast.makeText(this, "Please Input Product Name", Toast.LENGTH_SHORT).show();

        }
        else
        {
            StoreProductInfo();

        }

    }

    private void StoreProductInfo() {

        loadingBar.setMessage("Please Hold, we are adding the product");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.setTitle("Adding Product");
        loadingBar.show();

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd,yyyy");
        saveCurrentDate= currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime= currentTime.format(calendar.getTime());

        productID=saveCurrentDate +saveCurrentTime;

        final StorageReference filepath= ProductImagesRef.child(ImageUri.getLastPathSegment()+ productID+ ".jpg");

        final UploadTask uploadTask =filepath.putFile(ImageUri);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception ex) {
                String message= ex.toString();
                Toast.makeText(AdminAddNewProductActivity.this, "Error: "+message, Toast.LENGTH_SHORT).show();
                loadingBar.dismiss();

            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
            {
                Toast.makeText(AdminAddNewProductActivity.this, "Image is Uploaded ", Toast.LENGTH_SHORT).show();

                Task<Uri> URLTASK = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception
                    {
                        if(!task.isSuccessful())
                        {
                            throw task.getException();

                        }
                        downloadImageURL=filepath.getDownloadUrl().toString();
                        return  filepath.getDownloadUrl();

                    }

                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {

                        if(task.isSuccessful()){
                            downloadImageURL= task.getResult().toString();

                            Toast.makeText(AdminAddNewProductActivity.this, "URL image saved", Toast.LENGTH_SHORT).show();

                            saveProductInfoToDatabase();
                        }
                    }
                });
            }
        });





    }

    private void saveProductInfoToDatabase() {

        HashMap<String,Object> productMap = new HashMap<>();
        productMap.put("pid", productID);
        productMap.put("pcategory", CategoryName);
        productMap.put("pname", Pname);
        productMap.put("pdescription", Description);
        productMap.put("pprice", Pname);
        productMap.put("pimage", downloadImageURL);
        productMap.put("time", saveCurrentTime);
        productMap.put("date", saveCurrentDate);

        productRef.child(productID).updateChildren(productMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {
            if(task.isSuccessful())
            {
                Intent intent = new Intent(AdminAddNewProductActivity.this,AdminCatergoryActivity.class);
                startActivity(intent);

                loadingBar.dismiss();
                Toast.makeText(AdminAddNewProductActivity.this, "Product Added Successfully", Toast.LENGTH_SHORT).show();
            }
            else
                {
                    loadingBar.dismiss();
                    String message= task.getException().toString();
                    Toast.makeText(AdminAddNewProductActivity.this, "Error"+message, Toast.LENGTH_SHORT).show();


                }
            }
        });
    }

    private void OpenGallery() {
        Intent galleryIntent= new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, GalleryPic);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==GalleryPic && resultCode==RESULT_OK && data!= null)
        {
            ImageUri=data.getData();
            ProductImage.setImageURI(ImageUri);
        }

    }
}

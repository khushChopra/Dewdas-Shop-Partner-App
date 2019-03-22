package com.dewdastech.dewdasshoppartner;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class StoreItemForm extends AppCompatActivity {

    // CONSTANTS
    protected int RESULT_LOAD_IMAGE = 4173;

    // Views
    protected EditText itemNameEditText;
    protected EditText itemBrandNameEditText;
    protected EditText itemDescriptionEditText;
    protected EditText itemPriceEditText;
    protected EditText itemMRPEditText;
    protected EditText itemStockEditText;
    protected Button itemPhotoButton;
    protected Button itemSubmitButton;
    protected ImageView itemImageView;

    // Firebase variables
    protected FirebaseAuth myFirebaseAuth;
    protected StorageReference myMainStorageReference;
    protected StorageReference myStorageReference;
    protected DatabaseReference myDatabaseReference;

    // other variables
    protected Uri selectedImage;
    protected String downloadURL  = "";
    protected String storeID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_item_form);

        // views init
        viewsinit();

        // firebase init
        myMainStorageReference = FirebaseStorage.getInstance().getReference();
        myFirebaseAuth = FirebaseAuth.getInstance();
        myDatabaseReference = FirebaseDatabase.getInstance().getReference();

        // other variables init
        storeID = myFirebaseAuth.getCurrentUser().getUid();
    }

    public void itemDetailsSubmit(View v){
        final ProgressDialog progress = new ProgressDialog(this);
        progress.setTitle("Loading");
        progress.setMessage("Wait while loading...");
        progress.setCancelable(false);
        progress.show();

        File file = new File(selectedImage.getPath());
        String imageFileName = file.getName();
        myStorageReference = myMainStorageReference.child("shopImages/"+storeID+"/items/"+imageFileName);

        myStorageReference.putFile(selectedImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                myStorageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        downloadURL = uri.toString();

                        myDatabaseReference = myDatabaseReference.child("/storeByID/" + myFirebaseAuth.getCurrentUser().getUid()+"/items");

                        String key = myDatabaseReference.push().getKey();

                        StoreItem newStoreItem = new StoreItem(
                                key,
                                itemBrandNameEditText.getText().toString(),
                                itemNameEditText.getText().toString(),
                                itemDescriptionEditText.getText().toString(),
                                downloadURL,
                                Float.parseFloat(itemMRPEditText.getText().toString()),
                                Float.parseFloat(itemPriceEditText.getText().toString()),
                                Integer.parseInt(itemStockEditText.getText().toString())
                        );

                        myDatabaseReference.child(key).setValue(newStoreItem).addOnSuccessListener(
                                new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        progress.dismiss();
                                        Toast.makeText(StoreItemForm.this, "Success", Toast.LENGTH_SHORT).show();
                                        Intent i = new Intent(getApplicationContext(),MainActivity.class);
                                        startActivity(i);
                                    }
                                }
                        ).addOnFailureListener(
                                new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        progress.dismiss();
                                        Toast.makeText(StoreItemForm.this, "Failure", Toast.LENGTH_SHORT).show();
                                    }
                                }
                        );
                    }
                }).addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progress.dismiss();
                                Toast.makeText(StoreItemForm.this, "Failure", Toast.LENGTH_SHORT).show();
                            }
                        }
                );
            }
        }).addOnFailureListener(
                new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progress.dismiss();
                        Toast.makeText(StoreItemForm.this, "Failure", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==RESULT_LOAD_IMAGE){
            if(resultCode==RESULT_OK && data!=null){
                selectedImage = data.getData();
                itemImageView.setImageURI(selectedImage);
            }
        }

    }

    public void itemTakePhoto(View v){
        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i,RESULT_LOAD_IMAGE);
    }

    private void viewsinit(){
        itemBrandNameEditText = findViewById(R.id.brandEditText);
        itemDescriptionEditText = findViewById(R.id.itemDescriptionEditText);
        itemNameEditText = findViewById(R.id.itemNameEditText);
        itemPriceEditText = findViewById(R.id.itemPriceEditText);
        itemStockEditText = findViewById(R.id.itemStockditText);
        itemPhotoButton = findViewById(R.id.itemPhotoButton);
        itemMRPEditText = findViewById(R.id.itemMRPEditText);
        itemSubmitButton = findViewById(R.id.itemSubmitButton);
        itemImageView = findViewById(R.id.itemImageView);
    }
}

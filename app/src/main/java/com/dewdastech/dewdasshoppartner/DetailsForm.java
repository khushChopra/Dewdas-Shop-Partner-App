package com.dewdastech.dewdasshoppartner;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dewdastech.dewdasshoppartner.ch.hsr.geohash.GeoHash;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DetailsForm extends AppCompatActivity {

    // region Variables declaration
    // CONSTANTS
    protected final int PLACE_PICKER_REQUEST=854;
    protected final int RESULT_LOAD_IMAGE = 413;
    protected final int PERMISSION_REQUEST_CODE = 4433;

    // Views
    protected TextView storeIDTextView;
    protected EditText storeNameEditText;
    protected EditText phoneNumberEditText;
    protected EditText emailIDEditText;
    protected EditText ownerNameEditText;
    protected EditText descriptionEditText;
    protected Button placePickerButton;
    protected Button photoButton;
    protected Button submitButton;
    protected ImageView imageView;

    // Firebase variables
    protected FirebaseAuth myFirebaseAuth;
    protected StorageReference myMainStorageReference;
    protected StorageReference myStorageReference;
    protected DatabaseReference myDatabaseReference;

    // other variables
    protected Uri selectedImage;
    protected Bitmap tempSelectedImageBitmap;
    protected String downloadURL  = "";
    protected double latitude=0;
    protected double longitude=0;
    protected String area = "Empty";
    protected String storeID;
    // endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_form);

        // views initialisation
        viewsInit();

        // firebase init
        firebaseInit();

        // other variables init
        variablesInit();
    }

    public void detailsSubmit(View v){
        final ProgressDialog progress = new ProgressDialog(this);
        progress.setTitle("Loading");
        progress.setMessage("Wait while loading...");
        progress.setCancelable(false);
        progress.show();

        File file = new File(selectedImage.getPath());
        String imageFileName = file.getName();
        myStorageReference = myMainStorageReference.child("shopImages/"+storeID+"/"+imageFileName);

        myStorageReference.putFile(selectedImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                myStorageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        downloadURL = uri.toString();

                        Store myStore = new Store(
                                myFirebaseAuth.getCurrentUser().getUid(),
                                storeNameEditText.getText().toString(),
                                phoneNumberEditText.getText().toString(),
                                emailIDEditText.getText().toString(),
                                ownerNameEditText.getText().toString(),
                                latitude,
                                longitude,
                                descriptionEditText.getText().toString(),
                                downloadURL,
                                area
                        );

                        StoreDisplay myStoreDisplay = new StoreDisplay(myStore);

                        Map<String, Object> childUpdates = new HashMap<>();
                        childUpdates.put("/storeByID/" + myFirebaseAuth.getCurrentUser().getUid()+"/store",myStore);
                        childUpdates.put("/storeByArea/" + area + "/"+ myFirebaseAuth.getCurrentUser().getUid(), myStoreDisplay);

                        myDatabaseReference.updateChildren(childUpdates).addOnSuccessListener(
                                new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        progress.dismiss();
                                        Toast.makeText(DetailsForm.this, "Success", Toast.LENGTH_SHORT).show();
                                        Intent i = new Intent(getApplicationContext(),MainActivity.class);
                                        startActivity(i);
                                    }
                                }
                        ).addOnFailureListener(
                                new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        progress.dismiss();
                                        Toast.makeText(DetailsForm.this, "Failure", Toast.LENGTH_SHORT).show();
                                    }
                                }
                        );
                    }
                }).addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progress.dismiss();
                                Toast.makeText(DetailsForm.this, "Failure", Toast.LENGTH_SHORT).show();
                            }
                        }
                );
            }
        }).addOnFailureListener(
                new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progress.dismiss();
                        Toast.makeText(DetailsForm.this, "Failure", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    public void getImage(View v){
        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i,RESULT_LOAD_IMAGE);
    }

    // Launches activity to get lat, long and area for the store.
    public void getPlace(View v){
        Intent i = new Intent(getApplicationContext(),MapsActivity.class);
        startActivityForResult(i,PLACE_PICKER_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==RESULT_LOAD_IMAGE){
            if(resultCode==RESULT_OK && data!=null){
                selectedImage = data.getData();
                try {
                    tempSelectedImageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                } catch (IOException e) {
                    e.printStackTrace();
                }


                if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    // Permission is not granted
                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},PERMISSION_REQUEST_CODE);
                }
            }
        }
        else if(requestCode == PLACE_PICKER_REQUEST){
            if(resultCode == RESULT_OK && data != null){
                latitude = data.getExtras().getDouble("latitude");
                longitude = data.getExtras().getDouble("longitude");
                area = GeoHash.withCharacterPrecision(latitude, longitude, 5).toBase32();
                // area = getGeoHash(latitude,longitude);          TO DO NEXT
                Toast.makeText(getApplicationContext(), area+"- is area, lat is "+latitude+"!!", Toast.LENGTH_LONG).show();
            }
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission granted
                    selectedImage = getScaledImageUri(this,tempSelectedImageBitmap);
                    imageView.setImageURI(selectedImage);

                } else {
                    // permission denied
                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},PERMISSION_REQUEST_CODE);
                }
                return;
            }
        }

    }

    public Uri getScaledImageUri(Context context, Bitmap inImage) {
        int final_length = 450;
        int height = inImage.getHeight(), width = inImage.getWidth();
        inImage = Bitmap.createScaledBitmap(inImage,final_length,(final_length*height)/width,true);
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    private void viewsInit(){
        storeIDTextView = findViewById(R.id.storeIDTextView);
        storeNameEditText = findViewById(R.id.storeNameEditText);
        phoneNumberEditText = findViewById(R.id.phoneNumberEditText);
        emailIDEditText = findViewById(R.id.emailIDEditText);
        ownerNameEditText = findViewById(R.id.ownerNameEditText);
        descriptionEditText = findViewById(R.id.descriptionEditText);
        placePickerButton = findViewById(R.id.placePickerButton);
        photoButton = findViewById(R.id.photoButton);
        submitButton = findViewById(R.id.submitButton);
        imageView = findViewById(R.id.imageView);
    }

    private void firebaseInit(){
        myMainStorageReference = FirebaseStorage.getInstance().getReference();
        myFirebaseAuth = FirebaseAuth.getInstance();
        myDatabaseReference = FirebaseDatabase.getInstance().getReference();
    }

    private void variablesInit(){
        storeID = myFirebaseAuth.getCurrentUser().getUid();
        storeIDTextView.setText(storeID);
    }
}

package com.dewdastech.dewdasshoppartner;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DetailsForm extends AppCompatActivity {

    // CONSTANTS
    protected int PLACE_PICKER_REQUEST=854;
    protected int RESULT_LOAD_IMAGE = 413;

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
    protected String downloadURL  = "";
    protected String storeID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_form);

        // views initialisation
        viewsInit();

        //
        myMainStorageReference = FirebaseStorage.getInstance().getReference();
        myFirebaseAuth = FirebaseAuth.getInstance();
        myDatabaseReference = FirebaseDatabase.getInstance().getReference();


        storeID = myFirebaseAuth.getCurrentUser().getUid();
        storeIDTextView.setText(storeID);



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
                                0,
                                0,
                                descriptionEditText.getText().toString(),
                                downloadURL,
                                "Empty",
                                new ArrayList<StoreItem>()
                        );

                        StoreDisplay myStoreDisplay = new StoreDisplay(myStore);

                        Map<String, Object> childUpdates = new HashMap<>();
                        childUpdates.put("/storeByID/" + myFirebaseAuth.getCurrentUser().getUid(),myStore);
                        childUpdates.put("/storeByArea/" + "Empty/"+ myFirebaseAuth.getCurrentUser().getUid(), myStoreDisplay);

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==RESULT_LOAD_IMAGE){
            if(resultCode==RESULT_OK && data!=null){
                selectedImage = data.getData();
                imageView.setImageURI(selectedImage);
            }
        }

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
}

package com.dewdastech.dewdasshoppartner;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class Details extends Fragment {

    // region Variables
    // Views
    protected TextView storeIDTextView;
    protected TextView storeNameTextView;
    protected TextView phoneNumberTextView;
    protected TextView emailIDTextView;
    protected TextView ownerNameTextView;
    protected TextView descriptionTextView;
    protected ImageView storeImageView;



    protected Button editDetailsButton;
    protected Store currentStore = null;

    // Firebase variables
    protected FirebaseAuth myFirebaseAuth;
    protected FirebaseUser myUser;
    protected FirebaseAuth.AuthStateListener myListener;
    protected DatabaseReference myMainRef;
    protected DatabaseReference myReference;
    protected ValueEventListener myValueEventListener;
    // endregion

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_details, container, false);

        // views init
        storeIDTextView = v.findViewById(R.id.storeIDTextView);
        storeNameTextView = v.findViewById(R.id.storeNameTextView);
        descriptionTextView = v.findViewById(R.id.descriptionTextView);
        ownerNameTextView = v.findViewById(R.id.ownerNameTextView);
        phoneNumberTextView = v.findViewById(R.id.phoneNumberTextView);
        emailIDTextView = v.findViewById(R.id.emailIDTextView);
        storeImageView = v.findViewById(R.id.storeImageView);

        editDetailsButton = v.findViewById(R.id.editDetailsButton);
        editDetailsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // delete old storeByArea database reference
                if(currentStore!=null){
                    FirebaseDatabase.getInstance().getReference().child("storeByArea").child(currentStore.getArea()).child(currentStore.getStoreID()).setValue(null);
                }

                // deletes image         to implement

                // take to fill details
                startActivity(new Intent(getContext(),DetailsForm.class));
            }
        });

        // firebase init
        firebaseInit();

        return v;
    }

    private void setStoreView(Store myStore){
        if(myStore==null){
            storeIDTextView.setText("No store found");
        }
        else{
            storeIDTextView.setText("Store ID - "+myStore.getStoreID());
            storeNameTextView.setText(myStore.getStoreName());
            descriptionTextView.setText(myStore.getDescription());
            ownerNameTextView.setText(myStore.getOwnerName());
            phoneNumberTextView.setText(myStore.getPhoneNumber());
            emailIDTextView.setText(myStore.getEmailID());
            Glide.with(getContext()).load(myStore.getPhotoURL()).into(storeImageView);
        }
    }

    private void firebaseInit(){
        myFirebaseAuth = FirebaseAuth.getInstance();
        myUser = myFirebaseAuth.getCurrentUser();
        myMainRef = FirebaseDatabase.getInstance().getReference().child("storeByID");
        myListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                myUser = firebaseAuth.getCurrentUser();

                if(myUser==null){
                    databaseReferenceCleanUp();
                    storeIDTextView.setText("Not Logged in");
                    editDetailsButton.setEnabled(false);
                }
                else{
                    myReference = myMainRef.child(myUser.getUid()).child("store");
                    editDetailsButton.setEnabled(false);
                    myReference.addValueEventListener(myValueEventListener);
                }
            }
        };

        myValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Store myStore = dataSnapshot.getValue(Store.class);
                if(myStore==null){
                    setStoreView(null);
                    editDetailsButton.setEnabled(true);
                }
                else{
                    currentStore = myStore;
                    setStoreView(myStore);
                    editDetailsButton.setEnabled(true);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        };
        myFirebaseAuth.addAuthStateListener(myListener);
    }

    @Override
    public void onPause() {
        super.onPause();
        authCleanUp();
        databaseReferenceCleanUp();
    }

    @Override
    public void onResume() {
        super.onResume();
        if(myListener!=null && myFirebaseAuth!=null){
            myFirebaseAuth.addAuthStateListener(myListener);
        }
    }

    private void databaseReferenceCleanUp(){
        if(myValueEventListener!=null && myReference!=null){
            myReference.removeEventListener(myValueEventListener);
        }
    }

    private void authCleanUp(){
        if(myListener!=null && myFirebaseAuth!=null){
            myFirebaseAuth.removeAuthStateListener(myListener);
        }
    }

    public Details() {
        // Required empty public constructor
    }
}

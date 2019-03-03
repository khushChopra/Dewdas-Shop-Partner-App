package com.dewdastech.dewdasshoppartner;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class Details extends Fragment {

    // Views
    protected TextView detailsText;
    protected Button editDetailsButton;

    // Firebase variables
    protected FirebaseAuth myFirebaseAuth;
    protected FirebaseUser myUser;
    protected FirebaseAuth.AuthStateListener myListener;
    protected DatabaseReference myMainRef;
    protected DatabaseReference myReference;
    protected ValueEventListener myValueEventListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_details, container, false);

        // views init
        detailsText = v.findViewById(R.id.detailsText);
        editDetailsButton = v.findViewById(R.id.editDetailsButton);
        editDetailsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "works", Toast.LENGTH_SHORT).show();

                // take to fill details
                Intent i = new Intent(getContext(),DetailsForm.class);
                startActivity(i);
            }
        });


        // firebase init
        firebaseInit();

        return v;
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
                    detailsText.setText("Not logged in");
                    editDetailsButton.setEnabled(false);
                }
                else{
                    myReference = myMainRef.child(myUser.getUid());
                    editDetailsButton.setEnabled(true);
                    myReference.addValueEventListener(myValueEventListener);
                }
            }
        };

        myValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Store myStore = dataSnapshot.getValue(Store.class);
                if(myStore==null){
                    detailsText.setText("No data availabe");
                }
                else{
                    detailsText.setText(myStore.toString());
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

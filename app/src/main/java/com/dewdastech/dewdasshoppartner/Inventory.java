package com.dewdastech.dewdasshoppartner;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Inventory extends Fragment {

    // Views
    protected TextView inventoryTextView;
    protected ListView inventoryListView;
    protected Button inventoryAddButton;

    // Firebase variables
    protected FirebaseAuth myFirebaseAuth;
    protected FirebaseUser myUser;
    protected FirebaseAuth.AuthStateListener myListener;
    protected DatabaseReference myMainRef;
    protected DatabaseReference myReference;
    protected ChildEventListener myChildEventListener;

    // Other Variables
    protected List<StoreItem> storeItemList = new ArrayList<StoreItem>();
    protected StoreItemAdapter myArrayAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_inventory, container, false);

        // Views init
        inventoryTextView = v.findViewById(R.id.inventoryTextView);
        inventoryListView = v.findViewById(R.id.inventoryListView);
        inventoryAddButton = v.findViewById(R.id.inventoryAddButton);
        inventoryAddButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(getContext(),StoreItemForm.class);
                        startActivity(i);
                    }
                }
        );

        // Other Variables init
        myArrayAdapter = new StoreItemAdapter(getContext(),storeItemList);
        inventoryListView.setAdapter(myArrayAdapter);

        // Firebase init
        firebaseInit();

        return v;
    }


    private void firebaseInit(){
        myFirebaseAuth = FirebaseAuth.getInstance();
        myUser = myFirebaseAuth.getCurrentUser();
        myMainRef = FirebaseDatabase.getInstance().getReference().child("/storeByID/" + myFirebaseAuth.getCurrentUser().getUid());
        myListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                myUser = firebaseAuth.getCurrentUser();
                if(myUser==null){
                    databaseReferenceCleanUp();
                    inventoryTextView.setText("Not logged in");
                    inventoryAddButton.setEnabled(false);
                }
                else{
                    myReference = myMainRef.child("items");
                    inventoryAddButton.setEnabled(true);
                    storeItemList.clear();
                    myArrayAdapter.clear();
                    myReference.addChildEventListener(myChildEventListener);
                }
            }
        };

        myChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                StoreItem newStoreItem = dataSnapshot.getValue(StoreItem.class);
                myArrayAdapter.add(newStoreItem);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                int index =0;
                String key = dataSnapshot.getKey();
                while(index<storeItemList.size()){
                    if(storeItemList.get(index).getStoreItemID().equals(key)){
                        break;
                    }
                    index++;
                }
                storeItemList.remove(index);
                storeItemList.add(index,dataSnapshot.getValue(StoreItem.class));
                myArrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                int index =0;
                String key = dataSnapshot.getKey();
                while(index<storeItemList.size()){
                    if(storeItemList.get(index).getStoreItemID().equals(key)){
                        break;
                    }
                    index++;
                }
                storeItemList.remove(index);
                myArrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
    }

    @Override
    public void onPause() {
        authCleanUp();
        databaseReferenceCleanUp();
        super.onPause();
    }

    @Override
    public void onResume() {
        if(myListener!=null && myFirebaseAuth!=null){
            myFirebaseAuth.addAuthStateListener(myListener);
        }
        super.onResume();
    }

    private void databaseReferenceCleanUp(){
        if(myChildEventListener!=null && myReference!=null){
            myReference.removeEventListener(myChildEventListener);
            storeItemList.clear();
            myArrayAdapter.clear();
        }
    }

    private void authCleanUp(){
        if(myListener!=null && myFirebaseAuth!=null){
            myFirebaseAuth.removeAuthStateListener(myListener);
        }
    }

    public Inventory() {
        // Required empty public constructor
    }

}

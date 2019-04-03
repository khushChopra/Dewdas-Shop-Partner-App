package com.dewdastech.dewdasshoppartner;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class SelectItemFromInventory extends AppCompatActivity {

    // region Variables
    protected EditText itemSearchEditText;
    protected ListView searchInventoryListView;
    protected Button addNewItemButton;

    // Firebase variables
    protected FirebaseAuth myFirebaseAuth;
    protected FirebaseUser myUser;
    protected FirebaseAuth.AuthStateListener myListener;
    protected DatabaseReference myMainRef;
    protected DatabaseReference myReference;
    protected ChildEventListener myChildEventListener;

    // Other Variables
    protected List<StoreItem> storeItemList = new ArrayList<StoreItem>();
    protected SearchStoreItemAdapter myArrayAdapter;

    // endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_item_from_inventory);

        viewsInit();

        myArrayAdapter = new SearchStoreItemAdapter(getApplicationContext(),storeItemList);
        searchInventoryListView.setAdapter(myArrayAdapter);

        firebaseInit();
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
                }
                else{
                    myReference = myMainRef.child("items");
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

    private void viewsInit(){
        itemSearchEditText = findViewById(R.id.itemSearchEditText);
        searchInventoryListView = findViewById(R.id.searchInventoryListView);
        addNewItemButton = findViewById(R.id.addNewItemButton);
        addNewItemButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(getApplicationContext(),StoreItemForm.class);
                        startActivity(i);
                    }
                }
        );
        itemSearchEditText.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        searchInventoryListView.setFilterText(itemSearchEditText.getText().toString());
                    }
                }
        );
    }

    // TO DO Enable searching
}

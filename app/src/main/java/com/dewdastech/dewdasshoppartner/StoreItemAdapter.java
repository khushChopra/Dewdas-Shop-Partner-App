package com.dewdastech.dewdasshoppartner;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.module.AppGlideModule;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.sql.Array;
import java.util.ArrayList;
import java.util.List;

public class StoreItemAdapter extends ArrayAdapter<StoreItem> {
    private Context myContext;
    private List<StoreItem> itemList= new ArrayList<>();

    public StoreItemAdapter(@NonNull Context context, @LayoutRes List<StoreItem> list) {
        super(context, 0, list);
        myContext = context;
        itemList = list;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {


        View listItem = convertView;
        if(listItem==null){
            listItem = LayoutInflater.from(myContext).inflate(R.layout.store_item_row,parent,false);
        }

        TextView brandTextView = listItem.findViewById(R.id.brandNameTextView);
        TextView descriptionTextView = listItem.findViewById(R.id.descriptionTextView);
        TextView nameTextView = listItem.findViewById(R.id.nameTextView);
        TextView priceTextView = listItem.findViewById(R.id.priceTextView);
        TextView stockTextView = listItem.findViewById(R.id.stockTextView);
        TextView mrpTextView = listItem.findViewById(R.id.mrpTextView);
        ImageView itemImageView = listItem.findViewById(R.id.itemImageView);
        Button deleteButton = listItem.findViewById(R.id.deleteButton);
        Button plusButton = listItem.findViewById(R.id.plusButton);
        Button minusButton = listItem.findViewById(R.id.minusButton);

        final StoreItem myItem = itemList.get(position);

        brandTextView.setText(myItem.getBrand());
        descriptionTextView.setText(myItem.getDescription());
        nameTextView.setText(myItem.getName());
        priceTextView.setText(Float.toString(myItem.getPrice()));
        mrpTextView.setText(Float.toString(myItem.getMrp()));
        stockTextView.setText(Integer.toString(myItem.getStock()));
        Glide.with(myContext).load(myItem.getPhotoURL()).into(itemImageView);
        deleteButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference().child("storeByID").child(FirebaseAuth.getInstance().getUid()).child("items").child(myItem.getStoreItemID());
                        myRef.removeValue();
                    }
                }
        );
        minusButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference().child("storeByID").child(FirebaseAuth.getInstance().getUid()).child("items").child(myItem.getStoreItemID());
                        myRef.child("stock").setValue(myItem.getStock()-1);
                        Toast.makeText(getContext(), "minus", Toast.LENGTH_SHORT).show();
                    }
                }
        );
        plusButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference().child("storeByID").child(FirebaseAuth.getInstance().getUid()).child("items").child(myItem.getStoreItemID());
                        myRef.child("stock").setValue(myItem.getStock()+1);
                        Toast.makeText(getContext(), "Plus", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        return listItem;
    }
}

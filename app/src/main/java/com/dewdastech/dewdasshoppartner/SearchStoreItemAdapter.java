package com.dewdastech.dewdasshoppartner;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class SearchStoreItemAdapter extends ArrayAdapter {

    private Context myContext;
    private List<StoreItem> itemList;
    private Filter myFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            if (constraint != null && constraint.length() > 0) {
                ArrayList<StoreItem> filterList = new ArrayList<>();
                for (int i = 0; i < itemList.size(); i++) {
                    if ((itemList.get(i).getBrand().toLowerCase()).contains(constraint.toString().toLowerCase()) ||
                            (itemList.get(i).getName().toLowerCase()).contains(constraint.toString().toLowerCase())) {
                        filterList.add(itemList.get(i));
                    }
                }
                results.count = filterList.size();
                results.values = filterList;
            }
            else {
                results.count = itemList.size();
                results.values = itemList;
            }
            List<StoreItem> temp = (List<StoreItem>) results.values;
            for(StoreItem a : temp){
                Log.i("bhaut hard",a.toString());
            }
            Log.i("bhaut hard",""+results.count);

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            itemList = (List<StoreItem>) results.values;
            notifyDataSetChanged();
        }
    };

    public SearchStoreItemAdapter(@NonNull Context context, List<StoreItem> list) {
        super(context, 0, list);
        myContext = context;
        itemList = list;
    }

    @Override
    public Filter getFilter(){
        return myFilter;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {


        View listItem = convertView;
        if(listItem==null){
            listItem = LayoutInflater.from(myContext).inflate(R.layout.search_store_item_row,parent,false);
        }

        TextView brandTextView = listItem.findViewById(R.id.brandNameTextView);
        TextView descriptionTextView = listItem.findViewById(R.id.descriptionTextView);
        TextView nameTextView = listItem.findViewById(R.id.nameTextView);
        TextView priceTextView = listItem.findViewById(R.id.priceTextView);
        TextView stockTextView = listItem.findViewById(R.id.stockTextView);
        TextView mrpTextView = listItem.findViewById(R.id.mrpTextView);
        ImageView itemImageView = listItem.findViewById(R.id.itemImageView);
        Button addStockButton = listItem.findViewById(R.id.addStockButton);
        Button changeSellingPriceButton = listItem.findViewById(R.id.changeSellingPriceButton);

        final StoreItem myItem = itemList.get(position);

        brandTextView.setText(myItem.getBrand());
        descriptionTextView.setText(myItem.getDescription());
        nameTextView.setText(myItem.getName());
        priceTextView.setText(Float.toString(myItem.getPrice()));
        mrpTextView.setText(Float.toString(myItem.getMrp()));
        stockTextView.setText(Integer.toString(myItem.getStock()));
        Glide.with(myContext).load(myItem.getPhotoURL()).into(itemImageView);

        // region Button on click listeners
        addStockButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(getContext(),enterNumberOfStock.class);
                        i.putExtra("updatePath","storeByID/"+FirebaseAuth.getInstance().getUid()+"/items/"+myItem.getStoreItemID()+"/stock");
                        i.putExtra("currentCount",myItem.getStock());
                        i.putExtra("displayMessage","Enter the number of stocks added of "+myItem.toString());
                        myContext.startActivity(i);

                    }
                }
        );

        changeSellingPriceButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(getContext(),enterNumberOfStock.class);
                        i.putExtra("updatePath","storeByID/"+FirebaseAuth.getInstance().getUid()+"/items/"+myItem.getStoreItemID()+"/price");
                        i.putExtra("currentCount",0);
                        i.putExtra("displayMessage","Enter the number of stocks added of "+myItem.toString());
                        myContext.startActivity(i);
                    }
                }
        );
        // endregion

        return listItem;
    }

}


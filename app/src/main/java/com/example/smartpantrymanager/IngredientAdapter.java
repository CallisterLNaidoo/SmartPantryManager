package com.example.smartpantrymanager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class IngredientAdapter extends BaseAdapter {

    private final Context context;
    private final List<Ingredient> ingredientList;

    public IngredientAdapter(Context context, List<Ingredient> ingredientList) {
        this.context = context;
        this.ingredientList = ingredientList;
    }

    @Override
    public int getCount() {
        return ingredientList.size();
    }

    @Override
    public Object getItem(int position) {
        return ingredientList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return ingredientList.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(context)
                    .inflate(R.layout.item_ingredient, parent, false);
        }

        TextView txtItemName =
                convertView.findViewById(R.id.txtItemName);

        TextView txtItemQuantity =
                convertView.findViewById(R.id.txtItemQuantity);

        TextView txtItemExpiry =
                convertView.findViewById(R.id.txtItemExpiry);

        Ingredient ingredient = ingredientList.get(position);

        txtItemName.setText(ingredient.getName());

        double quantity = ingredient.getQuantity();

        String quantityText;

        if (quantity == (int) quantity) {
            quantityText = String.valueOf((int) quantity);
        } else {
            quantityText = String.valueOf(quantity);
        }

        txtItemQuantity.setText(
                quantityText + " " + ingredient.getUnit()
        );

        String expiryDate = ingredient.getExpiryDate();

        if (expiryDate == null || expiryDate.isEmpty()) {
            txtItemExpiry.setText("Expiry: Not specified");
        } else {
            txtItemExpiry.setText("Expiry: " + expiryDate);
        }

        return convertView;
    }
}
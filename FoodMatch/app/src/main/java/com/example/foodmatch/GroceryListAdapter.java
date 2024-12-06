package com.example.foodmatch;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.widget.ArrayAdapter;
import com.bumptech.glide.Glide;
import java.util.ArrayList;

public class GroceryListAdapter extends ArrayAdapter<GroceryItem> {

    private Context context;
    private int resource;
    private ArrayList<GroceryItem> groceryList;
    private GroceryListActivity activity; // Referencia a la actividad para manejar la eliminación
    private SupermarketActivity activity2;

    public GroceryListAdapter(Context context, int resource, ArrayList<GroceryItem> groceryList, GroceryListActivity activity) {
        super(context, resource, groceryList);
        this.context = context;
        this.resource = resource;
        this.groceryList = groceryList;
        this.activity = activity;
    }

    public GroceryListAdapter(Context context, int resource, ArrayList<GroceryItem> groceryList, SupermarketActivity activity) {
        super(context, resource, groceryList);
        this.context = context;
        this.resource = resource;
        this.groceryList = groceryList;
        this.activity2 = activity;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(resource, parent, false);
        }

        GroceryItem item = groceryList.get(position);

        ImageView groceryItemImage = convertView.findViewById(R.id.groceryItemImage);
        TextView groceryItemName = convertView.findViewById(R.id.groceryItemName);
        TextView groceryItemStore = convertView.findViewById(R.id.groceryItemStore);
        TextView groceryItemPrice = convertView.findViewById(R.id.groceryItemPrice);
        ImageButton deleteButton = convertView.findViewById(R.id.deleteButton);

        groceryItemName.setText(item.getName());
        groceryItemStore.setText("Store: " + item.getStore());
        groceryItemPrice.setText("Price: $" + item.getPrice());

        Glide.with(context)
                .load(context.getResources().getIdentifier(
                        item.getImageUrl(), "drawable", context.getPackageName()))
                .placeholder(R.drawable.ic_grocery_list)
                .into(groceryItemImage);

        // Manejar la acción del botón de borrar
        deleteButton.setOnClickListener(v -> {
            groceryList.remove(position); // Eliminar de la lista local
            notifyDataSetChanged(); // Actualizar la UI
            if (item.getKey() != null) {
                activity.deleteItemFromDatabase(item.getKey()); // Eliminar de Firebase
            }
        });

        return convertView;
    }
}

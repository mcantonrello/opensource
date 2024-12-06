package com.example.foodmatch;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private static final String TAG = "ProductAdapter";
    private List<Product> productList;
    private Context context;

    public ProductAdapter(Context context, List<Product> productList) {
        this.productList = productList;
        this.context = context;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);

        // Configurar los datos del producto
        holder.productNameTextView.setText(product.getName());
        holder.storeTextView.setText("Store: " + product.getStore());
        holder.priceTextView.setText("Price: $" + product.getPrice());
        holder.pricePerOunceTextView.setText("Price per Ounce: $" + product.getPricePerOunce());

        // Cargar imagen del producto
        int imageResId = context.getResources().getIdentifier(
                product.getImageUrl(), "drawable", context.getPackageName()
        );
        if (imageResId != 0) {
            holder.productImageView.setImageResource(imageResId);
        } else {
            holder.productImageView.setImageResource(R.drawable.ic_grocery_list);
        }

        // Configurar el botón de "Add to Grocery List"
        holder.addToGroceryListButton.setOnClickListener(v -> {
            Log.d(TAG, "Add button clicked for product: " + product.getName());

            // Obtener el userId del usuario
            String userId = UserUtils.getUserIdOrThrow(context);
            Log.d(TAG, "User ID retrieved: " + userId);

            // Referencia a la lista de compras en Firebase
            DatabaseReference groceryListRef = FirebaseDatabase.getInstance()
                    .getReference("grocery_list")
                    .child(userId);

            // Agregar el producto a la lista
            groceryListRef.push().setValue(product)
                    .addOnSuccessListener(aVoid -> {
                        Log.d(TAG, "Product added to grocery list: " + product.getName());
                        Toast.makeText(context, product.getName() + " added to Grocery List!", Toast.LENGTH_SHORT).show();

                        // Redirigir a GroceryListActivity
                        Intent intent = new Intent(context, GroceryListActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // Necesario para iniciar actividad desde Adapter
                        context.startActivity(intent);
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Failed to add product to grocery list: " + product.getName(), e);
                        Toast.makeText(context, "Failed to add " + product.getName() + " to Grocery List", Toast.LENGTH_SHORT).show();
                    });
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView productNameTextView, storeTextView, priceTextView, pricePerOunceTextView;
        ImageView productImageView;
        Button addToGroceryListButton;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            productNameTextView = itemView.findViewById(R.id.productNameTextView);
            storeTextView = itemView.findViewById(R.id.storeTextView);
            priceTextView = itemView.findViewById(R.id.priceTextView);
            pricePerOunceTextView = itemView.findViewById(R.id.pricePerOunceTextView);
            productImageView = itemView.findViewById(R.id.productImageView);
            addToGroceryListButton = itemView.findViewById(R.id.addToGroceryListButton);
        }
    }
}

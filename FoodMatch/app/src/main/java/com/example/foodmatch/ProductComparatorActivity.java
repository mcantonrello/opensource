package com.example.foodmatch;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ProductComparatorActivity extends AppCompatActivity {

    private static final String TAG = "ProductComparator";
    private RecyclerView productRecyclerView;
    private ProductAdapter productAdapter;
    private List<Product> productList;
    private DatabaseReference productsDatabase;

    private Button filterRecommended, filterLowestPrice, filterBestPricePerOunce;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_comparator);

        // Inicializar RecyclerView y Adapter
        productRecyclerView = findViewById(R.id.productRecyclerView);
        productRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        productList = new ArrayList<>();
        productAdapter = new ProductAdapter(this, productList);
        productRecyclerView.setAdapter(productAdapter);

        // Botón de regreso
        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish());

        // Inicializar referencia a la base de datos de Firebase
        productsDatabase = FirebaseDatabase.getInstance().getReference("products");

        // Obtener el nombre del producto desde ProductSearchActivity
        String productName = getIntent().getStringExtra("product_name");
        if (productName != null) {
            fetchProductData(productName);
        }

        // Configurar botones de filtro
        filterRecommended = findViewById(R.id.filterRecommended);
        filterLowestPrice = findViewById(R.id.filterLowestPrice);
        filterBestPricePerOunce = findViewById(R.id.filterBestPricePerOunce);

        // Configurar listeners de los botones de filtro
        View.OnClickListener filterClickListener = v -> {
            // Restablecer el estado de todos los botones
            resetFilterButtons();
            v.setSelected(true); // Resaltar el botón seleccionado

            // Aplicar el filtro correspondiente
            if (v.getId() == R.id.filterRecommended) {
                filterRecommended();
            } else if (v.getId() == R.id.filterLowestPrice) {
                filterByLowestPrice();
            } else if (v.getId() == R.id.filterBestPricePerOunce) {
                filterByBestPricePerOunce();
            }
        };

        // Asignar los listeners a los botones
        filterRecommended.setOnClickListener(filterClickListener);
        filterLowestPrice.setOnClickListener(filterClickListener);
        filterBestPricePerOunce.setOnClickListener(filterClickListener);

        // Seleccionar "Recommended" por defecto
        filterRecommended.setSelected(true);
        filterRecommended();
    }

    // Método para restablecer el estado de los botones de filtro
    private void resetFilterButtons() {
        filterRecommended.setSelected(false);
        filterLowestPrice.setSelected(false);
        filterBestPricePerOunce.setSelected(false);
    }

    // Método para filtrar productos recomendados
    private void filterRecommended() {
        Log.d(TAG, "Applying 'Recommended' filter");
        List<Product> recommendedProducts = new ArrayList<>();
        for (Product product : productList) {
            if ("Trader Joe's".equals(product.getStore())) { // Ejemplo de criterio
                recommendedProducts.add(product);
            }
        }
        updateRecyclerView(recommendedProducts);
    }

    // Método para filtrar por precio más bajo
    private void filterByLowestPrice() {
        Log.d(TAG, "Applying 'Lowest Price' filter");
        List<Product> sortedProducts = new ArrayList<>(productList);
        sortedProducts.sort((p1, p2) -> Double.compare(p1.getPrice(), p2.getPrice()));
        updateRecyclerView(sortedProducts);
    }

    // Método para filtrar por mejor precio por onza
    private void filterByBestPricePerOunce() {
        Log.d(TAG, "Applying 'Best Price per Ounce' filter");
        List<Product> sortedProducts = new ArrayList<>(productList);
        sortedProducts.sort((p1, p2) -> Double.compare(p1.getPricePerOunce(), p2.getPricePerOunce()));
        updateRecyclerView(sortedProducts);
    }

    // Método para actualizar el RecyclerView con los productos filtrados
    private void updateRecyclerView(List<Product> filteredProducts) {
        productAdapter = new ProductAdapter(this, filteredProducts);
        productRecyclerView.setAdapter(productAdapter);
    }

    // Método para buscar datos del producto en Firebase
    private void fetchProductData(String productName) {
        Log.d(TAG, "Fetching product data for: " + productName);

        productsDatabase.orderByChild("name").equalTo(productName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                productList.clear();
                if (snapshot.exists()) {
                    for (DataSnapshot productSnapshot : snapshot.getChildren()) {
                        Product product = productSnapshot.getValue(Product.class);
                        if (product != null) {
                            productList.add(product);
                            Log.d(TAG, "Product found: " + product.getName());
                        }
                    }
                    productAdapter.notifyDataSetChanged();
                } else {
                    Log.e(TAG, "Product not found in Firebase");
                    Toast.makeText(ProductComparatorActivity.this, "Product not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Error fetching product data: " + error.getMessage());
                Toast.makeText(ProductComparatorActivity.this, "Error connecting to Firebase", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

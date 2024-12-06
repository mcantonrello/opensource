package com.example.foodmatch;

import android.os.Bundle;
import android.util.Log;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FirebaseInitializerActivity extends AppCompatActivity {

    private DatabaseReference productsDatabase;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Referencia a la base de datos en el nodo "products"
        productsDatabase = FirebaseDatabase.getInstance().getReference("products");

        // Inicializar productos en Firebase
        initializeProducts();
    }

    private void initializeProducts() {
        Log.d("FirebaseInitializer", "Inicializando productos en Firebase...");

        Product apple = new Product("Apple", 0.99, 0.25, "Supermarket A", "https://example.com/apple.jpg");
        Product banana = new Product("Banana", 0.59, 0.20, "Supermarket B", "https://example.com/banana.jpg");
        Product orange = new Product("Orange", 1.19, 0.30, "Supermarket C", "https://example.com/orange.jpg");
        Product milk = new Product("Milk", 2.49, 0.10, "Supermarket A", "https://example.com/milk.jpg");
        Product bread = new Product("Bread", 1.79, 0.15, "Supermarket B", "https://example.com/bread.jpg");
        Product cheese = new Product("Cheese", 3.99, 0.50, "Supermarket C", "https://example.com/cheese.jpg");
        Product rice = new Product("Rice", 1.99, 0.05, "Supermarket A", "https://example.com/rice.jpg");
        Product chicken = new Product("Chicken", 4.99, 0.30, "Supermarket B", "https://example.com/chicken.jpg");
        Product salmon = new Product("Salmon", 7.99, 0.60, "Supermarket C", "https://example.com/salmon.jpg");

        // Agregar productos a Firebase
        productsDatabase.push().setValue(apple);
        productsDatabase.push().setValue(banana);
        productsDatabase.push().setValue(orange);
        productsDatabase.push().setValue(milk);
        productsDatabase.push().setValue(bread);
        productsDatabase.push().setValue(cheese);
        productsDatabase.push().setValue(rice);
        productsDatabase.push().setValue(chicken);
        productsDatabase.push().setValue(salmon);

        Log.d("FirebaseInitializer", "Productos inicializados correctamente.");
    }
}


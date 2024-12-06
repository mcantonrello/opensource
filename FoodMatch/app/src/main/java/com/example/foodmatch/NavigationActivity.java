package com.example.foodmatch;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class NavigationActivity extends AppCompatActivity {

    private TextView profileOption, groceryListOption, storesOption;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        profileOption = findViewById(R.id.profileOption);
        groceryListOption = findViewById(R.id.groceryListOption);
        storesOption = findViewById(R.id.storesOption);

        ImageButton logoutButton = findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(v -> {
            // Aquí defines la lógica de logout
            Intent intent = new Intent(NavigationActivity.this, SignInActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });


        profileOption.setOnClickListener(v -> {
            // Navegar a la actividad de perfil
            Intent intent = new Intent(NavigationActivity.this, UserProfileActivity.class);
            startActivity(intent);
        });

        groceryListOption.setOnClickListener(v -> {
            // Navegar a la actividad de lista de compras
            Intent intent = new Intent(NavigationActivity.this, GroceryListActivity.class);
            startActivity(intent);
        });

        storesOption.setOnClickListener(v -> {
            // Navegar a la actividad de búsqueda de tiendas
            Intent intent = new Intent(NavigationActivity.this, ProductSearchActivity.class);
            startActivity(intent);
        });
    }
}

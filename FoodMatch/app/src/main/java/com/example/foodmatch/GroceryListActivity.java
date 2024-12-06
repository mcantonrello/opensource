package com.example.foodmatch;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class GroceryListActivity extends AppCompatActivity {

    private ListView groceryListView;
    private ImageButton menuButton;
    private Button navigateToSupermarketButton;
    private GroceryListAdapter adapter;
    private ArrayList<GroceryItem> groceryList; // Lista de compras
    private DatabaseReference groceryListDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grocery_list);

        try {
            // Inicializar la lista aquí
            groceryList = new ArrayList<>();

            groceryListView = findViewById(R.id.groceryListView);
            menuButton = findViewById(R.id.menuButton);
            navigateToSupermarketButton = findViewById(R.id.navigateToSupermarketButton);

            // Configurar el adaptador
            adapter = new GroceryListAdapter(this, R.layout.item_grocery, groceryList, this);
            groceryListView.setAdapter(adapter);

            // Botón de navegación al menú
            menuButton.setOnClickListener(v -> {
                Intent intent = new Intent(GroceryListActivity.this, NavigationActivity.class);
                startActivity(intent);
                finish();
            });

            // Navegar a SupermarketActivity
            navigateToSupermarketButton.setOnClickListener(v -> {
                Intent intent = new Intent(GroceryListActivity.this, SupermarketActivity.class);
                startActivity(intent);
            });

            // Obtener el userId
            String userId = UserUtils.getUserIdOrThrow(this);

            // Referencia a la base de datos
            groceryListDatabase = FirebaseDatabase.getInstance().getReference("grocery_list").child(userId);

            // Listener de la base de datos
            groceryListDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    groceryList.clear(); // Limpiar antes de actualizar
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        GroceryItem item = dataSnapshot.getValue(GroceryItem.class);
                        if (item != null) {
                            item.setKey(dataSnapshot.getKey()); // Guardar la clave de Firebase
                            groceryList.add(item);
                        }
                    }
                    adapter.notifyDataSetChanged(); // Notificar al adaptador
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(GroceryListActivity.this, "Error loading grocery list", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (IllegalStateException e) {
            Toast.makeText(this, "User not logged in. Redirecting to Sign In.", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, SignInActivity.class));
            finish();
        }
    }

    // Método para eliminar un ítem de la base de datos
    public void deleteItemFromDatabase(String itemKey) {
        groceryListDatabase.child(itemKey).removeValue().addOnSuccessListener(aVoid -> {
            Toast.makeText(this, "Item removed successfully", Toast.LENGTH_SHORT).show();
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Failed to remove item", Toast.LENGTH_SHORT).show();
        });
    }
}

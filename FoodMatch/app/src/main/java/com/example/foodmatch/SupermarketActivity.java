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

public class SupermarketActivity extends AppCompatActivity {

    private ListView groceryListView;
    private SupermarketAdapter adapter;
    private ArrayList<GroceryItem> groceryList;
    private DatabaseReference groceryListDatabase;
    private ImageButton backButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_supermarket);

        backButton = findViewById(R.id.backButton);

        // Configurar el botón de retroceso
        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(SupermarketActivity.this, GroceryListActivity.class);
            startActivity(intent);
            finish();  // Opcional: Termina esta actividad para evitar que el usuario vuelva a ella usando el botón de retroceso
        });

        groceryListView = findViewById(R.id.groceryListView);
        groceryList = new ArrayList<>();

        // Obtener el userId
        String userId = UserUtils.getUserIdOrThrow(this);

        // Inicializar el adaptador de la lista
        adapter = new SupermarketAdapter(this, R.layout.item_grocery, groceryList, this);
        groceryListView.setAdapter(adapter);

        // Referencia a la base de datos
        groceryListDatabase = FirebaseDatabase.getInstance().getReference("grocery_list").child(userId);

        // Configurar botones para cada supermercado
        setUpButtonListeners();

    }

    private void setUpButtonListeners() {
        Button buttonAldi = findViewById(R.id.buttonAldi);
        Button buttonCostco = findViewById(R.id.buttonCostco);
        Button buttonTarget = findViewById(R.id.buttonTarget);
        Button buttonTraderJoes = findViewById(R.id.buttonTraderJoes);
        Button buttonWalmart = findViewById(R.id.buttonWalmart);
        Button buttonWholeFoods = findViewById(R.id.buttonWholeFoods);

        // Listener para el botón de Aldi
        buttonAldi.setOnClickListener(v -> fetchProductsForStore("Aldi"));

        // Listener para el botón de Costco
        buttonCostco.setOnClickListener(v -> fetchProductsForStore("Costco"));

        // Listener para el botón de Target
        buttonTarget.setOnClickListener(v -> fetchProductsForStore("Target"));

        // Listener para el botón de Trader Joe's
        buttonTraderJoes.setOnClickListener(v -> fetchProductsForStore("Trader Joe's"));

        // Listener para el botón de Walmart
        buttonWalmart.setOnClickListener(v -> fetchProductsForStore("Walmart"));


        buttonWholeFoods.setOnClickListener(v -> fetchProductsForStore("Whole Foods"));

    }

    // Método para obtener los productos filtrados por supermercado
    private void fetchProductsForStore(String store) {
        // Obtener el userId
        String userId = UserUtils.getUserIdOrThrow(this);

        // Obtener los productos del supermercado correspondiente y del usuario
        groceryListDatabase.orderByChild("store").equalTo(store).addListenerForSingleValueEvent(new ValueEventListener() {
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
                Toast.makeText(SupermarketActivity.this, "Error loading grocery list", Toast.LENGTH_SHORT).show();
            }
        });
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

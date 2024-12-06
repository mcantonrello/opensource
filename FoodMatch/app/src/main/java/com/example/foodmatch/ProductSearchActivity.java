package com.example.foodmatch;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class ProductSearchActivity extends AppCompatActivity implements ProductSuggestionAdapter.OnSuggestionClickListener {

    private EditText searchBar;
    private RecyclerView suggestionsRecyclerView;
    private ProductSuggestionAdapter adapter;
    private List<String> suggestions;
    private ImageButton menuButton;
    private Button searchButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_search);

        searchBar = findViewById(R.id.searchBar);
        suggestionsRecyclerView = findViewById(R.id.suggestionsRecyclerView);
        menuButton = findViewById(R.id.menuButton);
        searchButton = findViewById(R.id.searchButton);

        menuButton.setOnClickListener(v -> {
            Intent intent = new Intent(ProductSearchActivity.this, NavigationActivity.class);
            startActivity(intent);
        });

        // Listener for the search button
        searchButton.setOnClickListener(v -> {
            String query = searchBar.getText().toString();
            if (!query.isEmpty()) {
                navigateToProductComparator(query);
            } else {
                Toast.makeText(this, "Please enter a product", Toast.LENGTH_SHORT).show();
            }
        });

        // Initialize suggestions list
        suggestions = new ArrayList<>();
        suggestions.add("Apple");
        suggestions.add("Banana");
        suggestions.add("Orange");
        suggestions.add("Pear");
        suggestions.add("Mango");

        // RecyclerView configuration
        adapter = new ProductSuggestionAdapter(suggestions, this);
        suggestionsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        suggestionsRecyclerView.setAdapter(adapter);

        // Listener for text changes in the search bar
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterSuggestions(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        // Listener to detect the Enter key
        searchBar.setOnEditorActionListener((TextView v, int actionId, KeyEvent event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                    (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN)) {
                String query = searchBar.getText().toString();
                if (!query.isEmpty()) {
                    navigateToProductComparator(query);
                }
                return true;
            }
            return false;
        });
    }

    private void filterSuggestions(String query) {
        List<String> filteredSuggestions = new ArrayList<>();
        for (String suggestion : suggestions) {
            if (suggestion.toLowerCase().contains(query.toLowerCase())) {
                filteredSuggestions.add(suggestion);
            }
        }
        adapter = new ProductSuggestionAdapter(filteredSuggestions, this);
        suggestionsRecyclerView.setAdapter(adapter);
    }

    @Override
    public void onSuggestionClick(String suggestion) {
        Toast.makeText(this, "Selected: " + suggestion, Toast.LENGTH_SHORT).show();
        navigateToProductComparator(suggestion);
    }

    // Navigate to ProductComparatorActivity with the product name
    private void navigateToProductComparator(String productName) {
        Log.d("ProductSearchActivity", "Navigating with product: " + productName);
        Intent intent = new Intent(ProductSearchActivity.this, ProductComparatorActivity.class);
        intent.putExtra("product_name", productName);
        startActivity(intent);
    }
}

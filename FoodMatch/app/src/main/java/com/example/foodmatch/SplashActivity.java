// SplashActivity.java
package com.example.foodmatch;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.FirebaseApp;

public class SplashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Inicializar Firebase
        FirebaseApp.initializeApp(this);

        // Navegar a la siguiente actividad después de la inicialización
        startActivity(new Intent(SplashActivity.this, SignUpActivity.class));
        finish();
    }
}

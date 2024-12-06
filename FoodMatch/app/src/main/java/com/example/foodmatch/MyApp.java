// MyApp.java
package com.example.foodmatch;

import android.app.Application;
import com.google.firebase.FirebaseApp;

public class MyApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // Inicializa Firebase
        FirebaseApp.initializeApp(this);
    }
}

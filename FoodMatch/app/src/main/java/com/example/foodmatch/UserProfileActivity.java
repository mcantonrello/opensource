package com.example.foodmatch;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class UserProfileActivity extends AppCompatActivity {

    private Button editPasswordButton, logoutButton, deleteAccountButton;
    private ImageButton backToMenuButton;
    private DatabaseReference usersDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        // Inicializar la referencia a la base de datos
        usersDatabase = FirebaseDatabase.getInstance().getReference("users");

        backToMenuButton = findViewById(R.id.backToMenuButton);
        editPasswordButton = findViewById(R.id.editPasswordButton);
        logoutButton = findViewById(R.id.logoutButton);
        deleteAccountButton = findViewById(R.id.deleteAccountButton);

        // Configurar botón para regresar al menú
        backToMenuButton.setOnClickListener(v -> {
            Intent intent = new Intent(UserProfileActivity.this, NavigationActivity.class);
            startActivity(intent);
            finish();
        });

        // Configurar botón para editar contraseña
        editPasswordButton.setOnClickListener(v -> {
            Intent intent = new Intent(UserProfileActivity.this, EditPasswordActivity.class);
            startActivity(intent);
        });

        // Configurar botón para cerrar sesión
        logoutButton.setOnClickListener(v -> {
            Toast.makeText(UserProfileActivity.this, "Logged out", Toast.LENGTH_SHORT).show();

            // Limpiar userId usando UserUtils
            UserUtils.clearUserId(this);

            // Redirigir a la pantalla de inicio de sesión
            Intent intent = new Intent(UserProfileActivity.this, SignInActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });

        // Configurar botón para eliminar cuenta
        deleteAccountButton.setOnClickListener(v -> {
            try {
                // Recuperar userId usando UserUtils
                String userId = UserUtils.getUserIdOrThrow(this);

                // Eliminar la cuenta del usuario en Firebase
                usersDatabase.child(userId).removeValue().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(UserProfileActivity.this, "Account deleted", Toast.LENGTH_SHORT).show();

                        // Limpiar userId usando UserUtils
                        UserUtils.clearUserId(this);

                        // Redirigir a la pantalla de inicio de sesión
                        Intent intent = new Intent(UserProfileActivity.this, SignInActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(UserProfileActivity.this, "Failed to delete account", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (IllegalStateException e) {
                Toast.makeText(this, "User ID not found. Please log in again.", Toast.LENGTH_SHORT).show();
                // Redirigir al inicio de sesión si no hay userId
                Intent intent = new Intent(UserProfileActivity.this, SignInActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });
    }
}

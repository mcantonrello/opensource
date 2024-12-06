package com.example.foodmatch;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class EditPasswordActivity extends AppCompatActivity {

    private EditText newPasswordEditText, confirmNewPasswordEditText;
    private Button savePasswordButton;
    private DatabaseReference usersDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_password);

        // Initialize Firebase database reference
        usersDatabase = FirebaseDatabase.getInstance().getReference("users");

        newPasswordEditText = findViewById(R.id.newPasswordEditText);
        confirmNewPasswordEditText = findViewById(R.id.confirmNewPasswordEditText);
        savePasswordButton = findViewById(R.id.savePasswordButton);

        savePasswordButton.setOnClickListener(v -> {
            String newPassword = newPasswordEditText.getText().toString();
            String confirmNewPassword = confirmNewPasswordEditText.getText().toString();

            if (newPassword.equals(confirmNewPassword)) {
                // Retrieve userId from SharedPreferences
                SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
                String userId = sharedPreferences.getString("userId", null);

                if (userId != null) {
                    // Update the password in Firebase
                    usersDatabase.child(userId).child("password").setValue(newPassword)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Toast.makeText(EditPasswordActivity.this, "Password updated", Toast.LENGTH_SHORT).show();
                                    finish();
                                } else {
                                    Toast.makeText(EditPasswordActivity.this, "Failed to update password", Toast.LENGTH_SHORT).show();
                                }
                            });
                } else {
                    Toast.makeText(EditPasswordActivity.this, "User not found", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(EditPasswordActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

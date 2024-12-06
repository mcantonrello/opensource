package com.example.foodmatch;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity {

    private EditText emailEditText, passwordEditText, confirmPasswordEditText;
    private Button signUpButton, loginButton;
    private DatabaseReference usersDatabase;

    private static final String TAG = "SignUpActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        Log.d(TAG, "onCreate: Initializing views and database reference");

        usersDatabase = FirebaseDatabase.getInstance().getReference("users");

        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText);
        signUpButton = findViewById(R.id.signUpButton);
        loginButton = findViewById(R.id.loginButton);

        signUpButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString();
            String password = passwordEditText.getText().toString();
            String confirmPassword = confirmPasswordEditText.getText().toString();

            Log.d(TAG, "Sign up button clicked");

            if (password.equals(confirmPassword)) {
                Log.d(TAG, "Passwords match, checking if email is unique");
                checkEmailUnique(email, () -> {
                    Log.d(TAG, "Email is unique, registering user");
                    registerUser(email, password);
                    Log.d(TAG, "Registered User");
                });
            } else {
                Log.d(TAG, "Passwords do not match");
                Toast.makeText(SignUpActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            }
        });

        loginButton.setOnClickListener(v -> {
            Log.d(TAG, "Login button clicked, switching to SignInActivity");
            Intent intent = new Intent(SignUpActivity.this, SignInActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void checkEmailUnique(String email, Runnable onSuccess) {
        Log.d(TAG, "Checking if email is unique: " + email);

        usersDatabase.orderByChild("email").equalTo(email).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d(TAG, "Email check successful");
                if (task.getResult().getChildrenCount() == 0) {
                    Log.d(TAG, "Email is unique, proceeding with registration");
                    onSuccess.run();
                } else {
                    Log.d(TAG, "Email already registered");
                    Toast.makeText(SignUpActivity.this, "Email already registered", Toast.LENGTH_SHORT).show();
                }
            } else {
                Log.e(TAG, "Error checking email uniqueness", task.getException());
                Toast.makeText(SignUpActivity.this, "Error checking email", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            Log.e(TAG, "Failed to connect to Firebase", e);
            Toast.makeText(SignUpActivity.this, "Failed to connect to database", Toast.LENGTH_SHORT).show();
        });
    }


    private void registerUser(String email, String password) {
        Log.e(TAG, "Registering user");

        String userId = usersDatabase.push().getKey();

        if (userId == null) {
            Log.e(TAG, "Error generating userId");
            Toast.makeText(SignUpActivity.this, "Error generating userId", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d(TAG, "Generated userId: " + userId);

        User user = new User(userId, email, password);
        Log.d(TAG, "Registering user with email: " + email);

        usersDatabase.child(userId).setValue(user).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                UserUtils.saveUserId(this, userId);
                Log.d(TAG, "User registered successfully");
                Toast.makeText(SignUpActivity.this, "Registration successful", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(SignUpActivity.this, SignInActivity.class));
                finish();
            } else {
                Log.e(TAG, "Registration failed", task.getException());
                Toast.makeText(SignUpActivity.this, "Registration error", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

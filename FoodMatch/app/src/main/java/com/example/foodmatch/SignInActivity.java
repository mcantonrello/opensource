package com.example.foodmatch;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class SignInActivity extends AppCompatActivity {

    private static final String TAG = "SignInActivity";
    private static final int RC_SIGN_IN = 100;

    private EditText emailEditText, passwordEditText;
    private Button signInButton, signUpButton, googleSignInButton;
    private CheckBox termsCheckBox;

    private DatabaseReference usersDatabase;
    private GoogleSignInClient googleSignInClient;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        // Initialize FirebaseAuth and DatabaseReference
        firebaseAuth = FirebaseAuth.getInstance();
        usersDatabase = FirebaseDatabase.getInstance().getReference("users");

        // Configure Google Sign-In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, gso);

        // Initialize UI components
        signInButton = findViewById(R.id.signInButton1);
        signUpButton = findViewById(R.id.signUpButton1);
        googleSignInButton = findViewById(R.id.googleSignInButton1);
        termsCheckBox = findViewById(R.id.termsCheckBox1);
        emailEditText = findViewById(R.id.emailEditText1);
        passwordEditText = findViewById(R.id.passwordEditText1);

        signInButton.setOnClickListener(v -> {
            if (termsCheckBox.isChecked()) {
                String email = emailEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                loginUser(email, password);
            } else {
                Toast.makeText(SignInActivity.this, "Please accept the terms and conditions", Toast.LENGTH_SHORT).show();
            }
        });

        signUpButton.setOnClickListener(v -> {
            startActivity(new Intent(SignInActivity.this, SignUpActivity.class));
        });

        googleSignInButton.setOnClickListener(v -> {
            Log.d(TAG, "Google Sign-In button clicked");
            signInWithGoogle();
        });
    }

    private void signInWithGoogle() {
        Log.d(TAG, "Starting Google Sign-In process");
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Log.d(TAG, "Received result from Google Sign-In");
            if (data != null) {
                Log.d(TAG, "Data from Google Sign-In is not null");
            } else {
                Log.e(TAG, "Google Sign-In data is null");
            }
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(Exception.class);
            if (account != null) {
                Log.d(TAG, "Google Sign-In successful, account email: " + account.getEmail());
                firebaseAuthWithGoogle(account);
            } else {
                Log.e(TAG, "Google Sign-In returned null account");
                Toast.makeText(this, "Google Sign-In failed. Account is null.", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e(TAG, "Google Sign-In failed: " + e.getMessage(), e);
            Toast.makeText(this, "Google Sign-In failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        Log.d(TAG, "Starting Firebase authentication with Google credentials");
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);

        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "Firebase authentication successful");
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        if (user != null) {
                            saveUserToFirebase(user);
                            Toast.makeText(SignInActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(SignInActivity.this, NavigationActivity.class));
                            finish();
                        } else {
                            Log.e(TAG, "FirebaseUser is null after successful authentication");
                        }
                    } else {
                        Log.e(TAG, "Firebase authentication failed", task.getException());
                        Toast.makeText(SignInActivity.this, "Authentication Failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void saveUserToFirebase(FirebaseUser firebaseUser) {
        String userId = firebaseUser.getUid();
        String email = firebaseUser.getEmail();

        Log.d(TAG, "Saving user to Firebase: userId=" + userId + ", email=" + email);

        User user = new User(userId, email, ""); // Password is not required for Google sign-in
        usersDatabase.child(userId).setValue(user).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d(TAG, "User saved successfully to Firebase");
            } else {
                Log.e(TAG, "Error saving user to Firebase", task.getException());
            }
        });

        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("userId", userId);
        editor.apply();
    }



    private void loginUser(String email, String password) {
        Query query = usersDatabase.orderByChild("email").equalTo(email);
        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult().getChildrenCount() > 0) {
                for (DataSnapshot userSnapshot : task.getResult().getChildren()) {
                    User user = userSnapshot.getValue(User.class);
                    if (user != null && user.password.equals(password)) {
                        UserUtils.saveUserId(this, user.userId);
                        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("userId", user.userId);
                        editor.apply();

                        Toast.makeText(SignInActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(SignInActivity.this, NavigationActivity.class));
                        finish();
                    } else {
                        Toast.makeText(SignInActivity.this, "Incorrect password", Toast.LENGTH_SHORT).show();
                    }
                }
            } else {
                Toast.makeText(SignInActivity.this, "User not found", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

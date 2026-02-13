package lk.tgcompany.stylesync.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import lk.tgcompany.stylesync.R;

// Import Firebase libraries
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;


import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;

public class RegisterActivity extends AppCompatActivity {

    // Declare UI components
    private EditText nameField, emailField, passwordField;
    private CheckBox isAdminBox;
    private Button btnRegister;
    private TextView goToLoginText;

    // Firebase instances
    private FirebaseAuth mAuth;
    private FirebaseFirestore fStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        // Initialize UI components
        nameField = findViewById(R.id.nameField);
        emailField = findViewById(R.id.emailField);
        passwordField = findViewById(R.id.passwordField);
        isAdminBox = findViewById(R.id.isAdminBox);
        btnRegister = findViewById(R.id.btnRegister);

        goToLoginText = findViewById(R.id.loginRedirectText);

        goToLoginText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent loginIntent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(loginIntent);
            }
        });

        btnRegister.setOnClickListener(v -> {
            try {
                registerUserLogic();
            } catch (Exception e) {
                // High-level error handling
                Log.e("RegisterError", "Error: " + e.getMessage());
                Toast.makeText(this, "An unexpected error occurred!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void registerUserLogic() {
        String name = nameField.getText().toString().trim();
        String email = emailField.getText().toString().trim();
        String password = passwordField.getText().toString().trim();

        // 1. Validations (Security & Quality Filter)
        if (TextUtils.isEmpty(name)) {
            nameField.setError("Name is required");
            return;
        }
        if (TextUtils.isEmpty(email)) {
            emailField.setError("Email is required");
            return;
        }
        if (password.length() < 6) {
            passwordField.setError("Password must be at least 6 characters");
            return;
        }

        // 2. Firebase Auth - Create User
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // User created successfully in Auth
                        String userId = mAuth.getCurrentUser().getUid();
                        saveUserToFirestore(userId, name, email,password);
                    } else {
                        // Error during Auth process
                        Toast.makeText(RegisterActivity.this, "Auth Failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void saveUserToFirestore(String userId, String name, String email,String password) {
        // 3. Prepare Data for Firestore
        Map<String, Object> user = new HashMap<>();
        user.put("fullName", name);
        user.put("email", email);
        user.put("password", password);

        // Filter: Assign Role based on CheckBox
        if (isAdminBox.isChecked()) {
            user.put("role", "admin");
        } else {
            user.put("role", "customer");
        }

        // 4. Writing to Database with Success/Failure listeners
        fStore.collection("Users").document(userId)
                .set(user)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(RegisterActivity.this, "Success! Welcome to StyleSync", Toast.LENGTH_SHORT).show();
                    // Redirect based on role (We will implement dashboards next)
                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                    finish();
                })
                .addOnFailureListener(e -> {
                    // Database write failure handling
                    Log.d("FirestoreError", e.getMessage());
                    Toast.makeText(RegisterActivity.this, "Database Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
package lk.tgcompany.stylesync.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import lk.tgcompany.stylesync.R;

import android.content.Intent;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {

    private EditText emailField, passwordField;
    private Button btnLogin;
    private FirebaseAuth mAuth;
    private FirebaseFirestore fStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        emailField = findViewById(R.id.loginEmail);
        passwordField = findViewById(R.id.loginPassword);
        btnLogin = findViewById(R.id.btnLogin);

        btnLogin.setOnClickListener(v -> {
            String email = emailField.getText().toString().trim();
            String password = passwordField.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // Start Login process
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnSuccessListener(authResult -> {
                        // Check user role after successful login
                        checkUserRole(authResult.getUser().getUid());
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(LoginActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        });
    }

    private void checkUserRole(String uid) {
        DocumentReference df = fStore.collection("Users").document(uid);
        df.get().addOnSuccessListener(documentSnapshot -> {
            try {
                String role = documentSnapshot.getString("role");
                if ("admin".equals(role)) {
                    // Redirect to Admin Side
                    startActivity(new Intent(getApplicationContext(), AdminDashboardActivity.class));
                } else {
                    // Redirect to Customer Side
                    startActivity(new Intent(getApplicationContext(), CustomerMainActivity.class));
                }
                finish();
            } catch (Exception e) {
                Log.e("RoleError", e.getMessage());
            }
        });
    }
}
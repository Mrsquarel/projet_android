package com.example.dhawini;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


public class CompanyLoginActivity extends AppCompatActivity {

    private TextInputEditText etEmail, etPassword;
    private Button btnLogin;
    private ProgressBar progressBar;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company_login);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        initViews();
        setupListeners();
    }

    private void initViews() {
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);
        progressBar = findViewById(R.id.progress_bar);
    }

    private void setupListeners() {
        btnLogin.setOnClickListener(v -> {
            String email = "";
            if (etEmail.getText() != null) {
                email = etEmail.getText().toString().trim();
            }
            String password = "";
            if (etPassword.getText() != null) {
                password = etPassword.getText().toString().trim();
            }

            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(CompanyLoginActivity.this, "Email invalide", Toast.LENGTH_SHORT).show();
                return;
            }
            if (password.isEmpty()) {
                Toast.makeText(CompanyLoginActivity.this, "Mot de passe requis", Toast.LENGTH_SHORT).show();
                return;
            }
            attemptLogin(email, password);
        });
    }

    private void attemptLogin(String email, String password) {
        progressBar.setVisibility(View.VISIBLE);

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user == null) {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(CompanyLoginActivity.this, "Authentication échouée.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        String userId = user.getUid();
                        DocumentReference docRef = db.collection("approved_installers").document(userId);

                        docRef.get().addOnSuccessListener(documentSnapshot -> {
                            progressBar.setVisibility(View.GONE);
                            if (documentSnapshot.exists()) {
                                // User profile found!
                                String companyName = documentSnapshot.getString("company_name");
                                String numAgr = documentSnapshot.getString("num_agrement");
                                String companyEmail = documentSnapshot.getString("email");

                                // SUCCESS → Save session
                                SharedPreferences prefs = getSharedPreferences("company_prefs", MODE_PRIVATE);
                                prefs.edit()
                                        .putString("company_email", companyEmail)
                                        .putString("company_name", companyName)
                                        .putString("num_agrement", numAgr)
                                        .putBoolean("is_logged_in", true)
                                        .apply();

                                startActivity(new Intent(this, CompanyDashboardActivity.class));
                                finish();
                            } else {
                                // User is in Auth but not in approved_installers collection.
                                Toast.makeText(this, "Utilisateur authentifié mais non approuvé.", Toast.LENGTH_SHORT).show();
                                FirebaseAuth.getInstance().signOut(); // Log out
                            }
                        }).addOnFailureListener(e -> {
                            progressBar.setVisibility(View.GONE);
                            Log.e("FirestoreError", "Error fetching user details", e);
                            Toast.makeText(this, "Erreur de base de données: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            FirebaseAuth.getInstance().signOut();
                        });

                    } else {
                        // If sign in fails.
                        progressBar.setVisibility(View.GONE);
                        Log.w("AuthError", "signInWithEmail:failure", task.getException());
                        Toast.makeText(CompanyLoginActivity.this, "Authentication échouée.",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
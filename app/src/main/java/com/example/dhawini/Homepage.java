package com.example.dhawini;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button; // Don't forget this import!
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.google.firebase.analytics.FirebaseAnalytics;
import android.view.View;
import android.widget.Toast;


public class Homepage extends AppCompatActivity {
    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_homepage);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.ivLogo), (v, insets) -> {

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        Button btnClient = findViewById(R.id.btnClient);
        btnClient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnClient.setOnClickListener(view -> {
                    Toast.makeText(Homepage.this, "Button Clicked!", Toast.LENGTH_SHORT).show();
                    logClientButtonClick();
                });

                logClientButtonClick(); // Log to Firebase
            }
        });

            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        findViewById(R.id.btnClient).setOnClickListener(v ->
                startActivity(new Intent(this, QuickAssessementActivity.class)));

        findViewById(R.id.btnCompany).setOnClickListener(v ->
                startActivity(new Intent(this, CompanyLoginActivity.class)));

        findViewById(R.id.btnAdmin).setOnClickListener(v ->
                startActivity(new Intent(this, AdminLoginActivity.class)));
    }

    // THIS function should be OUTSIDE onCreate, but INSIDE the class
    private void logClientButtonClick() {
        Bundle bundle = new Bundle();
        bundle.putString("button_name", "btnClient");
        mFirebaseAnalytics.logEvent("client_button_clicked", bundle);
    }
}

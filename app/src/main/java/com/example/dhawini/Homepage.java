package com.example.dhawini;

import android.content.Intent;
import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.google.firebase.analytics.FirebaseAnalytics;


public class Homepage extends AppCompatActivity {
    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_homepage);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.ivLogo), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        findViewById(R.id.btnClient).setOnClickListener(v -> {
            logClientButtonClick();
            startActivity(new Intent(this, QuickAssessementActivity.class));
        });

        findViewById(R.id.btnCompany).setOnClickListener(v ->
                startActivity(new Intent(this, CompanyLoginActivity.class)));

        findViewById(R.id.btnAdmin).setOnClickListener(v ->
                startActivity(new Intent(this, AdminLoginActivity.class)));
    }

    private void logClientButtonClick() {
        Bundle bundle = new Bundle();
        bundle.putString("button_name", "btnClient");
        mFirebaseAnalytics.logEvent("client_button_clicked", bundle);
    }
}

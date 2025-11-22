package com.example.dhawini;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class CompanyDashboardActivity extends AppCompatActivity {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private TextView tvNewLeads, tvQuotesSent, tvActiveProjects, tvMonthlyRevenue;
    private LeadAdapter leadAdapter;
    private String companyName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company_dashboard);

        SharedPreferences prefs = getSharedPreferences("company_prefs", MODE_PRIVATE);
        companyName = prefs.getString("company_name", null);

        initViews();
        if (companyName != null) {
            loadDashboardData();
        }
    }

    private void initViews() {
        tvNewLeads = findViewById(R.id.tv_new_leads);
        tvQuotesSent = findViewById(R.id.tv_quotes_sent);
        tvActiveProjects = findViewById(R.id.tv_active_projects);
        tvMonthlyRevenue = findViewById(R.id.tv_monthly_revenue);

        RecyclerView rvLeads = findViewById(R.id.rv_priority_leads);
        rvLeads.setLayoutManager(new LinearLayoutManager(this));
        leadAdapter = new LeadAdapter(new ArrayList<>());
        rvLeads.setAdapter(leadAdapter);
    }

    private void loadDashboardData() {
        // Load KPIs
        db.collection("leads")
            .whereEqualTo("status", "new")
            .whereEqualTo("company_name", companyName)
            .get()
            .addOnSuccessListener(querySnapshot -> tvNewLeads.setText(String.valueOf(querySnapshot.size())));

        db.collection("quotes")
            .whereEqualTo("company_name", companyName)
            .get()
            .addOnSuccessListener(querySnapshot -> tvQuotesSent.setText(String.valueOf(querySnapshot.size())));

        db.collection("projects")
            .whereEqualTo("status", "active")
            .whereEqualTo("company_name", companyName)
            .get()
            .addOnSuccessListener(querySnapshot -> tvActiveProjects.setText(String.valueOf(querySnapshot.size())));

        db.collection("projects")
            .whereEqualTo("status", "completed")
            .whereEqualTo("company_name", companyName)
            .get()
            .addOnSuccessListener(querySnapshot -> {
                int revenue = querySnapshot.size() * 2500000; // Mock: 2.5M TND per project
                tvMonthlyRevenue.setText(getString(R.string.revenue_format, revenue));
            });

        // Load Priority Leads
        db.collection("leads")
            .whereEqualTo("priority", "high")
            .whereEqualTo("company_name", companyName)
            .get()
            .addOnSuccessListener(querySnapshot -> {
                List<Lead> leads = new ArrayList<>();
                for (DocumentSnapshot doc : querySnapshot) {
                   try {
                        Lead lead = new Lead();
                        // Safely get each field, checking for existence and correct type
                        lead.name = doc.contains("name") ? doc.getString("name") : "";
                        lead.status = doc.contains("status") ? doc.getString("status") : "";
                        lead.priority = doc.contains("priority") ? doc.getString("priority") : "";
                        leads.add(lead);
                    } catch (Exception e) {
                        Log.e("FirestoreParsing", "Error parsing lead document: " + doc.getId(), e);
                        // This will log the error and skip the bad document instead of crashing
                    }
                }
                leadAdapter.updateLeads(leads);
            })
            .addOnFailureListener(e -> {
                Log.e("FirestoreQuery", "Failed to load priority leads.", e);
            });
    }
}

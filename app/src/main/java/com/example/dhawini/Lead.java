package com.example.dhawini;

import androidx.annotation.Keep;

@Keep // This is to prevent Proguard from removing it as it's used by Firestore reflection
public class Lead {
    public String name = "";
    public String status = "";
    public String priority = "";

    public Lead() {
        // Default constructor required for calls to
        // DocumentSnapshot.toObject(Lead.class)
    }
}
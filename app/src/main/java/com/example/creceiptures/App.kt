package com.example.creceiptures

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class App {
    companion object {
        var firebaseAuth: FirebaseAuth? = null
        var firestore: FirebaseFirestore? = null
    }
}
package com.example.creceiptures

import android.app.ActionBar
import android.app.Dialog
import android.app.NotificationManager
import android.content.Context
import android.widget.Button
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore

class App {
    companion object {
        var firebaseAuth: FirebaseAuth? = null
        var firestore: FirebaseFirestore? = null
        var userCollection: CollectionReference? = null

//        fun openReviewDialog(context: Context, itemId: String) {
//            val dialog = Dialog(context)
//
//            dialog.setContentView(R.layout.dialog_add_review)
//
//            val window = dialog.window
//            window?.setLayout(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT)
//
//            dialog.findViewById<Button>(R.id.close).setOnClickListener {
//                dialog.dismiss()
//            }
//
//            dialog.findViewById<Button>(R.id.submit).setOnClickListener {
//                // TODO: Finish implementing using given pseudocode
//            }
//
//            dialog.show()
//        }
    }
}
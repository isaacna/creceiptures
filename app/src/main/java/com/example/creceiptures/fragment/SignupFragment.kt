package com.example.creceiptures.fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import kotlinx.android.synthetic.main.fragment_signup.*
import com.google.firebase.auth.AuthResult
import com.google.android.gms.tasks.Task
import android.util.Log
import com.example.creceiptures.App
import com.example.creceiptures.R
import com.example.creceiptures.activity.MainActivity
import com.google.firebase.Timestamp


@SuppressLint("ValidFragment")
class SignupFragment(context: Context): Fragment() {

    private var parentContext = context
    private val logTag: String = this.javaClass.toString()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return layoutInflater.inflate(R.layout.fragment_signup, container, false)
    }

    override fun onStart() {
        super.onStart()

        create_account.setOnClickListener {
            val email = email.text.toString()
            val username = username.text.toString()
            val password = password.text.toString()

            // if non-empty valid inputs, try to create a new user via Firebase Auth
            if (email != "" && username != "" && password != "") {
                App.firebaseAuth?.createUserWithEmailAndPassword(email, password)
                    ?.addOnCompleteListener { task: Task<AuthResult> ->
                        if (task.isSuccessful) {
                            // Create a new user with a first and last name
                            val user: HashMap<String, Any> = HashMap()
                            user.put("email", email)
                            user.put("username", username)

                            // Add a new document with a generated ID
                            App.firestore?.collection("user")
                                ?.document(email)
                                ?.set(user)
                                ?.addOnSuccessListener { documentReference ->
                                    Log.d(
                                        logTag,
                                        "DocumentSnapshot added with ID: " + email
                                    )
                                }
                                ?.addOnFailureListener { e -> Log.w(logTag, "Error adding document", e) }

                            // send info to update user info in AddPetActivity
                            val intent = Intent(parentContext, MainActivity::class.java)
                            intent.putExtra("email", email)
                            activity!!.setResult(Activity.RESULT_OK, intent)
                            activity!!.finish()

                        } else {
                            Toast.makeText(
                                parentContext, "Authentication failed",
                                Toast.LENGTH_SHORT
                            ).show()
                            System.out.println(task.exception.toString())
                        }
                    }
            } else {
                Toast.makeText(parentContext, "Must fill all fields", Toast.LENGTH_SHORT).show()
            }
        }
    }

}

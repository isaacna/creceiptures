package com.example.creceiptures.fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_signin.*
import android.widget.Toast
import com.example.creceiptures.App
import com.example.creceiptures.R
import com.example.creceiptures.activity.MainActivity


@SuppressLint("ValidFragment")
class SigninFragment(context: Context): Fragment() {

    private var parentContext = context
    private val logTag = this.javaClass.toString()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return layoutInflater.inflate(R.layout.fragment_signin, container, false)
    }

    override fun onStart() {
        super.onStart()

        sign_in.setOnClickListener {
            Log.d("Ellen", "logging in?")

            val email = email.text.toString()
            val password = password.text.toString()

            // TODO: Implement sign in with email and password; if user authenticates successfully, finish activity; else make a toast
            App.firebaseAuth?.signInWithEmailAndPassword(email, password)
                ?.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        val user = App.firebaseAuth?.currentUser

                        // send info to update user info in Mainactivity
                        val intent = Intent(parentContext, MainActivity::class.java)
                        intent.putExtra("email", user?.email)
                        activity!!.setResult(Activity.RESULT_OK, intent)
                        activity!!.finish()
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(logTag, "signInWithEmail:failure", task.exception)
                        Toast.makeText(
                            activity, "Authentication failed.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                }
        }
    }
}
package com.example.creceiptures.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridView
import android.widget.TextView
import android.widget.Toast
import com.example.creceiptures.App
import com.example.creceiptures.R
import com.example.creceiptures.activity.MainActivity
import com.example.creceiptures.adapter.GridAdapter
import com.example.creceiptures.model.cReceiptureInGrid

@SuppressLint("ValidFragment")
class HomeFragment(context: Context): Fragment() {
    private var parentContext = context
    private var initialized: Boolean = false
    private var userSet: Boolean = false
    var adapter: GridAdapter? = null
    var pets: ArrayList<cReceiptureInGrid> = ArrayList<cReceiptureInGrid>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.d("Ellen", "HomeFragment onCreateView")
        // Inflate the layout for this fragment
        val view: View =  inflater.inflate(R.layout.fragment_home, container, false)

        adapter = GridAdapter(this.context!!, pets, parentContext as MainActivity)
        (view.findViewById<GridView>(R.id.gridView))?.adapter = adapter

        return view
    }

    override fun onStart() {
        super.onStart()

        Log.d("Ellen", "HomeFragment onStart")

        if (!this.initialized) {
            val fm = fragmentManager
            val ft = fm?.beginTransaction()

            this.initialized = true
        }
    }

    override fun onResume() {
        super.onResume()

        Log.d("Ellen", "HomeFragment onResume")


        if (userSet == true) {
            Log.d("Ellen", "HomeFragment onResume: userSet == true")
            return
        }

//        val view = getView()
        if (App.firebaseAuth != null && App.firebaseAuth?.currentUser != null ) {
            val test = App.firebaseAuth?.currentUser?.email
            // get current user's username
            val user = App.firestore?.collection("user")?.document(App.firebaseAuth?.currentUser?.email!!)
            user?.get()?.addOnCompleteListener { task ->
                Log.d("Ellen", "HomeFragment onResume: got user from firestore!")
                val username = task.result!!.data!!["username"] as String
                loadPets(username)
                view!!.findViewById<TextView>(R.id.title_bar).text = "${username}'s pets"
            }
            userSet = true
        }


    }

    private fun loadPets(username: String) {
        App.firestore?.collection("cReceipture")?.whereEqualTo("owner_curr", username)
            ?.get()
            ?.addOnSuccessListener { result ->
                for (document in result) {
                    pets.add(cReceiptureInGrid(
                        document.id,
                        document.data["name"] as String,
                        (document.data["value"] as Long).toInt(),
                        Uri.parse(document.data["imgUri"] as String)))
                }
                adapter?.notifyDataSetChanged()
            }
            ?.addOnFailureListener { exception ->
                Log.d("Ellen", "Error getting pets: ", exception)
            }
    }

}
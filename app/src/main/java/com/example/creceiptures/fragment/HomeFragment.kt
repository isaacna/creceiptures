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
import com.example.creceiptures.adapter.GridAdapter
import com.example.creceiptures.model.cReceiptureInGrid

@SuppressLint("ValidFragment")
class HomeFragment(context: Context): Fragment() {
    private var parentContext = context
    private var initialized: Boolean = false
    private var userSet: Boolean = false
//    private var listener: OnFragmentInteractionListener? = null
    var adapter: GridAdapter? = null
    var pets: ArrayList<cReceiptureInGrid> = ArrayList<cReceiptureInGrid>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.d("Ellen", "HomeFragment onCreateView")
                // Inflate the layout for this fragment
        val view: View =  inflater.inflate(R.layout.fragment_home, container, false)

        adapter = GridAdapter(this.context!!, pets)
        (view.findViewById<GridView>(R.id.gridView))?.adapter = adapter

        return view
    }

    override fun onStart() {
        super.onStart()

        Log.d("Ellen", "HomeFragment onStart")

        if (!this.initialized) {
            val fm = fragmentManager
            val ft = fm?.beginTransaction()
//            ft?.add(R.id.list_holder, WhatsNewFragment(this.parentContext), "NEW_FRAG")
//            ft?.commit()

//            search_edit_text.setOnEditorActionListener { _, actionId, _ ->
//                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
//                    val searchText = search_edit_text.text
//                    search_edit_text.setText("")
//                    if (searchText.toString() == "") {
//                        val toast = Toast.makeText(this.parentContext, "Please enter text", Toast.LENGTH_SHORT)
//                        toast.setGravity(Gravity.CENTER, 0, 0)
//                        toast.show()
//                        return@setOnEditorActionListener true
//                    }
//                    else {
//                        return@setOnEditorActionListener false
//                    }
//                }
//
//                return@setOnEditorActionListener false
//            }

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

        val view = getView()
        if (App.firebaseAuth != null && App.firebaseAuth?.currentUser != null ) {
            val test = App.firebaseAuth?.currentUser?.email
            Log.d("Ellen", "HomeFragment onResume: userSet == false")
            // get current user's username
            val user = App.firestore?.collection("user")?.document(App.firebaseAuth?.currentUser?.email!!)
            user?.get()?.addOnCompleteListener { task ->
                Log.d("Ellen", "HomeFragment onResume: got user from firestore!")

                // check if multiplier and streak needs to be reset due to inactivity
                val username = task.result!!.data!!["username"] as String
                loadPets(username)
                view!!.findViewById<TextView>(R.id.title_bar).text = "${username}'s pets"
            }
            userSet = true
        }


    }

    private fun loadPets(username: String) {
        Log.d("Ellen", "HomeFragment loadPets")

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
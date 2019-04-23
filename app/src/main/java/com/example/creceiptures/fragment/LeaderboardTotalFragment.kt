package com.example.creceiptures.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.creceiptures.App

import com.example.creceiptures.R
import com.example.creceiptures.model.UserInLeaderboard
import com.google.firebase.firestore.Query

@SuppressLint("ValidFragment")
class LeaderboardTotalFragment(context: Context) : Fragment() {

    private val parentContext: Context = context
    private var adapter: ListAdapter? = null
    private var users: ArrayList<UserInLeaderboard> = ArrayList<UserInLeaderboard>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view: View =  inflater.inflate(R.layout.leaderboard_total_list, container, false)
        adapter = ListAdapter(this.context!!, 0, users)
        (view.findViewById<ListView>(R.id.listView))?.adapter = adapter

        return view
    }

    override fun onResume() {
        super.onResume()
        if (users.size == 0)
            loadUsers()
    }

    // note: would have to be more efficient in a larger scale.
    private fun loadUsers() {

        App.firestore?.collection("user")?.orderBy("totalPetCoin", Query.Direction.DESCENDING)
            ?.limit(15)
            ?.get()
            ?.addOnSuccessListener { result ->
                for (document in result) {
                    users.add(
                        UserInLeaderboard(
                            document.data["username"] as String,
                            (document.data["numPets"] as Long).toInt(),
                            (document.data["totalPetCoin"] as Long).toInt())
                    )
                }
                adapter?.notifyDataSetChanged()
            }
            ?.addOnFailureListener { exception ->
                Log.d("Ellen", "Error getting users: ", exception)
            }
    }

    // Adapter for the ListView for this leaderboard
    private class ListAdapter(context: Context, resource: Int, private val list: ArrayList<UserInLeaderboard>) :
        ArrayAdapter<UserInLeaderboard>(context, resource, list) {

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val user: UserInLeaderboard = list[position]

            val rowView = inflater.inflate(R.layout.leaderboard_total_list_item, parent, false)
            (rowView.findViewById(R.id.rank) as TextView).text = (position + 1).toString()
            (rowView.findViewById(R.id.user) as TextView).text = user.name
            (rowView.findViewById(R.id.total_petCoin) as TextView).text = user.totalPetCoin.toString()

            return rowView
        }
    }

}
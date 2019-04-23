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
import android.widget.*
import com.example.creceiptures.App

import com.example.creceiptures.R
import com.example.creceiptures.model.cReceiptureInLeaderboard
import com.google.firebase.firestore.Query
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.leaderboard_value_list_item.view.*

@SuppressLint("ValidFragment")
class LeaderboardValueFragment(context: Context) : Fragment() {

    private val parentContext: Context = context
    private var adapter: ListAdapter? = null
    private var pets: ArrayList<cReceiptureInLeaderboard> = ArrayList<cReceiptureInLeaderboard>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view: View =  inflater.inflate(R.layout.leaderboard_value_list, container, false)
        adapter = ListAdapter(this.context!!, 0, pets)
        (view.findViewById<ListView>(R.id.listView))?.adapter = adapter

        if (pets.size == 0) // hasn't queried firebase yet
        loadPets()

        return view
    }

    private fun loadPets() {
        App.firestore?.collection("cReceipture")?.orderBy("value", Query.Direction.DESCENDING)
            ?.limit(15)
            ?.get()
            ?.addOnSuccessListener { result ->
                for (document in result) {
                    pets.add(cReceiptureInLeaderboard(
                        document.data["name"] as String,
                        document.data["owner_curr"] as String,
                        (document.data["value"] as Long).toInt(),
                        Uri.parse(document.data["imgUri"] as String)))
                }
                adapter?.notifyDataSetChanged()
            }
            ?.addOnFailureListener { exception ->
                Log.d("Ellen", "Error getting pets: ", exception)
            }
    }

    // Adapter for the ListView for this leaderboard
    private class ListAdapter(context: Context, resource: Int, private val list: ArrayList<cReceiptureInLeaderboard>) :
        ArrayAdapter<cReceiptureInLeaderboard>(context, resource, list) {

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val pet: cReceiptureInLeaderboard = list[position]

            val rowView = inflater.inflate(R.layout.leaderboard_value_list_item, parent, false)
            Picasso.get()
                .load(pet.imgUri)
                .resizeDimen(R.dimen.leaderboard_img_size, R.dimen.leaderboard_img_size)
                .into(rowView.pet_icon)
            (rowView.findViewById(R.id.rank) as TextView).text = (position + 1).toString()
            (rowView.findViewById(R.id.pet_name) as TextView).text = pet.name
            (rowView.findViewById(R.id.owner) as TextView).text = pet.owner
            (rowView.findViewById(R.id.petCoin) as TextView).text = pet.value.toString()

            return rowView
        }
    }

}

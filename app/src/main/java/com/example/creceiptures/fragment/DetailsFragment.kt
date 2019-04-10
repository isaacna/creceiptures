package com.example.creceiptures.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.example.creceiptures.App

import com.example.creceiptures.R
import com.example.creceiptures.activity.MinigameActivity
import com.example.creceiptures.model.cReceipture
import com.google.android.gms.tasks.Task
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.creceipture_grid_item.view.*
import kotlin.math.sqrt

@SuppressLint("ValidFragment")
class DetailsFragment(context: Context, petId: String) : Fragment() {
    private var parentContext: Context = context
    private val petId: String = petId
    private lateinit var pet: cReceipture
//    private var listener: OnFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.d("Ellen", "DetailsFragment onCreateView")
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_details, container, false)
    }

    override fun onStart() {
        super.onStart()
        Log.d("Ellen", "DetailsFragment onStart")

        // get pet from firebase
        // https://medium.com/@scarygami/cloud-firestore-quicktip-documentsnapshot-vs-querysnapshot-70aef6d57ab3
        // https://firebase.google.com/docs/firestore/query-data/get-data
        val petDoc = App.firestore?.collection("cReceipture")?.document(petId)
        petDoc?.get()?.addOnCompleteListener { task: Task<DocumentSnapshot> ->
            if (task.isSuccessful) {
                Log.d("DetailsActivity", "${petId} successfully found")
                val data = task.result!!.data!!
                pet = cReceipture(petId,
                    data["name"] as String,
                    (data["value"] as Long).toInt(),
                    Uri.parse(data["imgUri"] as String),
                    data["owner_curr"] as String,
                    data["owner_og"] as String
                )
                Log.d("DetailsActivity", pet.toString())

                // update UI to reflect pet stats
                Picasso.get()
                    .load(pet.imgUri)
                    .resizeDimen(R.dimen.details_img_size, R.dimen.details_img_size)
                    .into(view!!.findViewById<ImageView>(R.id.img))
                view!!.findViewById<TextView>(R.id.name).text = pet.name
                view!!.findViewById<TextView>(R.id.value).text = "petCoin value: ${pet.value.toString()}"

                view!!.findViewById<Button>(R.id.minigame_button).setOnClickListener{
                    Log.d("DetailsActivity", "play button clicked")
                    val intent = Intent(context, MinigameActivity::class.java)
                    intent.putExtra("PET", pet)
                    context!!.startActivity(intent)
                }
            }
            else {
                Log.d("DetailsActivity", "failed to find pet ${petId}")
            }
        }
    }

//    override fun onAttach(context: Context) {
//        super.onAttach(context)
//        if (context is OnFragmentInteractionListener) {
//            listener = context
//        } else {
//            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
//        }
//    }

//    override fun onDetach() {
//        super.onDetach()
//        listener = null
//    }

}

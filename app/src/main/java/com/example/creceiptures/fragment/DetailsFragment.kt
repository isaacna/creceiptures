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
    private var pet: cReceipture? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_details, container, false)
    }

    override fun onStart() {
        super.onStart()

        // get pet from firebase
        val petDoc = App.firestore?.collection("cReceipture")?.document(petId)
        petDoc?.get()?.addOnCompleteListener { task: Task<DocumentSnapshot> ->
            if (task.isSuccessful) {
                val data = task.result!!.data!!
                pet = cReceipture(petId,
                    data["name"] as String,
                    (data["value"] as Long).toInt(),
                    Uri.parse(data["imgUri"] as String),
                    data["owner_curr"] as String,
                    data["owner_og"] as String
                )

                // update UI to reflect pet stats
                Picasso.get()
                    .load(pet?.imgUri)
                    .resizeDimen(R.dimen.details_img_size, R.dimen.details_img_size)
                    .into(view!!.findViewById<ImageView>(R.id.img))
                view!!.findViewById<TextView>(R.id.name).text = pet?.name
                view!!.findViewById<TextView>(R.id.value).text = "petCoin value: ${pet?.value.toString()}"

                view!!.findViewById<Button>(R.id.minigame_button).setOnClickListener{
                    val intent = Intent(context, MinigameActivity::class.java)
                    intent.putExtra("PET", pet)
                    context!!.startActivity(intent)
                }
            }
            else {
                Log.d("DetailsActivity", "failed to find pet ${petId}")
            }
        }

        // listen for updates, mainly to "value"
        petDoc?.addSnapshotListener{ snapshot: DocumentSnapshot?, e ->
            if (e != null) {
                Log.w("DetailsFragment", "Listen to pet snapshot failed.", e)
            }
            else if (snapshot != null && snapshot.exists()) {
                pet?.value = (snapshot?.get("value") as Long).toInt()
                view!!.findViewById<TextView>(R.id.value).text = "petCoin value: ${pet?.value.toString()}"
            } else {
                Log.d("DetailsFragment", "Current data: null")
            }
        }
    }

}

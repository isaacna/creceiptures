package com.example.creceiptures.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.example.creceiptures.R
import com.example.creceiptures.model.cReceiptureInGrid
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.creceipture_grid_item.view.*

class GridAdapter: BaseAdapter {
    var petList = ArrayList<cReceiptureInGrid>()
    var context: Context? = null

    constructor(context: Context, songlist: ArrayList<cReceiptureInGrid>) : super() {
        this.context = context
        this.petList = songlist
    }

    override fun getCount(): Int {
        return petList.size
    }

    override fun getItem(position: Int): Any {
        return petList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val pet = this.petList[position]

        var inflater = context!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        var petView = inflater.inflate(R.layout.creceipture_grid_item, null)

        // https://alexdunn.org/2018/06/20/android-kotlin-basics-lazy-loading-images-with-picasso/
        Picasso.with(context).isLoggingEnabled = true   // for debugging
        Picasso.with(context)
            .load(pet.imgUri)
            .into(petView.img)                        //Your image view object.
        petView.name.text = pet.name
        petView.value.text = pet.value.toString()

//         load up DetailsActivity with this song's info on click
//        petView.setOnClickListener {
//            val intent = Intent(context, DetailsActivity::class.java)
//            intent.putExtra("PET_ID", pet.id)
//            context!!.startActivity(intent)
//        }

        return petView
    }
}
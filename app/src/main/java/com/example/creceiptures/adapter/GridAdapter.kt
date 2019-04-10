package com.example.creceiptures.adapter

import android.content.Context
import android.content.Intent
import android.util.Log
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
    var listener: OnGridItemSelectListener? = null

    constructor(context: Context, songlist: ArrayList<cReceiptureInGrid>, listener: OnGridItemSelectListener) : super() {
        this.context = context
        this.petList = songlist
        this.listener = listener
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
        Picasso.get()
            .load(pet.imgUri)
            .resizeDimen(R.dimen.grid_img_size, R.dimen.grid_img_size)
            .into(petView.img)                        //Your image view object.
        petView.name.text = pet.name
        petView.value.text = pet.value.toString()

//         load up DetailsActivity with this song's info on click
        petView.setOnClickListener {
            Log.d("Ellen", "grid item clicked")
            listener?.onGridItemSelect(pet.id)
        }

        return petView
    }

    // so that AddPetActivity responds to a grid item click by switching to DetailsFragment
    interface OnGridItemSelectListener {
        fun onGridItemSelect(petId: String)
    }
}
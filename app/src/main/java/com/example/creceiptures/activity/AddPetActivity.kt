package com.example.creceiptures.activity

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.ImageView
import android.widget.TextView
import com.example.creceiptures.R
import com.example.creceiptures.utils.AsyncUtils
import java.io.*

class AddPetActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_pet)

//        val asyncUtils : AsyncUtils =  AsyncUtils()
//        val receipt = asyncUtils.loadReceipt()
//        System.out.println(receipt.toString())
//
//
//        val receiptFile : File = File("drawable/receipt.png")
//        System.out.println(receiptFile.name)
        selectImageInAlbum()
    }

    fun selectImageInAlbum() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        if (intent.resolveActivity(packageManager) != null) {
            startActivityForResult(intent,
                REQUEST_SELECT_IMAGE_IN_ALBUM
            )
        }
    }
    companion object {
        private val REQUEST_TAKE_PHOTO = 0
        private val REQUEST_SELECT_IMAGE_IN_ALBUM = 1
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        // Check which request we're responding to
        if (requestCode == REQUEST_SELECT_IMAGE_IN_ALBUM) {
            // Make sure the request was successful
            if (resultCode == Activity.RESULT_OK) {

//                if(data !=null) {
                    val uri : Uri = data!!.data!!
//                }
                try {
                    val v : ImageView = findViewById(R.id.test)
                    val bm: Bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri)
//                    v.setImageBitmap(bm)


                    var receiptFile : File = File(this.filesDir, "receipt.jpg")

//                    val os : OutputStream = BufferedOutputStream(FileOutputStream(receiptFile))
//                    bm.compress(Bitmap.CompressFormat.JPEG,100, os)
//                    os.close()

//                    v.setImageDrawable()

                    val t: TextView = findViewById(R.id.hello)
//                    t

                    val asyncUtils : AsyncUtils =  AsyncUtils(this)
                    val receipt = asyncUtils.loadReceipt(bm)
                    System.out.println("MAIN")
                    System.out.println("RECEIPT")
                    System.out.println(receipt.value!!.getTotal())


                }
                catch (e: IOException) {
                    System.out.println("ripppp")
                }
                // The user picked a contact.
                // The Intent's data Uri identifies which contact was selected.

                // Do something with the contact here (bigger example below)
            }
        }
    }
}

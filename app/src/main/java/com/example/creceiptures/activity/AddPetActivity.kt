package com.example.creceiptures.activity

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import com.example.creceiptures.App
import com.example.creceiptures.R
import com.example.creceiptures.model.cReceipture
import com.example.creceiptures.utils.AsyncUtils
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.squareup.picasso.Picasso
import java.io.*

class AddPetActivity : AppCompatActivity() {

    private val logTag: String = this.javaClass.toString()
    companion object {
        private val REQUEST_SELECT_IMAGE_IN_ALBUM = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_pet)

        selectImageInAlbum()
    }

    //get an image from camera roll
    private fun selectImageInAlbum() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        if (intent.resolveActivity(packageManager) != null) {
            startActivityForResult(intent,
                REQUEST_SELECT_IMAGE_IN_ALBUM
            )
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        // Check which request we're responding to
        if (requestCode == REQUEST_SELECT_IMAGE_IN_ALBUM) {
            // Make sure the request was successful
            if (resultCode == Activity.RESULT_OK) {

                //path to receipt image
                val receiptPath : Uri = data!!.data!!

                try {
                    //set image of receipt
                    val v : ImageView = findViewById(R.id.test)
                    val bm: Bitmap = MediaStore.Images.Media.getBitmap(contentResolver, receiptPath)
                    v.setImageBitmap(bm)

                    val t: TextView = findViewById(R.id.hello)

                    //fetch the receipt using async task
                    val asyncUtils : AsyncUtils =  AsyncUtils(this)
                    val receipt = asyncUtils.loadReceipt(bm).value

                    //get username
                    lateinit var username : String
                    if (App.firebaseAuth != null && App.firebaseAuth?.currentUser != null ) {
                        // get current user's username
                        val user = App.firestore?.collection("user")?.document(App.firebaseAuth?.currentUser?.email!!)
                        user?.get()?.addOnCompleteListener { task ->
                            username = task.result!!.data!!["username"] as String
                            t.setText(username)

                            //create unique url for fetching creceipture image
                            val merchant = receipt!!.getMerchant().replace(" ", "%20")
                            val uri : Uri = Uri.parse("https://robohash.org/" + receipt.getTotal() + merchant)

                            //load pet image into imageview
                            getPetImage(uri)

                            //set onclick listener for naming the pet then submitting it
                            val button : Button = findViewById(R.id.namePetButton)
                            button.setOnClickListener {
                                val petNameEditText : EditText = findViewById(R.id.petNameInput)
                                if(petNameEditText.text != null) {

                                    //create pet
                                    val name = petNameEditText.text.toString()
                                    val value = receipt.getTotal()
                                    val newPet : cReceipture = cReceipture("id", name, value.toInt(), uri, username, username )

                                    //add pet to firebase
                                    addPetToFirebase(newPet)

                                    //return to home activity
                                    this.finish()
                                }
                            }

                        }
                    }


                }
                catch (e: IOException) {
                    System.out.println("ripppp")
                }

            }
            else {
                this.finish()
            }
        }
    }

    private fun addPetToFirebase(cReceipture: cReceipture) {
        if (App.firebaseAuth != null && App.firebaseAuth?.currentUser != null ) {

            val data: HashMap<String, Any> = HashMap<String, Any>()
            data.put("name", cReceipture.name)
            data.put("owner_curr", cReceipture.owner_curr)
            data.put("owner_og", cReceipture.owner_og)
            data.put("value", cReceipture.value)
            data.put("imgUri", cReceipture.imgUri.toString())

            //get pet document and add new creceipture
            val petTable = App.firestore?.collection("cReceipture")
            petTable
                ?.document(cReceipture.name + "-" + cReceipture.owner_og)
                ?.set(data)
                ?.addOnSuccessListener { documentReference ->
                    Log.d(
                        logTag,
                        "DocumentSnapshot added"
                    )
                }
                ?.addOnFailureListener { e -> Log.w(logTag, "Error adding document", e) }

            //update user's total in firebase
            val userDoc = App.firestore?.collection(("user"))?.document(App.firebaseAuth?.currentUser?.email!!)
            userDoc?.get()?.addOnCompleteListener { task: Task<DocumentSnapshot> ->
                if (task.isSuccessful) {
                    Log.d("AddPetActivity", "user successfully found")
                    val oldTotalPetCoin: Long = task.result!!.data!!["totalPetCoin"] as Long
                    userDoc.update(
                        "totalPetCoin", (oldTotalPetCoin + cReceipture.value)
                    )
                    val oldPetCount: Long = task.result!!.data!!["numPets"] as Long
                    userDoc.update(
                        "numPets", (oldPetCount + 1)
                    )

                } else {
                    Log.d("AddPetActivity", "failed to find update user total petcoin")
                }
            }
        }
    }

    //load pet image
    private fun getPetImage(uri: Uri) {

        val petView : ImageView = findViewById(R.id.newGeneratedPet)

        try {
            Picasso.get().load(uri).into(petView)
        }
        catch(e: IOException) {
            System.out.println(e.stackTrace)
        }


    }
}

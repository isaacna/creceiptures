package com.example.creceiptures.activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.example.creceiptures.App
import com.example.creceiptures.R
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.squareup.picasso.Picasso

class TradeDecisionActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trade_decision)

        val isRequester : Boolean = intent.getBooleanExtra("isRequester", false)
        System.out.println("IS REQUESTER " + isRequester)

        val acceptButton : Button = findViewById(R.id.acceptButton)
        val declineButton : Button = findViewById(R.id.declineButton)
        val pendingView : TextView = findViewById(R.id.pendingText)

        val requester : String = intent.getStringExtra("requester")
        var requester_pet : String = intent.getStringExtra("requester_pet")
        var requester_pet_uri : String = intent.getStringExtra("requester_pet_uri")
        val accepter : String = intent.getStringExtra("accepter")
        var accepter_pet : String = intent.getStringExtra("accepter_pet")
        var accepter_pet_uri : String = intent.getStringExtra("accepter_pet_uri")

        val accepter_email :String = intent.getStringExtra("accepter_email")
        val requester_email : String = intent.getStringExtra("requester_email")

        val yourPetImage : ImageView = findViewById(R.id.yourPetImageDecision)
        val otherPetImage : ImageView = findViewById(R.id.otherPetImageDecision)

        val yourText : TextView = findViewById(R.id.yourPetNameDecision)
        val otherText : TextView = findViewById(R.id.otherPetNameDecision)


        if (isRequester) { //user is requester, set button to cancel request only
            acceptButton.visibility = View.GONE
            val cancel : String = "Cancel"
            val pending : String = "Pending"

            declineButton.text = cancel
            pendingView.text = pending

            declineButton.setOnClickListener {
                deleteTrade(accepter, accepter_pet, requester, requester_pet)
            }

            Picasso.get().load(requester_pet_uri).into(yourPetImage)
            Picasso.get().load(accepter_pet_uri).into(otherPetImage)

            val textY = "Your " + requester_pet
            yourText.text = textY

            val textO = accepter + "'s " + accepter_pet
            otherText.text = textO
        }
        else { //user is accepter, set accept or decline button
            val incoming : String = "Incoming Request"
            pendingView.text = incoming

            acceptButton.setOnClickListener {
                acceptTrade(accepter, accepter_email, accepter_pet, requester, requester_email, requester_pet)
            }

            declineButton.setOnClickListener {
                deleteTrade(accepter, accepter_pet, requester, requester_pet)
            }

            Picasso.get().load(accepter_pet_uri ).into(yourPetImage)
            Picasso.get().load(requester_pet_uri).into(otherPetImage)

            val textY = "Your " + accepter_pet
            yourText.text = textY

            val textO = requester + "'s " + requester_pet
            otherText.text = textO
        }

    }

    fun swapPetData(ownerA: String, owner_A_email : String, petA : String, ownerB : String, owner_B_email : String, petB: String) {

        //swap owner of pet A to owner B
        //get og owner of pet A to get document id for pet A
        App.firestore?.collection("cReceipture")
            ?.whereEqualTo("name", petA)
            ?.whereEqualTo("owner_curr", ownerA)
            ?.get()
            ?.addOnSuccessListener { result ->
                //get original owner
                var owner_og = ""
                for(document in result) {
                    owner_og = document.data["owner_og"] as String
                }

                //update petA document to set ownerB as the owner
                val petDocA = App.firestore?.collection("cReceipture")?.document(petA + "-" + owner_og)
                petDocA?.get()?.addOnCompleteListener {task: Task<DocumentSnapshot> ->
                    if (task.isSuccessful) {
//                        System.out.println("updating pet A ")
                        //swap owners
                        petDocA.update(
                            "owner_curr", ownerB
                        )

                        //get pet A value
                        val valueA = task.result!!.data!!["value"] as Long
                        System.out.println("A VAL " + valueA)

                        //get user B doc and add pet A value
                        val userB = App.firestore?.collection("user")?.document(owner_B_email)
                        userB?.get()?.addOnCompleteListener { task2: Task<DocumentSnapshot> ->
                            if(task2.isSuccessful) {
                                val oldTotalPetCoin = task2.result!!.data!!["totalPetCoin"] as Long
                                userB.update("totalPetCoin", oldTotalPetCoin + valueA)
                            }
                        }

                        //get user A doc and subtract pet A value
                        val userA = App.firestore?.collection("user")?.document(owner_A_email)
                        userA?.get()?.addOnCompleteListener { task2: Task<DocumentSnapshot> ->
                            if(task2.isSuccessful) {
                                val oldTotalPetCoin = task2.result!!.data!!["totalPetCoin"] as Long
                                userA.update("totalPetCoin", oldTotalPetCoin - valueA)
                            }
                        }

                    } else {
                        Log.d("TradeDecisionActivity", "failed to swap pet A owners")
                    }
                }

                //add pet A petcoin to user B
                //subtract pet A petcoin from user A
            }

        //swap owner of pet B
        //get og owner of pet B to get document id for pet B
        App.firestore?.collection("cReceipture")
            ?.whereEqualTo("name", petB)
            ?.whereEqualTo("owner_curr", ownerB)
            ?.get()
            ?.addOnSuccessListener { result ->
                //get original owner
                var owner_og = ""
                for(document in result) {
                    owner_og = document.data["owner_og"] as String
                }

                //update petB document to sett owner A as the owner
                val petDocB = App.firestore?.collection("cReceipture")?.document(petB + "-" + owner_og)
                petDocB?.get()?.addOnCompleteListener {task: Task<DocumentSnapshot> ->
                    if (task.isSuccessful) {
//                        System.out.println("updating pet B ")

                        petDocB.update(
                            "owner_curr", ownerA
                        )

                        //get pet B value
                        val valueB = task.result!!.data!!["value"] as Long

                        //get user B doc
                        val userB = App.firestore?.collection("user")?.document(owner_B_email)
                        userB?.get()?.addOnCompleteListener { task2: Task<DocumentSnapshot> ->
                            if(task2.isSuccessful) {
                                val oldTotalPetCoin = task2.result!!.data!!["totalPetCoin"] as Long
                                userB.update("totalPetCoin", oldTotalPetCoin - valueB)
                            }
                        }

                        val userA = App.firestore?.collection("user")?.document(owner_A_email)
                        userA?.get()?.addOnCompleteListener { task2: Task<DocumentSnapshot> ->
                            if(task2.isSuccessful) {
                                val oldTotalPetCoin = task2.result!!.data!!["totalPetCoin"] as Long
                                userA.update("totalPetCoin", oldTotalPetCoin + valueB)
                            }
                        }

                    } else {
                        Log.d("TradeDecisionActivity", "failed to swap pet B owners")
                    }
                }

                //add pet B petcoin to user A
                //subtract pet B petcoin from user B
            }

    }

    fun acceptTrade(accepter: String, accepter_email : String, accepter_pet : String, requester : String, requester_email : String, requester_pet: String) {
        //update trades
        deleteTrade(accepter, accepter_pet, requester, requester_pet)

        //switch ownership
        System.out.println("USER A = " + accepter)
        System.out.println("USER B = " + requester)
        swapPetData(accepter, accepter_email, accepter_pet, requester, requester_email, requester_pet)


        //adjust petcoin value of users


    }

    //delete from trades
    fun deleteTrade(accepter: String, accepter_pet : String, requester : String, requester_pet: String) {
        //update trades
        if (App.firebaseAuth != null && App.firebaseAuth?.currentUser != null ) {
            val documentPath = requester + "-" + requester_pet + "-for-" + accepter + "-" + accepter_pet
            App.firestore?.collection("trades")
                ?.document(documentPath)
                ?.delete()
        }

    }

}


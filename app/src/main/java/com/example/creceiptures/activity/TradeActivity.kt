package com.example.creceiptures.activity

import android.content.Context
import android.content.Intent
import android.media.Image
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.Layout
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.creceiptures.App
import com.example.creceiptures.R
import com.squareup.picasso.Picasso

class TradeActivity : AppCompatActivity() {

    var adapter : TradeArrayAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trade)

        if (App.firebaseAuth != null && App.firebaseAuth?.currentUser != null ) {
            val incomingButton: Button = this.findViewById(R.id.incomingTradesButton)
            val outgoingButton: Button = findViewById(R.id.outgoingTradeButton)

            val user = App.firestore?.collection("user")?.document(App.firebaseAuth?.currentUser?.email!!)
            user?.get()?.addOnCompleteListener { task ->
                val username = task.result!!.data!!["username"] as String

                //when you want to load incoming trades
                incomingButton.setOnClickListener {
                    loadIncomingTrades(username)
                }

                //when you want to load outgoing trades
                outgoingButton.setOnClickListener {
                    loadOutgoingTrades(username)
                }
            }
        }
    }

    //loading incoming trades
    fun loadIncomingTrades(user : String) {
        App.firestore?.collection("trades")?.whereEqualTo("accepter",user)
            ?.get()
            ?.addOnSuccessListener { result ->
                val tradeArray = ArrayList<TradeItem>()
                for (document in result) {
                    val tradeItem = TradeItem(
                        document.data["requester"] as String,
                        document.data["requester_pet"] as String,
                        document.data["requester_pet_uri"] as String,
                        document.data["accepter"] as String,
                        document.data["accepter_pet"] as String,
                        document.data["accepter_pet_uri"] as String,
                        false)
                    tradeArray.add(tradeItem)
                }
                adapter = TradeArrayAdapter(this, R.id.tradeView, tradeArray)
                val lv : ListView = this.findViewById(R.id.tradeView)
                lv.adapter = adapter
            }
    }

    //loading outgoing trades
    fun loadOutgoingTrades(user : String) {
        App.firestore?.collection("trades")?.whereEqualTo("requester",user)
            ?.get()
            ?.addOnSuccessListener { result ->
                val tradeArray = ArrayList<TradeItem>()
                for (document in result) {
                    val tradeItem = TradeItem(
                        document.data["requester"] as String,
                        document.data["requester_pet"] as String,
                        document.data["requester_pet_uri"] as String,
                        document.data["accepter"] as String,
                        document.data["accepter_pet"] as String,
                        document.data["accepter_pet_uri"] as String,
                        true)
                    tradeArray.add(tradeItem)
                }
                adapter = TradeArrayAdapter(this, R.id.tradeView, tradeArray)
                val lv : ListView = findViewById(R.id.tradeView)
                lv.adapter = adapter
            }
    }

    //load request new trade activity
    fun requestNewTrade(view : View) {
        val intent: Intent = Intent(this, RequestTradeActivity::class.java)
        startActivity(intent)
    }

}

class TradeItem(requester: String, requester_pet : String, requester_pet_uri : String, accepter: String, accepter_pet: String, accepter_pet_uri : String, isRequester : Boolean) {
    val requester : String
    val requester_pet : String
    val requester_pet_uri: String
    val accepter: String
    val accepter_pet: String
    val accepter_pet_uri: String
    val isRequester : Boolean

    init {
        this.requester = requester
        this.requester_pet = requester_pet
        this.requester_pet_uri = requester_pet_uri
        this.accepter = accepter
        this.accepter_pet = accepter_pet
        this.accepter_pet_uri = accepter_pet_uri
        this.isRequester = isRequester
    }
}

class TradeArrayAdapter(context : Context, resource : Int, trades : ArrayList<TradeItem>) : ArrayAdapter<TradeItem> (context, resource, trades){

    val trades : ArrayList<TradeItem>
    init {
        this.trades = trades
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater : LayoutInflater = LayoutInflater.from(context)
        val view : View = inflater.inflate(R.layout.trade_in_listview, parent, false)

        val userPetName : TextView = view.findViewById(R.id.userPetName)
        val userPetView : ImageView = view.findViewById(R.id.userPetImage)
        val otherPetName : TextView = view.findViewById(R.id.otherPetName)
        val otherPetView : ImageView = view.findViewById(R.id.otherPetImage)

        val tradeItem = trades[position]

        //if user is requester for trade
        if(tradeItem.isRequester) {
            val userText = "Your " + tradeItem.requester_pet
            userPetName.text = userText
            val otherText = tradeItem.accepter + "'s " + tradeItem.accepter_pet
            otherPetName.text = otherText

            Picasso.get().load(tradeItem.requester_pet_uri).into(userPetView)
            Picasso.get().load(tradeItem.accepter_pet_uri).into(otherPetView)

            //make trade request view for requesters
            view.setOnClickListener {
                System.out.println("i am clicked!!!")
                //TODO
            }

        }
        //if user is accepter for trade
        else {
            //set text for pet trade
            val userText = "Your " + tradeItem.accepter_pet
            userPetName.text = userText
            val otherText = tradeItem.requester + "'s " + tradeItem.requester_pet
            otherPetName.text = otherText

            //set images for pet trade
            Picasso.get().load(tradeItem.accepter_pet_uri).into(userPetView)
            Picasso.get().load(tradeItem.requester_pet_uri).into(otherPetView)

            //make trade request view for accepters
            view.setOnClickListener {
                System.out.println("i am clicked!!!")
                //TODO
            }
        }



        return view
    }

    fun addTradeItem(tradeItem : TradeItem) {
        trades.add(tradeItem)
    }
}

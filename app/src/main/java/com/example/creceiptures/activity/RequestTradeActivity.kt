package com.example.creceiptures.activity

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.creceiptures.App
import com.example.creceiptures.R
import com.squareup.picasso.Picasso

class RequestTradeActivity : AppCompatActivity() {

    var otherAdapter : OthersArrayAdapter? = null
    var otherSpinner : Spinner? = null

    var userPetAdapter : PetArrayAdapter? = null
    var userPetSpinner : Spinner? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_request_trade)

        if (App.firebaseAuth != null && App.firebaseAuth?.currentUser != null ) {
            val user = App.firestore?.collection("user")?.document(App.firebaseAuth?.currentUser?.email!!)
            user?.get()?.addOnCompleteListener { task ->
                val username = task.result!!.data!!["username"] as String
                otherSpinner = this.findViewById(R.id.other_spinner_view)
                userPetSpinner = findViewById(R.id.user_pet_spinner_view)

                loadOthers(username)
                loadUserPets(username)


            }
        }

    }

    //load other users into spinner
    fun loadOthers(username : String) {
        App.firestore?.collection("user")
            ?.get()
            ?.addOnSuccessListener { result ->
                val othersArray  = ArrayList<String>()
                for (document in result) {
                    othersArray.add(document.data["username"] as String)
                }
                othersArray.remove(username)

                otherAdapter = OthersArrayAdapter(this, othersArray)
                otherSpinner!!.adapter = otherAdapter

                //https://stackoverflow.com/questions/46447296/android-kotlin-onitemselectedlistener-for-spinner-not-working
                otherSpinner?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                    override fun onNothingSelected(parent: AdapterView<*>?) {

                    }

                    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                        System.out.println("SPINS")
                        System.out.println(parent?.getItemAtPosition(position).toString())

                        //TODO loadOtherPets()

                    }

                }
            }
    }

    //dependent on selected of others spinner 
    fun loadOtherPets() {

    }

    //load user pets into spinner
    fun loadUserPets(username : String) {
        App.firestore?.collection("cReceipture")?.whereEqualTo("owner_curr", username)
            ?.get()
            ?.addOnSuccessListener { result ->
                val petArray = ArrayList<PetItem>()
                for (document in result) {
                    petArray.add(PetItem(
                        document.data["name"] as String,
                        document.data["imgUri"] as String,
                        (document.data["value"] as Long).toInt()
                    ))
                }

                userPetAdapter = PetArrayAdapter(this, petArray)
                userPetSpinner!!.adapter = userPetAdapter

            }
    }

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
                System.out.println(tradeArray.get(0).accepter_pet)
            }
    }
}

//http://developine.com/custom-spinner-with-imageview-in-android-kotlin-tutorial/
class OthersArrayAdapter(context: Context, others : ArrayList<String> ) : BaseAdapter() {
    val others : ArrayList<String>
    init {
        this.others = others
    }
    val inflater : LayoutInflater = LayoutInflater.from(context)


    override fun getView(position: Int, convertView: View?, parent: ViewGroup?) : View {

        val view : View = inflater.inflate(R.layout.spinner_item_other, parent, false)

        val otherName : TextView = view.findViewById(R.id.otherName)
        otherName.text = others[position]
        return view
    }

    override fun getItemId(position: Int): Long {

        return 0

    }

    override fun getCount(): Int {
        return others.size
    }

    override fun getItem(p : Int) : Any? {
        return this.others[p]
    }
}

class PetArrayAdapter( context: Context, others : ArrayList<PetItem> ) : BaseAdapter() {
    val pets : ArrayList<PetItem>
    init {
        this.pets = others
    }
    val inflater : LayoutInflater = LayoutInflater.from(context)


    override fun getView(position: Int, convertView: View?, parent: ViewGroup?) : View {

        val view : View = inflater.inflate(R.layout.spinner_item_pet, parent, false)

        val petName : TextView = view.findViewById(R.id.spinnerPetName)
        petName.text = pets[position].name

        val petValue : TextView = view.findViewById(R.id.spinnerPetValue)
        petValue.text = pets[position].value.toString()

        val petImage : ImageView = view.findViewById(R.id.spinnerPetImage)
        Picasso.get().load(pets[position].uri).into(petImage)
        return view
    }

    override fun getItemId(position: Int): Long {

        return 0

    }

    override fun getCount(): Int {
        return pets.size
    }

    override fun getItem(p : Int) : Any? {
        return pets[p]
    }
}

class PetItem(name : String, uri : String, value : Int) {
    val name : String
    val uri : String
    val value : Int
    init {
        this.name = name
        this.uri = uri
        this.value = value
    }
}
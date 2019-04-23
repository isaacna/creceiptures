package com.example.creceiptures.activity

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.creceiptures.App
import com.example.creceiptures.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_request_trade.*

class RequestTradeActivity : AppCompatActivity() {
    private val logTag: String = this.javaClass.toString()

    var otherAdapter : OthersArrayAdapter? = null
    var otherSpinner : Spinner? = null

    var userPetAdapter : PetArrayAdapter? = null
    var userPetSpinner : Spinner? = null

    var otherPetAdapter : PetArrayAdapter? = null
    var otherPetSpinner : Spinner? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_request_trade)

        if (App.firebaseAuth != null && App.firebaseAuth?.currentUser != null ) {
            val user = App.firestore?.collection("user")?.document(App.firebaseAuth?.currentUser?.email!!)
            user?.get()?.addOnCompleteListener { task ->
                val username = task.result!!.data!!["username"] as String
                otherSpinner = this.findViewById(R.id.other_spinner_view)
                userPetSpinner = findViewById(R.id.user_pet_spinner_view)
                otherPetSpinner = findViewById(R.id.other_pet_spinner_view)

                loadOthers(username)
                loadUserPets(username)

                val button : Button = findViewById(R.id.sendRequestButton)
                button.setOnClickListener {
                    sendRequest(username, App.firebaseAuth?.currentUser?.email!!)
                }
            }
        }

    }

    //load other users into spinner
    fun loadOthers(username : String) {
        App.firestore?.collection("user")
            ?.get()
            ?.addOnSuccessListener { result ->
                val othersArray  = ArrayList<OtherItem>()
                for (document in result) {
                    if(username != document.data["username"]) {
                        othersArray.add(OtherItem(
                                document.data["username"] as String,
                                document.data["email"] as String))
                    }
                }

                otherAdapter = OthersArrayAdapter(this, othersArray)
                otherSpinner!!.adapter = otherAdapter

                //https://stackoverflow.com/questions/46447296/android-kotlin-onitemselectedlistener-for-spinner-not-working
                otherSpinner?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                    override fun onNothingSelected(parent: AdapterView<*>?) {

                    }

                    //get username from first spinner
                    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                        val otherItemInSpinner = parent?.getItemAtPosition(position) as OtherItem
                        loadOtherPets(otherItemInSpinner.username)
                    }

                }
            }
    }

    //load pets dependent on selected of others spinner
    fun loadOtherPets(username : String) {
        App.firestore?.collection("creceipture")?.whereEqualTo("owner_curr", username)
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
                otherPetAdapter = PetArrayAdapter(this, petArray)
                otherPetSpinner!!.adapter = otherPetAdapter
            }
    }

    //load user pets into spinner
    fun loadUserPets(username : String) {
        App.firestore?.collection("creceipture")?.whereEqualTo("owner_curr", username)
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

    //submit trade request to firebase
    fun sendRequest(username : String, userEmail : String) {
        if(otherSpinner!!.selectedItem!=null && otherPetSpinner!!.selectedItem!=null && userPetSpinner!!.selectedItem != null) {
            System.out.println("SENDING TRADE REQUEST")

            //get vals from spinners
            val other : OtherItem = other_spinner_view.selectedItem as OtherItem
            val otherPet : PetItem = other_pet_spinner_view.selectedItem as PetItem
            val userPet : PetItem = user_pet_spinner_view.selectedItem as PetItem

            //prep trade data to insert
            val data: HashMap<String, Any> = HashMap<String, Any>()
            data.put("accepter", other.username)
            data.put("accepter_pet", otherPet.name)
            data.put("accepter_pet_uri", otherPet.uri)
            data.put("accepter_email", other.email)
            data.put("requester", username)
            data.put("requester_pet", userPet.name)
            data.put("requester_pet_uri", userPet.uri)
            data.put("requester_email", userEmail)

            val documentPath : String = username + "-" + userPet.name + "-for-" + other.username + "-" + otherPet.name

            val newTrade = App.firestore?.collection("trades")
            newTrade
                ?.document(documentPath)
                ?.set(data)
                ?.addOnSuccessListener { documentReference ->
                    Log.d(
                        logTag,
                        "DocumentSnapshot added"
                    )
                }
                ?.addOnFailureListener { e -> Log.w(logTag, "Error adding trade document", e) }
            this.finish()
        }
        else {
            //make toast if not all fields are full
            val duration = Toast.LENGTH_SHORT
            val toast = Toast.makeText(this, "Fill out all fields!", duration)
            toast.show()
        }
    }

}

//http://developine.com/custom-spinner-with-imageview-in-android-kotlin-tutorial/
//custom adapter for spinner
class OthersArrayAdapter(context: Context, others : ArrayList<OtherItem> ) : BaseAdapter() {
    val others : ArrayList<OtherItem>
    init {
        this.others = others
    }
    val inflater : LayoutInflater = LayoutInflater.from(context)


    override fun getView(position: Int, convertView: View?, parent: ViewGroup?) : View {

        val view : View = inflater.inflate(R.layout.spinner_item_other, parent, false)

        val otherName : TextView = view.findViewById(R.id.otherName)
        otherName.text = others[position].username
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

//custom adapter for spinner
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

//item for a pet in a spinner
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

class OtherItem(username : String, email : String ) {
    val username : String
    val email :String
    init {
        this.username = username
        this.email = email
    }
}
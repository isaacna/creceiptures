package com.example.creceiptures.activity

import android.app.ActionBar
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.MenuItem
import android.widget.Button
import com.example.creceiptures.App
import com.example.creceiptures.R
import com.example.creceiptures.adapter.GridAdapter
import com.example.creceiptures.enum.UserInterfaceState
import com.example.creceiptures.fragment.DetailsFragment
import com.example.creceiptures.fragment.HomeFragment
import com.example.creceiptures.fragment.LeaderboardFragment
import com.example.creceiptures.fragment.NoConnectionFragment
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseApp
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*




class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, GridAdapter.OnGridItemSelectListener {

    var currentView = UserInterfaceState.HOME
    private var isNetworkConnected = false
    private val SIGN_IN_REQUEST: Int = 0
    val fm = supportFragmentManager // loads fragment into view


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        FirebaseApp.initializeApp(this)

        val toggle = ActionBarDrawerToggle(
            this, drawer_layout, toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)
//        nav_view.setCheckedItem(R.id.nav_home)


        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo

        // initialize firebase stuff
        if (App.firebaseAuth == null) {
            App.firebaseAuth = FirebaseAuth.getInstance()
        }
        if (App.firestore == null) {
            App.firestore = FirebaseFirestore.getInstance()
        }
        if (App.firebaseAuth != null && App.firebaseAuth?.currentUser == null) {
            val intent = Intent(this, AccountActivity::class.java)
            startActivity(intent)
        }

        // Load Fragment into View
//        val fm = supportFragmentManager

        // add
        val ft = fm.beginTransaction()

//        if (networkInfo == null) {
//            Log.e("NETWORK", "not connected")
//            ft.add(R.id.frag_placeholder, NoConnectionFragment())
//        }

//        else {
            Log.e("NETWORK", "connected")
            ft.add(R.id.frag_placeholder, HomeFragment(this), "HOME_FRAG")
            this.isNetworkConnected = true
//        }

        ft.commit()

//        // if no user logged in, go to login/signup activity
//        if (App.firebaseAuth?.currentUser == null) {
//            Log.d("Ellen", "tryna open AccountActivity")
//            val intent = Intent(this, AccountActivity::class.java)
//            startActivityForResult(intent, SIGN_IN_REQUEST)
//        }



        supportActionBar?.title = "Home"
    }

    override fun onStart() {
        super.onStart()

        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo

        // Load Fragment into View
        val fm = supportFragmentManager

        // add
        val ft = fm.beginTransaction()

        if (networkInfo != null && App.firebaseAuth?.currentUser != null) {
            Log.e("NETWORK", "connected")
            this.isNetworkConnected = true

            if (this.currentView == UserInterfaceState.HOME) {
                ft.replace(R.id.frag_placeholder, HomeFragment(this), "HOME_FRAG")
            }
        }
        else {
            Log.e("NETWORK", "not connected")
            ft.replace(R.id.frag_placeholder, NoConnectionFragment())
        }

        ft.commit()
    }

    override fun onResume() {
        super.onResume()

        // if no user logged in, go to login/signup activity
        if (App.firebaseAuth?.currentUser == null) {
            Log.d("Ellen", "tryna open AccountActivity")
            val intent = Intent(this, AccountActivity::class.java)
            startActivityForResult(intent, SIGN_IN_REQUEST)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        /** Check which request we're responding to */
        if (requestCode == SIGN_IN_REQUEST) {
            /** Make sure the request was successful */
            if (resultCode == Activity.RESULT_OK) {
                Log.d("Ellen", "sign in successful")
                // go to home fragment
//                val ft = fm.beginTransaction()
//                ft.add(R.id.frag_placeholder, HomeFragment(this), "HOME_FRAG")
//                ft.commit()
            }

        }
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_addpet -> {
                // TODO
                val intent = Intent(this, AddPetActivity::class.java)
                this.startActivity(intent)
            }
            R.id.nav_petgrid -> {
                if (this.isNetworkConnected) {
                    this.currentView = UserInterfaceState.HOME

                    // Load Fragment into View
                    val fm = supportFragmentManager

                    // add
                    val ft = fm.beginTransaction()
                    ft.replace(R.id.frag_placeholder, HomeFragment(this@MainActivity), "HOME_FRAG")
                    ft.commit()

                    supportActionBar?.title = "Home"
                    supportActionBar?.subtitle = "What's New"
                }
            }
            R.id.nav_trade -> {
                // TODO
            }
            R.id.nav_leaderboard -> {
                if (this.isNetworkConnected) {
                    this.currentView = UserInterfaceState.LEADERBOARD

                    // Load Fragment into View
                    val fm = supportFragmentManager

                    // add
                    val ft = fm.beginTransaction()
                    ft.replace(R.id.frag_placeholder, LeaderboardFragment(this@MainActivity), "HOME_FRAG")
                    ft.commit()

                    supportActionBar?.title = "Leaderboard"
                }
            }
            R.id.nav_about_us -> {
//                displayDialog(R.layout.dialog_about_us)
            }
            R.id.nav_sign_in_out -> {
                if (App.firebaseAuth?.currentUser == null) {
                    item.title = "Sign Out"
                }
                else {
                    item.title = "Sign In"

                    App.firebaseAuth?.signOut()
                }

                val intent = Intent(this, AccountActivity::class.java)
                startActivity(intent)
            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onGridItemSelect(petId: String) {
        Log.d("Ellen", "onGridItemSelect")
        if (this.isNetworkConnected) {
            this.currentView = UserInterfaceState.DETAILS

            // Load Fragment into View
            val fm = supportFragmentManager

            // add
            val ft = fm.beginTransaction()
            ft.replace(R.id.frag_placeholder, DetailsFragment(this@MainActivity, petId), "DETAILS_FRAG")
            ft.commit()

            supportActionBar?.title = "Details"
        }
    }

//    private fun displayDialog(layout: Int) {
//        val dialog = Dialog(this)
//        dialog.setContentView(layout)
//
//        val window = dialog.window
//        window?.setLayout(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT)
//
//        dialog.findViewById<Button>(R.id.close).setOnClickListener {
//            dialog.dismiss()
//        }
//
//        dialog.show()
//    }
}

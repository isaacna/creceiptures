package com.example.creceiptures.activity

import android.app.ActionBar
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.util.Log
import android.view.MenuItem
import android.widget.Button
import com.example.creceiptures.App
import com.example.creceiptures.R
import com.example.creceiptures.enum.UserInterfaceState
import com.example.creceiptures.fragment.HomeFragment
import com.example.creceiptures.fragment.NoConnectionFragment
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    var currentView = UserInterfaceState.HOME
    private var isNetworkConnected = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//        setSupportActionBar(toolbar)
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

        // Load Fragment into View
        val fm = supportFragmentManager

        // add
        val ft = fm.beginTransaction()

        if (networkInfo == null) {
            Log.e("NETWORK", "not connected")
            ft.add(R.id.frag_placeholder, NoConnectionFragment())
        }
        if (App.firebaseAuth == null) {
            App.firebaseAuth = FirebaseAuth.getInstance()
        }

        if (App.firebaseAuth != null && App.firebaseAuth?.currentUser == null) {
            val intent = Intent(this, AccountActivity::class.java)
            startActivity(intent)
        }
        else {
            Log.e("NETWORK", "connected")
            ft.add(R.id.frag_placeholder, HomeFragment(this), "HOME_FRAG")
            this.isNetworkConnected = true
        }

        ft.commit()

        supportActionBar?.title = "Home"
        supportActionBar?.subtitle = "What's New"
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
            R.id.nav_home -> {
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
            R.id.nav_favorites -> {
                this.currentView = UserInterfaceState.FAVORITES

                // Load Fragment into View
                val fm = supportFragmentManager

                // add
//                val ft = fm.beginTransaction()
//                ft.remove(fm.findFragmentById(R.id.frag_placeholder)!!)
//                ft.add(R.id.frag_placeholder, FavoritesFragment(this@MainActivity), "FAVORITES_FRAG")
//                ft.commit()

                supportActionBar?.title = "Favorites"
                supportActionBar?.subtitle = ""
            }
            R.id.nav_reviews -> {
                if (this.isNetworkConnected) {
                    this.currentView = UserInterfaceState.REVIEW

                    // Load Fragment into View
                    val fm = supportFragmentManager

                    // add
//                    val ft = fm.beginTransaction()
//                    ft.remove(fm.findFragmentById(R.id.frag_placeholder)!!)
//                    ft.add(R.id.frag_placeholder, MyReviewsFragment(this@MainActivity), "REVIEWS_FRAG")
//                    ft.commit()

                    supportActionBar?.title = "My Reviews"
                    supportActionBar?.subtitle = ""
                }
            }
            R.id.nav_about_us -> {
//                displayDialog(R.layout.dialog_about_us)
            }
            R.id.nav_privacy_policy -> {
//                displayDialog(R.layout.dialog_privacy_policy)
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

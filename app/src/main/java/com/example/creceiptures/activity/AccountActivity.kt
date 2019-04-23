package com.example.creceiptures.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity;
import com.example.creceiptures.R
import com.example.creceiptures.adapter.AccountPagerAdapter
import kotlinx.android.synthetic.main.activity_account.*

class AccountActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account)

        val adapter = AccountPagerAdapter(this, supportFragmentManager)

        viewpager.adapter = adapter
        tabs.setupWithViewPager(viewpager)

    }

    override fun onBackPressed() {
        // "Close" app (push to background), don't go back to the AddPetActivity
        finishAffinity()
    }

}

package com.example.creceiptures.activity

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity;
import android.util.Log
import com.example.creceiptures.R
import com.example.creceiptures.model.cReceipture

import kotlinx.android.synthetic.main.activity_minigame.*

class MinigameActivity : AppCompatActivity() {

    private lateinit var pet: cReceipture

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_minigame)
        setSupportActionBar(toolbar)

        pet =intent.getParcelableExtra("PET")
        Log.d("MinigameActivity", pet.toString())
    }

}

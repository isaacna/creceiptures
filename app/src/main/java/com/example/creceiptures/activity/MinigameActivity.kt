package com.example.creceiptures.activity

import android.app.ActionBar
import android.app.Dialog
import android.os.Bundle
import android.os.CountDownTimer
import android.support.v7.app.AppCompatActivity;
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.example.creceiptures.App
import com.example.creceiptures.R
import com.example.creceiptures.model.cReceipture
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_minigame.*

class MinigameActivity : AppCompatActivity() {

    private lateinit var pet: cReceipture
    private lateinit var timer: TapTimer
    private var inProgress: Boolean = false
    private var points: Int = 0
    lateinit var dialog: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_minigame)
        setSupportActionBar(toolbar)

        pet = intent.getParcelableExtra("PET")

        dialog = Dialog(this)
        timer = TapTimer(this)

    }

    override fun onStart() {
        super.onStart()

        Picasso.get()
            .load(pet.imgUri)
            .resizeDimen(R.dimen.details_img_size, R.dimen.details_img_size)
            .into(findViewById<ImageView>(R.id.pet))

        findViewById<ImageView>(R.id.pet).setOnClickListener {
            // if a game is currently running, one tap is one point
            if (inProgress) {
                points++

                // https://github.com/daimajia/AndroidViewAnimations
                // pet responds on each tap
                YoYo.with(Techniques.Wobble)
                    .duration(300)
                    .repeat(1)
                    .playOn(findViewById(R.id.pet))
            }
        }

    }

    override fun onResume() {
        super.onResume()
        inProgress = false
        showInitDialog()
    }

    override fun onStop() {
        super.onStop()
        timer.cancel()
    }

    // the first dialog explaining how to play
    private fun showInitDialog() {
        dialog.setContentView(R.layout.dialog_minigame_start)

        val window = dialog.window
        window?.setLayout(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT)

        dialog.findViewById<Button>(R.id.start_button).setOnClickListener {
            inProgress = true
            dialog.dismiss()
            timer.start()
        }

        dialog.show()
    }

    // dialog after time's up; can either play again or leave activity.
    private fun showEndDialog() {
        dialog.setContentView(R.layout.dialog_minigame_end)

        val window = dialog.window
        window?.setLayout(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT)

        dialog.findViewById<TextView>(R.id.end_info).text = "${pet.name} gained ${points.toString()} petCoin!"

        dialog.findViewById<Button>(R.id.play_again_button).setOnClickListener {
            System.out.println("play again pressed")
            findViewById<TextView>(R.id.timer).text = "0:15"
            points = 0
            inProgress = true
            dialog.dismiss()
            timer.start()
        }
        dialog.findViewById<Button>(R.id.back_button).setOnClickListener {
            System.out.println("back pressed")
            dialog.dismiss()
            finish()
        }

        dialog.show()
    }

    // 15 second timer with 1 second intervals
    private class TapTimer(activity: MinigameActivity): CountDownTimer(15000, 1000) {

        val activity = activity
        val clock: TextView = activity.findViewById<TextView>(R.id.timer)

        override fun onTick(millisUntilFinished: Long) {
            var secLeft: String = (millisUntilFinished / 1000).toString()
            if (secLeft.length == 1) {
                clock.text = "0:0${secLeft}"
            }
            else {
                clock.text = "0:${secLeft}"
            }
        }

        override fun onFinish() {
            // update pet value in firebase
            activity.inProgress = false
            activity.pet.value += (activity.points / 2)
            val petDoc = App.firestore?.collection("cReceipture")?.document(activity.pet.id)
            petDoc?.update(
                "value", activity.pet.value
            )

            //update user's total in firebase
            val userDoc = App.firestore?.collection(("user"))?.document(App.firebaseAuth?.currentUser?.email!!)
            userDoc?.get()?.addOnCompleteListener { task: Task<DocumentSnapshot> ->
                if (task.isSuccessful) {
                    Log.d("DetailsActivity", "user successfully found")
                    val oldTotalPetCoin: Long = task.result!!.data!!["totalPetCoin"] as Long
                    userDoc.update(
                        "totalPetCoin", (oldTotalPetCoin + activity.points)
                    )
                } else {
                    Log.d("MinigameActivity", "failed to find update user total petcoin")
                }
            }

            // show dialog
            activity.showEndDialog()
        }

    }

}

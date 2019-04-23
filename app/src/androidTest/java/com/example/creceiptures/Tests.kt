package com.example.creceiptures

import android.support.test.runner.AndroidJUnit4
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import junit.framework.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Rule
import android.support.test.espresso.Espresso
import android.support.test.espresso.action.ViewActions
import android.support.test.espresso.matcher.ViewMatchers
import android.support.test.rule.ActivityTestRule
import com.example.creceiptures.activity.MinigameActivity
import android.content.Intent
import android.net.Uri
import com.example.creceiptures.model.cReceipture
import com.google.firebase.auth.FirebaseAuth
import org.junit.Before


@RunWith(AndroidJUnit4::class)
class Tests {

    private val pet = HashMap<String, Any>()
    private lateinit var petObj: cReceipture

    @Before
    fun setup() {
        pet["name"] = "test"
        pet["imgUri"] = "https://robohash.org/test?set=set4"
        pet["value"] = 10
        pet["owenr_curr"] = "testUser"
        pet["owner_og"] = "testUser"

        petObj = cReceipture("test", "test", 10, Uri.parse("https://robohash.org/test?set=set4"), "testUser", "testUser")
    }

    @Test
    fun testAddPet() {
        //setup
        if (App.firestore == null) {
            App.firestore = FirebaseFirestore.getInstance()
        }

        App.firestore!!.collection("creceipture")!!.document(pet["name"] as String)
            .set(pet)
            .addOnSuccessListener { documentReference ->
                App.firestore!!.collection("creceipture").document(pet["name"] as String)
                    ?.get()?.addOnCompleteListener { task: Task<DocumentSnapshot> ->
                    if (task.isSuccessful) {
                        assertEquals(task.result!!["owner_og"], "testUser")
                    }
                    else {
                        fail("Error retrieving document")
                    }
                }

                // cleanup
                App.firestore!!.collection("creceipture").document(pet["name"] as String).delete()
            }
            .addOnFailureListener { e ->
                fail("Error adding document")
            }

    }
//
//    @Rule
//    var detailsActivityRule: IntentsTestRule<MainActivity> = IntentsTestRule(MainActivity::class.java)
//
//    @Test
//    fun testDetailsToMinigame() {
//        // setup
//        intending(not(isInternal()).respondWith(Instrumentation.ActivityResult(Activity.RESULT_OK, null))
//    }

    @get:Rule
    var minigameTestRule: ActivityTestRule<MinigameActivity> = ActivityTestRule(MinigameActivity::class.java, false, false)

    @Test
    fun testMinigameDialog() {

        // pass pet into intent
        val i = Intent()
        i.putExtra("PET", petObj)
        minigameTestRule.launchActivity(i)
        Espresso.onView(ViewMatchers.withId(R.id.start_button)).perform(ViewActions.click())
        assertTrue(!minigameTestRule.activity.dialog.isShowing)
    }

    @Test
    fun testFirebaseAuth() {
        App.firebaseAuth = FirebaseAuth.getInstance()
        assertNotNull("firebaseAuth is still null", App.firebaseAuth)

        val email = "ellen.dai@wustl.edu"
        val password = "hello123"
        App.firebaseAuth?.signInWithEmailAndPassword(email, password)
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    assertNotNull(App.firebaseAuth?.currentUser)
                } else {
                    fail("user could not log in")
                }

            }

        App.firebaseAuth?.signOut()
    }

}
package com.example.creceiptures.model

import android.net.Uri
import com.google.firebase.firestore.DocumentSnapshot
import java.net.URI

class cReceiptureInGrid(id: String, name: String, value: Int, uri: Uri) {
    var name: String = name
    var id: String = id
    var value: Int = value
    // may change to drawable later, after we stop using robohash
    var imgUri: Uri = uri

}
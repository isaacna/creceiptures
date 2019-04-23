package com.example.creceiptures.model

import android.net.Uri

class cReceiptureInLeaderboard(name: String, owner: String, value: Int, uri: Uri) {
    var name: String = name
    var owner: String = owner
    var value: Int = value
    var imgUri: Uri = uri
}
package com.example.creceiptures.model

import android.net.Uri

class cReceiptureInGrid(id: String, name: String, value: Int, uri: Uri) {
    var name: String = name
    var id: String = id
    var value: Int = value
    var imgUri: Uri = uri
}
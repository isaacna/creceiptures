package com.example.creceiptures.utils

import java.io.Serializable

class ReceiptRequest() : Serializable {
    private var image : String = ""
    private var filename : String = ""
    private var contentType : String = ""

    constructor(
        image : String,
        filename : String,
        contentType : String
    ) : this() {
        this.image = image
        this.filename = filename
        this.contentType = contentType
    }

    fun getImage() : String {
        return this.image
    }

    fun getFilename() : String {
        return this.filename
    }

    fun getContentType() : String {
        return this.contentType
    }
}
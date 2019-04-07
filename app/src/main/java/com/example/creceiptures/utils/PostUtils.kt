package com.example.creceiptures.utils

import java.io.DataOutputStream
import java.net.HttpURLConnection

class PostUtils {
    private var httpConn : HttpURLConnection? = null
    private var request : DataOutputStream? = null
    private val  boundary : String =  "*****"
    private val  crlf :String = "\r\n"
    private val  twoHyphens :String = "--"
}
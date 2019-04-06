package com.example.creceiptures.utils

import android.app.Application
import android.content.Context
import android.text.TextUtils
import android.util.Log
import com.example.creceiptures.Receipt
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.*
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.nio.charset.Charset
import android.graphics.Bitmap
import android.util.Base64;



class QueryUtils : Application() {

    companion object {
        private val LogTag = this::class.java.simpleName
//        private const val BaseUrl = "https://api.taggun.io/api/receipt/v1/simple/file"
        private const val BaseUrl = "https://api.taggun.io/api/receipt/v1/simple/encoded"

        fun fetchReceiptInfo(bitmap : Bitmap, context : Context) : Receipt? {
            val url: URL? =
                createUrl(BaseUrl)

            var jsonResponse: String? = null
            try {
                System.out.println("making http request")
//                jsonResponse = makeHttpRequest2(url, bitmap, context)
                jsonResponse = makeHttpRequest(url, bitmap, context)
            }

            catch (e: IOException) {
                Log.e(LogTag, "Problem making the HTTP request.", e)
            }

            return extractDataFromJson(jsonResponse)
        }

        private fun extractDataFromJson(trackJson: String?): Receipt? {
            if (TextUtils.isEmpty(trackJson)) {
                return null
            }

            System.out.println("android dev is stupid as shit ")

            var receipt = Receipt()

            System.out.println("fuck me ")


            try {
                System.out.println("lol mother fucker")
                val baseJSONresponse = JSONObject(trackJson)

                val total = returnValueOrDefault<JSONObject>(
                    baseJSONresponse,
                    "totalAmount"
                ) as JSONObject?
                val tax = returnValueOrDefault<JSONObject>(
                    baseJSONresponse,
                    "taxAmount"
                ) as JSONObject?
                val date = returnValueOrDefault<JSONObject>(
                    baseJSONresponse,
                    "date"
                ) as JSONObject?
                val merchant = returnValueOrDefault<JSONObject>(
                    baseJSONresponse,
                    "merchantName"
                ) as JSONObject?
                val address = returnValueOrDefault<JSONObject>(
                    baseJSONresponse,
                    "merchantAddress"
                ) as JSONObject?
                val state = returnValueOrDefault<JSONObject>(
                    baseJSONresponse,
                    "merchantState"
                ) as JSONObject?

                var totalData = 0.0
                var taxData = 0.0
                var dateData = ""
                var merchantData = ""
                var addressData = ""
                var stateData = ""


                System.out.println("null stuff")

                if (total != null) {
                    if(total.has("data")) {
                        totalData = returnValueOrDefault<Double>(
                            total,
                            "data"
                        ) as Double
                    }
                }

                if (tax != null) {
                    if(tax.has("data")) {
                        taxData = returnValueOrDefault<Double>(
                            tax,
                            "data"
                        ) as Double
                    }
                }

                System.out.println("fuck date")

                if (date != null) {
                    if(date.has("data")) {
                        dateData = returnValueOrDefault<String>(
                            date,
                            "data"
                        ) as String
                    }
                }

                System.out.println("date is ok")

                if (merchant != null) {
                    if(merchant.has("data")) {
                        merchantData = returnValueOrDefault<String>(
                            merchant,
                            "data"
                        ) as String
                    }
                }

                if (address != null) {
                    if(address.has("data")) {
                        addressData = returnValueOrDefault<String>(
                            address,
                            "data"
                        ) as String
                    }

                }

                if (state != null) {
                    if(state.has("data")) {
                        stateData = returnValueOrDefault<String>(
                            state,
                            "data"
                        ) as String
                    }
                }
                System.out.println("before receipt creation")

                receipt = Receipt(
                    totalData,
                    taxData,
                    dateData,
                    merchantData,
                    addressData,
                    stateData
                )

            }


            catch (e: JSONException) {
                Log.e(LogTag, "Problem parsing the product JSON results", e)
            }

            return receipt
        }

        private fun createUrl(stringUrl: String): URL? {
            var url: URL? = null
            try {
                url = URL(stringUrl)
            }
            catch (e: MalformedURLException) {
                Log.e(LogTag, "Problem building the URL.", e)
            }

            return url
        }

        private fun makeHttpRequest(url : URL?, bitmap : Bitmap, context : Context) : String {
            var jsonResponse = ""

            if (url == null) {
                System.out.println("URL WAS NULL")

                return jsonResponse
            }

            var urlConnection: HttpURLConnection? = null
            var inputStream: InputStream? = null
            val boundary : String = "*****"

            try {

                val os : ByteArrayOutputStream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os)
                val byteArr : ByteArray = os.toByteArray()
                val encodedImage : String = Base64.encodeToString(byteArr, Base64.DEFAULT)
                System.out.println("PLSSSS")

                urlConnection = url.openConnection() as HttpURLConnection

                //set API KEY
                urlConnection.setRequestProperty("apikey", "ce67f480541011e98bfadfb7eb1aa8b5")
                urlConnection.setRequestProperty("Connection", "Keep-Alive");
                urlConnection.setRequestProperty("Cache-Control", "no-cache");

                val receiptRequest : ReceiptRequest = ReceiptRequest(encodedImage, "receipt.jpg", "image/jpeg")

                var body : JSONObject = JSONObject()
                body.put("image", receiptRequest.getImage())
                body.put("filename", receiptRequest.getFilename())
                body.put("contentType", receiptRequest.getContentType())



                //post instead of GET
                urlConnection.requestMethod = "POST" //changed this
                urlConnection.setRequestProperty("Content-Type", "application/json")

                System.out.println("ababababababab")

                var arr : ByteArray = body.toString().toByteArray()
                urlConnection.outputStream.write(arr)


                System.out.println("jfjfjfjfjfjfj")

//                System.out.println(urlConnection.toString())

                urlConnection.connect()
                System.out.println("successful connection")


                if (urlConnection.responseCode == 200) {
                    System.out.println("good connection")

                    inputStream = urlConnection.inputStream
                    jsonResponse = readFromStream(inputStream)
                    System.out.println(urlConnection.responseMessage)

                }
                else {
                    System.out.println("Bad Connection")
                    Log.e(LogTag, "Error response code: ${urlConnection.responseCode}")
                }

            }
            catch(e: IOException) {
                Log.e(LogTag, "Problem retrieving the product data results: $url", e)
                System.out.println(e)
                System.out.println(e.stackTrace)
            }
            finally {
                System.out.println("FINALLY")
                urlConnection?.disconnect()
                inputStream?.close()
            }

            System.out.println(jsonResponse)
            return jsonResponse

        }

        private fun readFromStream(inputStream: InputStream?): String {
            val output = StringBuilder()
            if (inputStream != null) {
                val inputStreamReader = InputStreamReader(inputStream, Charset.forName("UTF-8"))
                val reader = BufferedReader(inputStreamReader)
                var line = reader.readLine()
                while (line != null) {
                    output.append(line)
                    line = reader.readLine()
                }
            }

            return output.toString()
        }

        private inline fun <reified T> returnValueOrDefault(json: JSONObject, key: String): Any? {
            when (T::class) {
                String::class -> {
                    return if (json.has(key)) {
                        json.getString(key)
                    } else {
                        ""
                    }
                }
                Int::class -> {
                    return if (json.has(key)) {
                        json.getInt(key)
                    }
                    else {
                        return -1
                    }
                }
                Double::class -> {
                    return if (json.has(key)) {
                        json.getDouble(key)
                    }
                    else {
                        return -1.0
                    }
                }
                Long::class -> {
                    return if (json.has(key)) {
                        json.getLong(key)
                    }
                    else {
                        return (-1).toLong()
                    }
                }
                JSONObject::class -> {
                    return if (json.has(key)) {
                        json.getJSONObject(key)
                    }
                    else {
                        return null
                    }
                }
                JSONArray::class -> {
                    return if (json.has(key)) {
                        json.getJSONArray(key)
                    }
                    else {
                        return null
                    }
                }
                else -> {
                    return null
                }
            }
        }
    }
}

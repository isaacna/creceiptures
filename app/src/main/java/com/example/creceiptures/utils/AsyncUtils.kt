package com.example.creceiptures.utils

import android.annotation.SuppressLint
import android.arch.lifecycle.MutableLiveData
import android.content.Context
import android.graphics.Bitmap
import android.os.AsyncTask
import android.util.Log
import com.example.creceiptures.model.Receipt

class AsyncUtils(context: Context) {
    var receipt : MutableLiveData<Receipt> = MutableLiveData()
    var context : Context = context

    fun loadReceipt(bitmap: Bitmap) : MutableLiveData<Receipt> {
        receipt.value = FetchReceiptAsyncTask().execute(bitmap).get()
        return receipt
    }

    //async task for fetching extra facts about a track
    @SuppressLint("StaticFieldLeak")
    inner class FetchReceiptAsyncTask: AsyncTask<Bitmap, Void, Receipt>() {
        override fun doInBackground(vararg param: Bitmap): Receipt? {
            val result : Receipt? = QueryUtils.fetchReceiptInfo(param[0], context)
            if (result != null) {
                System.out.println(result.getAddress())

            }
            else {
                System.out.println("RESULT WAS NULL")
            }
            return result
        }

        override fun onPostExecute(result: Receipt?) {
            super.onPostExecute(result)
            if (result == null) {
                Log.e("RESULTS", "No receipt found")
            }
            else {
                receipt.value = result
                System.out.println("A receipt was fetched")
            }
        }
    }
}
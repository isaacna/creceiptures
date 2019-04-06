package com.example.creceiptures

import java.io.Serializable
import java.util.*

class Receipt(): Serializable {
    private var total : Double = 0.0
    private var tax : Double = 0.0
    private var date : String = ""
    private var merchant : String = ""
    private var address : String = ""
    private var state : String = ""

    constructor(
        total: Double,
        tax: Double,
        date: String,
        merchant : String,
        address : String,
        state : String
    ) : this() {
        this.total = total
        this.tax = tax
        this.date = date
        this.merchant = merchant
        this.address = address
        this.state = state
    }

    fun getTotal(): Double {
        return this.total
    }

    fun getTax():Double {
        return this.tax
    }

    fun getDate(): String {
        return this.date
    }

    fun getMerchant(): String {
        return this.merchant
    }

    fun getAddress(): String {
        return this.address
    }

    fun getState() : String {
        return this.state
    }
}
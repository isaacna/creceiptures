package com.example.creceiptures.model

import android.net.Uri
import android.os.Parcel
import android.os.Parcelable

class cReceipture(id: String, name: String, value: Int, uri: Uri, owner_curr: String, owner_og: String): Parcelable {
    var name: String = name
    var id: String = id
    var value: Int = value
    var imgUri: Uri = uri

    var owner_curr = owner_curr
    var owner_og = owner_og

    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readInt(),
        Uri.parse(parcel.readString()),
        parcel.readString(),
        parcel.readString()
    ) {}

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(name)
        parcel.writeInt(value)
        parcel.writeString(imgUri.toString())
        parcel.writeString(owner_curr)
        parcel.writeString(owner_og)
    }

    override fun toString(): String {
        return "Minigame loaded pet: ${id}, ${name}, ${value.toString()}, ${imgUri.toString()}, ${owner_curr}, ${owner_og}"
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<cReceipture> {
        override fun createFromParcel(parcel: Parcel): cReceipture {
            return cReceipture(parcel)
        }

        override fun newArray(size: Int): Array<cReceipture?> {
            return arrayOfNulls(size)
        }
    }

}
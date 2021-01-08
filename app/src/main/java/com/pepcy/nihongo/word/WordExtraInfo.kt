package com.pepcy.nihongo.word

import android.os.Parcel
import android.os.Parcelable

class WordExtraInfo(
        val key: String = "",
        val value: String = ""
) : Parcelable {
    override fun toString(): String = "{$key, $value}"

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(key)
        parcel.writeString(value)
    }

    override fun describeContents(): Int {
        return 0
    }

    constructor(parcel: Parcel) : this(
            parcel.readString().toString(),
            parcel.readString().toString()) {}

    companion object CREATOR : Parcelable.Creator<WordExtraInfo> {
        override fun createFromParcel(parcel: Parcel): WordExtraInfo {
            return WordExtraInfo(parcel)
        }

        override fun newArray(size: Int): Array<WordExtraInfo?> {
            return arrayOfNulls(size)
        }
    }
}
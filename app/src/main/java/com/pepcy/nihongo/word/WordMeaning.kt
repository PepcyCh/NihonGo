package com.pepcy.nihongo.word

import android.os.Parcel
import android.os.Parcelable

class WordMeaning(
    val indexMajor: Int,
    val indexMinor: Int,
    var meaning: String,
    val examples: ArrayList<String> = ArrayList()
) : Parcelable {
    override fun toString(): String {
        return "{ index: $indexMajor.$indexMinor meaning: $meaning, examples: ${examples.joinToString(", ", "[", "]")} }"
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(indexMajor)
        parcel.writeInt(indexMinor)
        parcel.writeString(meaning)
        parcel.writeStringList(examples)
    }

    override fun describeContents(): Int = 0

    constructor(parcel: Parcel) : this(
            parcel.readInt(),
            parcel.readInt(),
            parcel.readString().toString()) {
        parcel.readStringList(examples)
    }

    companion object CREATOR : Parcelable.Creator<WordMeaning> {
        override fun createFromParcel(parcel: Parcel): WordMeaning {
            return WordMeaning(parcel)
        }

        override fun newArray(size: Int): Array<WordMeaning?> {
            return arrayOfNulls(size)
        }
    }
}
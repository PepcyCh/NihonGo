package com.pepcy.nihongo.word

import android.os.Parcel
import android.os.Parcelable

class WordItem(
    var type: WordType,
    val meanings: ArrayList<WordMeaning> = ArrayList(),
    val extra: ArrayList<WordExtraInfo> = ArrayList()
) : Parcelable {
    override fun toString(): String {
        return "{ type: $type, meanings: ${meanings.joinToString(", ", "[", "]") }}" +
                ", extra: ${extra.joinToString(", ", "[", "]")} }"
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(type.name)
        parcel.writeTypedList(meanings)
        parcel.writeTypedList(extra)
    }

    override fun describeContents(): Int = 0

    constructor(parcel: Parcel) : this(WordType.valueOf(parcel.readString().toString())) {
        parcel.readTypedList(meanings, WordMeaning.CREATOR)
        parcel.readTypedList(extra, WordExtraInfo.CREATOR)
    }

    companion object CREATOR : Parcelable.Creator<WordItem> {
        override fun createFromParcel(parcel: Parcel): WordItem {
            return WordItem(parcel)
        }

        override fun newArray(size: Int): Array<WordItem?> {
            return arrayOfNulls(size)
        }
    }
}
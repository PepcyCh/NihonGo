package com.pepcy.nihongo.word

import android.os.Parcel
import android.os.Parcelable
import com.pepcy.nihongo.fakekana.KanaUtil
import java.io.Serializable
import java.lang.StringBuilder

class Word() : Serializable, Parcelable, Cloneable, Comparable<Word> {
    var title = ""
    var kana = ""
    var isKanji = false
    var suru = false
    var items = ArrayList<WordItem>()
    var fromLocal = false

    override fun toString(): String {
        val str = StringBuilder()
        str.append("Word { title: ")
        str.append(title)
        str.append(", kana; $kana, suru: $suru, isKanji: $isKanji, meaning: ")
        str.append(items.joinToString(", ", "[", "]"))
        str.append(" }")
        return str.toString()
    }

    fun containKanji(): Boolean {
        for (ch in title) {
            if (!KanaUtil.isKana(ch)) {
                return true
            }
        }
        return false
    }

    fun getMeaningString(limitLength: Int = -1): String {
        val sb = StringBuilder()
        for (item in items) {
            if (item.type != WordType.NONE) {
                sb.append("[${item.type.abstractString()}]")
            }
            if (suru) {
                sb.append("（スル）")
            }
            for (meaning in item.meanings) {
                when (meaning.meaning[0]) {
                    '《' -> {
                        val index = meaning.meaning.indexOf('》')
                        sb.append(meaning.meaning.substring(index + 1))
                    }
                    '（' -> {
                        val index = meaning.meaning.indexOf('）')
                        sb.append(meaning.meaning.substring(index + 1))
                    }
                    else -> {
                        sb.append(meaning.meaning)
                    }
                }
            }
        }
        return if (limitLength > 0 && sb.length > limitLength) {
            sb.toString().substring(0, limitLength) + " ……"
        } else {
            sb.toString()
        }
    }

    fun getRandomSingleMeaning(limitLength: Int = -1): String {
        val meaningStrings = ArrayList<String>()
        for (item in items) {
            val typeStr = if (item.type != WordType.NONE) {
                "[${item.type.abstractString()}]"
            } else {
                ""
            }
            for (meaning in item.meanings) {
                if (meaning.meaning[0] == '（' || meaning.meaning[0] == '《') {
                    continue
                }
                val str = typeStr + meaning.meaning
                meaningStrings.add(str)
            }
        }

        if (meaningStrings.isEmpty()) {
            return getMeaningString(limitLength)
        }

        val res = meaningStrings.random()
        return if (limitLength > 0 && res.length > limitLength) {
            res.substring(0, limitLength)
        } else {
            res
        }
    }

    override fun equals(other: Any?): Boolean = other is Word && other.title == title && other.kana == kana

    override fun hashCode(): Int {
        return title.hashCode() + kana.hashCode()
    }

    override fun compareTo(other: Word): Int {
        return if (kana == other.kana) {
            title.compareTo(other.title)
        } else {
            kana.compareTo(other.kana)
        }
    }

    fun mergeWith(another: Word): Word {
        for (anoItem in another.items) {
            val itemIndex = items.indexOfFirst { it.type == anoItem.type }
            if (itemIndex >= 0) {
                for (anoMeaning in anoItem.meanings) {
                    items[itemIndex].meanings.add(anoMeaning)
                }
                for (anoExtra in anoItem.extra) {
                    items[itemIndex].extra.add(anoExtra)
                }
            } else {
                items.add(anoItem)
            }
        }
        return this
    }

    public override fun clone(): Word {
        val another = Word()
        another.title = title
        another.kana = kana
        another.isKanji = isKanji
        another.suru = suru
        another.items = items.clone() as ArrayList<WordItem>
        another.fromLocal = fromLocal
        return another
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(title)
        parcel.writeString(kana)
        parcel.writeByte(if (isKanji) 1 else 0)
        parcel.writeByte(if (suru) 1 else 0)
        parcel.writeTypedList(items)
        parcel.writeByte(if (fromLocal) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    constructor(parcel: Parcel) : this() {
        title = parcel.readString().toString()
        kana = parcel.readString().toString()
        isKanji = parcel.readByte() != 0.toByte()
        suru = parcel.readByte() != 0.toByte()
        parcel.readTypedList(items, WordItem.CREATOR)
        fromLocal = parcel.readByte() != 0.toByte()
    }

    companion object CREATOR : Parcelable.Creator<Word> {
        override fun createFromParcel(parcel: Parcel): Word {
            return Word(parcel)
        }

        override fun newArray(size: Int): Array<Word?> {
            return arrayOfNulls(size)
        }
    }
}
package com.pepcy.nihongo.learning

import android.os.Parcel
import android.os.Parcelable
import com.pepcy.nihongo.database.CardEntity
import com.pepcy.nihongo.database.WordsDao
import com.pepcy.nihongo.database.WordsDatabaseUtil
import com.pepcy.nihongo.word.Word
import kotlin.math.roundToInt

class Card(
    val word: Word,
    val setName: String,
    var level: Int = 0,
    var nextDate: Long = 0,
) : Parcelable {
    var accuracy = 0.0

    val learned: Boolean
        get() = nextDate != 0L

    constructor(dao: WordsDao, entity: CardEntity) : this(
            WordsDatabaseUtil.getWord(dao, entity.title, entity.kana, entity.isKanji)!!,
            entity.setName,
            entity.level,
            entity.date,
    )

    fun getEntity(): CardEntity = CardEntity(
            word.title,
            word.kana,
            word.isKanji,
            setName,
            level,
            nextDate,
    )

    fun calcNextTime(step: Int) {
        val nextLevel = (accuracy * (level + step) + (1.0 - accuracy) * 1).roundToInt()
        level = nextLevel
        val dayCount = WordSet.getDayCountOfToday()
        nextDate = dayCount + level
    }

    override fun hashCode(): Int = word.hashCode()

    override fun equals(other: Any?): Boolean = other is Card && word == other.word

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(word, flags)
        parcel.writeString(setName)
        parcel.writeInt(level)
        parcel.writeLong(nextDate)
    }

    override fun describeContents(): Int = 0

    constructor(parcel: Parcel) : this(
            parcel.readParcelable<Word>(Word::class.java.classLoader)!!,
            parcel.readString()!!,
            parcel.readInt(),
            parcel.readLong()
    )

    companion object CREATOR : Parcelable.Creator<Card> {
        override fun createFromParcel(parcel: Parcel): Card {
            return Card(parcel)
        }

        override fun newArray(size: Int): Array<Card?> {
            return arrayOfNulls(size)
        }
    }
}
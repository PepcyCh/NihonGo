package com.pepcy.nihongo.learning

import android.os.Parcel
import android.os.Parcelable
import com.pepcy.nihongo.database.WordSetDailyEntity
import com.pepcy.nihongo.database.WordSetEntity
import com.pepcy.nihongo.database.WordsDao

class Task(
        private val wordSetEntity: WordSetEntity,
        private val questionsPerCard: Int,
        private val newCount: Int,
        private val reviewCount: Int,
) : Parcelable {
    val questions = ArrayList<Question>()
    var timeUsed = 0L

    override fun toString(): String = "Task { ${questions.joinToString(", ")} }"

    fun calcNextTime(dao: WordsDao) {
        val result = HashMap<Card, Int>()
        var correctCount = 0
        for (question in questions) {
            if (result.containsKey(question.card)) {
                result[question.card] = result[question.card]!! + if (question.correctlyAnswered) 1 else 0
            } else {
                result[question.card] = if (question.correctlyAnswered) 1 else 0
            }

            if (question.correctlyAnswered) {
                ++correctCount
            }
        }

        for (value in result) {
            value.key.accuracy = value.value / questionsPerCard.toDouble()
            value.key.calcNextTime(if (questionsPerCard > 2) 2 else 1)
            dao.updateCard(value.key.getEntity())
        }
        val newEntity = WordSetEntity(
                wordSetEntity.setName,
                wordSetEntity.newWordsPerDay,
                wordSetEntity.questionsPerCard,
                wordSetEntity.questionSingleMeaning,
                WordSet.getDayCountOfToday(),
                wordSetEntity.createTime,
        )
        dao.updateWordSet(newEntity)
        val accuracy = correctCount.toDouble() / questions.size
        dao.insertWordSetDaily(WordSetDailyEntity(
                wordSetEntity.setName,
                WordSet.getDayCountOfToday(),
                accuracy,
                newCount,
                reviewCount,
                timeUsed
        ))
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(wordSetEntity, flags)
        parcel.writeInt(questionsPerCard)
        parcel.writeInt(newCount)
        parcel.writeInt(reviewCount)
        parcel.writeTypedList(questions)
    }

    override fun describeContents(): Int = 0

    constructor(parcel: Parcel) : this(
            parcel.readParcelable<WordSetEntity>(WordSetEntity::class.java.classLoader)!!,
            parcel.readInt(),
            parcel.readInt(),
            parcel.readInt(),
    ) {
        parcel.readTypedList(questions, Question.CREATOR)
    }

    companion object CREATOR : Parcelable.Creator<Task> {
        override fun createFromParcel(parcel: Parcel): Task {
            return Task(parcel)
        }

        override fun newArray(size: Int): Array<Task?> {
            return arrayOfNulls(size)
        }
    }
}
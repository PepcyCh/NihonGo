package com.pepcy.nihongo.learning

import android.os.Parcel
import android.os.Parcelable
import com.pepcy.nihongo.fakekana.FakeKanaGenerator

class Question(val card: Card, val type: QuestionType) : Parcelable {
    var title = ""
    val items = ArrayList<String>()
    var correctItemIndex = 0
    var chosenItemIndex = -1

    override fun toString(): String {
        val str = StringBuffer()
        str.append("Question { title: $title, correct: $correctItemIndex")
        str.append(", items: ${items.joinToString(", ", "[", "]")} }")
        return str.toString()
    }

    val correctlyAnswered: Boolean
        get() = correctItemIndex == chosenItemIndex

    fun generate(set: WordSet, singleMeaning: Boolean) {
        if (type == QuestionType.KANJI_TO_MEANING || type == QuestionType.KANA_TO_MEANING) {
            generateRandomMeaning(set, singleMeaning)
        } else if (type == QuestionType.MEANING_TO_KANA) {
            generateRandomKana(set)
        } else if (type == QuestionType.MEANING_TO_KANJI) {
            generateRandomKanji(set)
        } else { // QuestionType.KANJI_TO_KANA
            generateFakeKana()
            // generateRandomKana(set)
        }

        title = if (type == QuestionType.MEANING_TO_KANA || type == QuestionType.MEANING_TO_KANJI) {
            if (singleMeaning) {
                card.word.getRandomSingleMeaning()
            } else {
                card.word.getMeaningString()
            }
        } else if (type == QuestionType.KANJI_TO_MEANING || type == QuestionType.KANJI_TO_KANA) {
            card.word.title
        } else { // QuestionType.KANA_TO_MEANING
            card.word.kana
        }
    }

    private fun generateFakeKana() {
        items.add(card.word.kana)
        for (i in 1..3) {
            while (true) {
                val fakeKana = FakeKanaGenerator.gen(card.word.kana)
                var okay = true
                for (j in 0 until i) {
                    if (items[j] == fakeKana) {
                        okay = false
                        break
                    }
                }
                if (okay) {
                    items.add(fakeKana)
                    break
                }
            }
        }

        items.shuffle()
        for (i in 0..3) {
            if (items[i] == card.word.kana) {
                correctItemIndex = i
                break
            }
        }
    }
    private fun generateRandomKana(set: WordSet) {
        items.add(card.word.kana)
        val randIndices = getThreeRandomCard(card, set)
        for (ind in randIndices) {
            items.add(set.getCard(ind).word.kana)
        }
        items.shuffle()
        for (i in 0..3) {
            if (items[i] == card.word.kana) {
                correctItemIndex = i
                break
            }
        }
    }
    private fun generateRandomMeaning(set: WordSet, singleMeaning: Boolean) {
        val answer = if (singleMeaning) {
            card.word.getRandomSingleMeaning(20)
        } else {
            card.word.getMeaningString(20)
        }
        items.add(answer)
        val randIndices = getThreeRandomCard(card, set)
        for (ind in randIndices) {
            items.add(set.getCard(ind).word.getMeaningString(20))
        }
        items.shuffle()
        for (i in 0..3) {
            if (items[i] == answer) {
                correctItemIndex = i
                break
            }
        }
    }
    private fun generateRandomKanji(set: WordSet) {
        items.add(card.word.title)
        val randIndices = getThreeRandomCard(card, set)
        for (ind in randIndices) {
            items.add(set.getCard(ind).word.title)
        }
        items.shuffle()
        for (i in 0..3) {
            if (items[i] == card.word.title) {
                correctItemIndex = i
                break
            }
        }
    }

    private fun getThreeRandomCard(card: Card, set: WordSet): ArrayList<Int> {
        val indices = ArrayList<Int>()
        for (i in 0..2) {
            while (true) {
                val randIndex = (0 until set.cardsCount).random()
                val randCard = set.getCard(randIndex)

                var flag = card == randCard
                for (j in 0 until i) {
                    if (randIndex == indices[j]) {
                        flag = true
                        break
                    }
                }
                if (flag) {
                    continue
                }

                indices.add(randIndex)
                break
            }
        }
        return indices
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(card, flags)
        parcel.writeInt(type.value)
        parcel.writeString(title)
        parcel.writeStringList(items)
        parcel.writeInt(correctItemIndex)
        parcel.writeInt(chosenItemIndex)
    }

    override fun describeContents(): Int = 0

    constructor(parcel: Parcel) : this(
            parcel.readParcelable<Card>(Card::class.java.classLoader)!!,
            QuestionType.fromInt(parcel.readInt())!!
    ) {
        title = parcel.readString().toString()
        parcel.readStringList(items)
        correctItemIndex = parcel.readInt()
        chosenItemIndex = parcel.readInt()
    }

    companion object CREATOR : Parcelable.Creator<Question> {
        override fun createFromParcel(parcel: Parcel): Question {
            return Question(parcel)
        }

        override fun newArray(size: Int): Array<Question?> {
            return arrayOfNulls(size)
        }
    }
}
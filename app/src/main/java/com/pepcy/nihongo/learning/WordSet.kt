package com.pepcy.nihongo.learning

import com.pepcy.nihongo.database.WordSetEntity
import com.pepcy.nihongo.database.WordsDao
import com.pepcy.nihongo.database.WordsDatabaseUtil
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashSet
import kotlin.random.Random

class WordSet(
        val setName: String,
        val dao: WordsDao,
        var newWordsPerDay: Int = 0,
        var questionsPerCard: Int = 0,
        var questionSingleMeaning: Boolean = false,
        var latestLearnDay: Long = 0,
        private val createTime: Long = System.currentTimeMillis(),
) {
    private var cards = ArrayList<Card>()
    private var newWordsCount = 0
    private var reviewWordsCount = 0

    val cardsCount: Int
        get() = cards.size
    val newWords: Int
        get() = newWordsCount
    val reviewWords: Int
        get() = reviewWordsCount
    val needToReview: Boolean
        get() = latestLearnDay < getDayCountOfToday()
    val newWordsToday: Int
        get() = if (latestLearnDay < getDayCountOfToday()) newWordsCount.coerceAtMost(newWordsPerDay) else 0
    val studied: Boolean
        get() = newWordsCount != cardsCount

    constructor(dao: WordsDao, entity: WordSetEntity) : this(
            entity.setName,
            dao,
            entity.newWordsPerDay,
            entity.questionsPerCard,
            entity.questionSingleMeaning,
            entity.latestLearnDay,
            entity.createTime,
    ) {
        cards = WordsDatabaseUtil.getAllCardsOfSet(dao, setName)

        val dayCount = getDayCountOfToday()
        for (card in cards) {
            if (card.nextDate == 0L) {
                ++newWordsCount
            } else if (card.nextDate == dayCount) {
                ++reviewWordsCount
            } else {
                break
            }
        }
        if (latestLearnDay >= dayCount) {
            reviewWordsCount = 0
        }
    }

    fun getEntity(): WordSetEntity = WordSetEntity(
            setName,
            newWordsPerDay,
            questionsPerCard,
            questionSingleMeaning,
            latestLearnDay,
            createTime
    )

    fun getCard(index: Int): Card = cards[index]

    fun generateTask(): Task {
        val task = Task(getEntity(), questionsPerCard, newWordsToday, reviewWordsCount)

        val newWordsIndices = HashSet<Int>()
        for (i in 0 until newWordsToday) {
            while (true) {
                val randIndex = Random.nextInt(0, newWordsCount - 1)
                if (!newWordsIndices.contains(randIndex)) {
                    newWordsIndices.add(randIndex)
                    break
                }
            }
        }

        val taskCards = ArrayList<Card>()
        for (index in newWordsIndices) {
            taskCards.add(cards[index])
        }

        val dayCount = getDayCountOfToday()
        for (i in newWordsCount until cards.size) {
            if (cards[i].nextDate == dayCount) {
                taskCards.add(cards[i])
            } else {
                break
            }
        }

        for (card in taskCards) {
            val questions = getRandomQuestions(card)
            for (q in questions) {
                task.questions.add(q)
            }
        }
        task.questions.shuffle()

        return task
    }

    fun getLevelStat(): Array<Double> {
        val ratioData = arrayOf(0.0, 0.0, 0.0, 0.0)

        for (card in cards) {
            val quantizedLevel = when(card.level) {
                0 -> 0
                in 1..3 -> 1
                in 4..10 -> 2
                else -> 3
            }

            ratioData[quantizedLevel] += 1.0
        }

        ratioData[0] = ratioData[0] / cards.size
        ratioData[1] = ratioData[1] / cards.size
        ratioData[2] = ratioData[2] / cards.size
        ratioData[3] = ratioData[3] / cards.size

        return ratioData
    }

    private fun getRandomQuestions(card: Card): ArrayList<Question> {
        val questionTypes = QuestionType.values().toCollection(ArrayList())
        questionTypes.shuffle()
        val questions = ArrayList<Question>()
        for (type in questionTypes) {
            if (!card.word.containKanji() && type == QuestionType.KANJI_TO_KANA) {
                continue
            }

            val question = Question(card, type)
            question.generate(this, questionSingleMeaning)
            questions.add(question)
            if (questions.size == questionsPerCard) {
                break
            }
        }
        return questions
    }

    companion object {
        fun getDayCountOfToday(): Long {
            val tz = TimeZone.getDefault()
            var today = System.currentTimeMillis()
            today += tz.getOffset(today)
            return today / 1000 / 3600 / 24
        }
    }
}
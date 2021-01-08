package com.pepcy.nihongo.database

import android.content.res.Resources
import com.google.gson.Gson
import com.pepcy.nihongo.R
import com.pepcy.nihongo.baseset.BaseSetJp1
import com.pepcy.nihongo.baseset.BaseSetJp2
import com.pepcy.nihongo.baseset.BaseSetJp3
import com.pepcy.nihongo.baseset.BaseSetJp4
import com.pepcy.nihongo.initwords.InitWords
import com.pepcy.nihongo.learning.Card
import com.pepcy.nihongo.learning.WordSet
import com.pepcy.nihongo.webliocrawler.WeblioCrawler
import com.pepcy.nihongo.word.*
import java.io.InputStreamReader

class WordsDatabaseUtil {
    companion object {
        fun getWord(dao: WordsDao, title: String, kana: String, isKanji: Boolean): Word? {
            val word = Word()
            word.title = title
            word.kana = kana
            word.isKanji = isKanji

            val wordEntity = dao.getWord(title, kana, isKanji) ?: return null
            word.suru = wordEntity.suru

            val wordItemEntities = dao.getWordItem(title, kana, isKanji)
            if (wordItemEntities.isEmpty()) {
                return null
            }
            for (itemEntity in wordItemEntities) {
                val type = WordType.fromInt(itemEntity.type) ?: return null

                val meanings = ArrayList<WordMeaning>()
                val wordMeaningEntities = dao.getWordMeaning(title, kana, isKanji, itemEntity.tid)
                for (meaningEntity in wordMeaningEntities) {
                    val major = meaningEntity.major
                    val minor = meaningEntity.minor
                    val examples = ArrayList<String>()
                    val wordMeaningExampleEntities =
                            dao.getWordMeaningExample(title, kana, isKanji, itemEntity.tid, meaningEntity.mid)
                    for (exampleEntity in wordMeaningExampleEntities) {
                        examples.add(exampleEntity.example)
                    }
                    meanings.add(WordMeaning(major, minor, meaningEntity.meaning, examples))
                }

                val extraInfos = ArrayList<WordExtraInfo>()
                val wordExtraEntities = dao.getWordExtra(title, kana, isKanji, itemEntity.tid)
                for (extraEntity in wordExtraEntities) {
                    extraInfos.add(WordExtraInfo(extraEntity.key, extraEntity.value))
                }

                word.items.add(WordItem(type, meanings, extraInfos))
            }

            return word
        }

        private fun getWordFromTitle(dao: WordsDao, title: String): ArrayList<Word> {
            val words = ArrayList<Word>()
            val wordEntities = dao.getWordFromTitle(title)
            for (wordEntity in wordEntities) {
                val word = getWord(dao, title, wordEntity.kana, wordEntity.isKanji)
                if (word != null) {
                    words.add(word)
                }
            }
            return words
        }
        private fun getWordFromKana(dao: WordsDao, kana: String): ArrayList<Word> {
            val words = ArrayList<Word>()
            val wordEntities = dao.getWordFromKana(kana)
            for (wordEntity in wordEntities) {
                val word = getWord(dao, wordEntity.title, kana, wordEntity.isKanji)
                if (word != null) {
                    words.add(word)
                }
            }
            return words
        }
        fun queryWordFromDatabase(dao: WordsDao, str: String): ArrayList<Word> {
            val res = getWordFromTitle(dao, str)
            val resFromKana = getWordFromKana(dao, str)
            for (word in resFromKana) {
                if (!res.contains(word)) {
                    res.add(word)
                }
            }
            for (word in res) {
                word.fromLocal = true
            }
            return res
        }

        fun insertWord(dao: WordsDao, word: Word) {
            dao.insertWord(WordEntity(word.title, word.kana, word.isKanji, word.suru))
            for ((tid, item) in word.items.withIndex()) {
                dao.insertWordItem(WordItemEntity(word.title, word.kana, word.isKanji, tid, item.type.value))
                for ((mid, meaning) in item.meanings.withIndex()) {
                    dao.insertWordMeaning(WordMeaningEntity(
                            word.title, word.kana, word.isKanji, tid, mid, meaning.indexMajor,
                            meaning.indexMinor, meaning.meaning))

                    for ((eid, example) in meaning.examples.withIndex()) {
                        dao.insertWordMeaningExample(WordMeaningExampleEntity(
                                word.title, word.kana, word.isKanji, tid, mid, eid, example))
                    }
                }
                for ((xid, extra) in item.extra.withIndex()) {
                    dao.insertWordExtra(WordExtraInfoEntity(word.title, word.kana, word.isKanji,
                            tid, xid, extra.key, extra.value))
                }
            }
        }

        fun getAllCardsOfSet(dao: WordsDao, setName: String): ArrayList<Card> {
            val cards = ArrayList<Card>()

            val cardEntities = dao.getAllCards(setName)
            for (entity in cardEntities) {
                cards.add(Card(dao, entity))
            }

            return cards
        }
        fun getAllWordSets(dao: WordsDao): ArrayList<WordSet> {
            val sets = ArrayList<WordSet>()

            val setEntities = dao.getAllWordSets()
            for (entity in setEntities) {
                sets.add(WordSet(dao, entity))
            }

            return sets
        }
        fun getAllWordSetNames(dao: WordsDao): ArrayList<String> {
            val names = ArrayList<String>()

            val setEntities = dao.getAllWordSets()
            for (entity in setEntities) {
                names.add(entity.setName)
            }

            return names
        }

        fun insertCardToSet(dao: WordsDao, setName: String, wordLabel: WordLabel) {
            if (checkCardInSet(dao, setName, wordLabel)) {
                return
            }
            val entity = dao.getWord(wordLabel.title, wordLabel.kana, wordLabel.isKanji)
            if (entity == null) {
                WeblioCrawler().queryWord(wordLabel)?.let {
                    insertWord(dao, it)
                } ?: return
            }
            dao.insertCard(CardEntity(wordLabel.title, wordLabel.kana, wordLabel.isKanji, setName, 0, 0))
        }
        fun insertCardToSet(dao: WordsDao, setName: String, word: Word) {
            if (checkCardInSet(dao, setName, word)) {
                return
            }
            val entity = dao.getWord(word.title, word.kana, word.isKanji)
            if (entity == null) {
                insertWord(dao, word)
            }
            dao.insertCard(CardEntity(word.title, word.kana, word.isKanji, setName, 0, 0))
        }

        fun checkCardInSet(dao: WordsDao, setName: String, wordLabel: WordLabel): Boolean {
            return dao.hasCardInSet(setName, wordLabel.title, wordLabel.kana, wordLabel.isKanji) != 0
        }
        fun checkCardInSet(dao: WordsDao, setName: String, word: Word): Boolean {
            return dao.hasCardInSet(setName, word.title, word.kana, word.isKanji) != 0
        }

        fun initWords(dao: WordsDao, resources: Resources) {
            val gson = Gson()
            var inputStream = resources.openRawResource(R.raw.word_list_n1)
            var words = gson.fromJson(InputStreamReader(inputStream, Charsets.UTF_8), InitWords::class.java)
            for (word in words.words) {
                if (dao.getWord(word.title, word.kana, word.isKanji) == null) {
                    insertWord(dao, word.getWord())
                }
            }
            inputStream = resources.openRawResource(R.raw.word_list_n2)
            words = gson.fromJson(InputStreamReader(inputStream, Charsets.UTF_8), InitWords::class.java)
            for (word in words.words) {
                if (dao.getWord(word.title, word.kana, word.isKanji) == null) {
                    insertWord(dao, word.getWord())
                }
            }
            inputStream = resources.openRawResource(R.raw.word_list_n3)
            words = gson.fromJson(InputStreamReader(inputStream, Charsets.UTF_8), InitWords::class.java)
            for (word in words.words) {
                if (dao.getWord(word.title, word.kana, word.isKanji) == null) {
                    insertWord(dao, word.getWord())
                }
            }
            inputStream = resources.openRawResource(R.raw.word_list_n4)
            words = gson.fromJson(InputStreamReader(inputStream, Charsets.UTF_8), InitWords::class.java)
            for (word in words.words) {
                if (dao.getWord(word.title, word.kana, word.isKanji) == null) {
                    insertWord(dao, word.getWord())
                }
            }
        }
        fun initBaseSet(resources: Resources) {
            val gson = Gson()
            var inputStream = resources.openRawResource(R.raw.word_list_n1)
            var words = gson.fromJson(InputStreamReader(inputStream, Charsets.UTF_8), InitWords::class.java)
            for (word in words.words) {
                BaseSetJp1.SET.add(WordLabel(word.title, word.kana, word.isKanji))
            }
            inputStream = resources.openRawResource(R.raw.word_list_n2)
            words = gson.fromJson(InputStreamReader(inputStream, Charsets.UTF_8), InitWords::class.java)
            for (word in words.words) {
                BaseSetJp2.SET.add(WordLabel(word.title, word.kana, word.isKanji))
            }
            inputStream = resources.openRawResource(R.raw.word_list_n3)
            words = gson.fromJson(InputStreamReader(inputStream, Charsets.UTF_8), InitWords::class.java)
            for (word in words.words) {
                BaseSetJp3.SET.add(WordLabel(word.title, word.kana, word.isKanji))
            }
            inputStream = resources.openRawResource(R.raw.word_list_n4)
            words = gson.fromJson(InputStreamReader(inputStream, Charsets.UTF_8), InitWords::class.java)
            for (word in words.words) {
                BaseSetJp4.SET.add(WordLabel(word.title, word.kana, word.isKanji))
            }
        }
    }
}
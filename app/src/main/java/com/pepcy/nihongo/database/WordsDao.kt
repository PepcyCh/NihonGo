package com.pepcy.nihongo.database

import androidx.room.*

@Dao
interface WordsDao {
    @Query("SELECT * FROM word WHERE title = :title")
    fun getWordFromTitle(title: String): List<WordEntity>
    @Query("SELECT * FROM word WHERE kana = :kana")
    fun getWordFromKana(kana: String): List<WordEntity>
    @Query("SELECT * FROM word WHERE title = :title AND kana = :kana AND isKanji = :isKanji")
    fun getWord(title: String, kana: String, isKanji: Boolean): WordEntity?

    @Query("SELECT * FROM word_item WHERE title = :title AND kana = :kana AND isKanji = :isKanji ORDER BY tid")
    fun getWordItem(title: String, kana: String, isKanji: Boolean): List<WordItemEntity>

    @Query("SELECT * FROM word_meaning WHERE title = :title AND kana = :kana " +
            "AND isKanji = :isKanji AND tid = :tid ORDER BY mid")
    fun getWordMeaning(title: String, kana: String, isKanji: Boolean, tid: Int): List<WordMeaningEntity>

    @Query("SELECT * FROM word_meaning_example WHERE title = :title AND kana = :kana " +
            "AND isKanji = :isKanji AND tid = :tid AND mid = :mid ORDER BY eid")
    fun getWordMeaningExample(title: String, kana: String, isKanji: Boolean, tid: Int, mid: Int): List<WordMeaningExampleEntity>

    @Query("SELECT * FROM word_extra WHERE title = :title AND kana = :kana AND isKanji = :isKanji AND tid = :tid ORDER BY xid")
    fun getWordExtra(title: String, kana: String, isKanji: Boolean, tid: Int): List<WordExtraInfoEntity>

    @Query("SELECT * FROM word_set ORDER BY createTime")
    fun getAllWordSets(): List<WordSetEntity>
    @Query("SELECT * FROM word_set WHERE setName = :setName")
    fun getWordSet(setName: String): WordSetEntity?
    @Query("SELECT COUNT(*) FROM word_set WHERE setName = :setName")
    fun hasWordSetWithName(setName: String): Int

    @Query("SELECT COUNT(*) FROM card WHERE setName = :setName")
    fun getWordSetCardCount(setName: String): Int

    @Query("SELECT * FROM card WHERE setName = :setName ORDER BY date")
    fun getAllCards(setName: String): List<CardEntity>

    @Query("SELECT * FROM card WHERE setName = :setName AND date = :date")
    fun getCardOfDate(setName: String, date: Long): List<CardEntity>

    @Query("SELECT * FROM card WHERE setName = :setName AND level = -1")
    fun getNewCards(setName: String): List<CardEntity>

    @Query("SELECT COUNT(*) FROM card WHERE setName = :setName AND title = :title AND kana = :kana AND isKanji = :isKanji")
    fun hasCardInSet(setName: String, title: String, kana: String, isKanji: Boolean): Int

    @Query("SELECT * FROM word_set_daily WHERE setName = :setName ORDER BY day DESC")
    fun getWordSetDaily(setName: String): List<WordSetDailyEntity>

    @Insert
    fun insertWord(entity: WordEntity)
    @Insert
    fun insertWordItem(entity: WordItemEntity)
    @Insert
    fun insertWordMeaning(entity: WordMeaningEntity)
    @Insert
    fun insertWordMeaningExample(entity: WordMeaningExampleEntity)
    @Insert
    fun insertWordExtra(entity: WordExtraInfoEntity)

    @Insert
    fun insertCard(entity: CardEntity)
    @Insert
    fun insertWordSet(entity: WordSetEntity)
    @Insert
    fun insertWordSetDaily(entity: WordSetDailyEntity)

    @Update
    fun updateCard(entity: CardEntity)
    @Update
    fun updateWordSet(entity: WordSetEntity)

    @Delete
    fun deleteCard(entity: CardEntity)
    @Delete
    fun deleteWordSet(entity: WordSetEntity)
    @Delete
    fun deleteWordSetDaily(entity: WordSetDailyEntity)
}
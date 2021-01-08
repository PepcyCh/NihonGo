package com.pepcy.nihongo.database

import androidx.room.Entity

@Entity(tableName = "word", primaryKeys = ["title", "kana", "isKanji"])
data class WordEntity(
    val title: String,
    val kana: String,
    val isKanji: Boolean,
    val suru: Boolean,
)

@Entity(tableName = "word_item", primaryKeys = ["title", "kana", "isKanji", "tid"])
data class WordItemEntity(
    val title: String,
    val kana: String,
    val isKanji: Boolean,
    val tid: Int,
    val type: Int,
)

@Entity(tableName = "word_meaning", primaryKeys = ["title", "kana", "isKanji", "tid", "mid"])
data class WordMeaningEntity(
    val title: String,
    val kana: String,
    val isKanji: Boolean,
    val tid: Int,
    val mid: Int,
    val major: Int,
    val minor: Int,
    val meaning: String,
)

@Entity(tableName = "word_meaning_example", primaryKeys = ["title", "kana", "isKanji", "tid", "mid", "eid"])
data class WordMeaningExampleEntity(
    val title: String,
    val kana: String,
    val isKanji: Boolean,
    val tid: Int,
    val mid: Int,
    val eid: Int,
    val example: String,
)

@Entity(tableName = "word_extra", primaryKeys = ["title", "kana", "isKanji", "tid", "xid"])
data class WordExtraInfoEntity(
    val title: String,
    val kana: String,
    val isKanji: Boolean,
    val tid: Int,
    val xid: Int,
    val key: String,
    val value: String,
)
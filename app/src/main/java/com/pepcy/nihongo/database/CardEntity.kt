package com.pepcy.nihongo.database

import androidx.room.Entity

@Entity(tableName = "card", primaryKeys = ["title", "kana", "isKanji", "setName"])
data class CardEntity(
    val title: String,
    val kana: String,
    val isKanji: Boolean,
    val setName: String,
    val level: Int,
    val date: Long,
)
package com.pepcy.nihongo.database

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "word_set")
data class WordSetEntity(
    @PrimaryKey val setName: String,
    val newWordsPerDay: Int,
    val questionsPerCard: Int,
    val questionSingleMeaning: Boolean,
    val latestLearnDay: Long,
    val createTime: Long
) : Parcelable

@Entity(tableName = "word_set_daily", primaryKeys = ["setName", "day"])
data class WordSetDailyEntity(
    val setName: String,
    val day: Long,
    val accuracy: Double,
    val newCount: Int,
    val reviewCount: Int,
    val timeUsed: Long,
)
package com.pepcy.nihongo.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "config")
data class ConfigEntity(
    @PrimaryKey val cid: Int,
    val lang: String,
)
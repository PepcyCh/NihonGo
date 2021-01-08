package com.pepcy.nihongo.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface ConfigDao {
    @Insert
    fun insertConfig(configEntity: ConfigEntity)

    @Update
    fun updateConfig(configEntity: ConfigEntity)

    @Query("SELECT COUNT(*) FROM config")
    fun checkConfig(): Int

    @Query("SELECT * FROM config WHERE cid = 1")
    fun getConfig(): ConfigEntity
}
package com.pepcy.nihongo.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [
    WordEntity::class, WordItemEntity::class, WordMeaningEntity::class,
    WordMeaningExampleEntity::class, WordExtraInfoEntity::class, WordSetEntity::class,
    WordSetDailyEntity::class, CardEntity::class], version = 1)
abstract class WordsDatabase : RoomDatabase() {
    abstract fun wordsDao(): WordsDao

    companion object {
        @Volatile
        private var INSTANCE: WordsDatabase? = null

        fun getInstance(context: Context): WordsDatabase {
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(
                            context.applicationContext, WordsDatabase::class.java, "words_database")
                            .fallbackToDestructiveMigration().build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}
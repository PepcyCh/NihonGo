package com.pepcy.nihongo.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [ConfigEntity::class], version = 1)
abstract class ConfigDatabase : RoomDatabase() {
    abstract fun configDao(): ConfigDao

    companion object {
        @Volatile
        private var INSTANCE: ConfigDatabase? = null

        fun getInstance(context: Context): ConfigDatabase {
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext, ConfigDatabase::class.java, "config_database")
                        .fallbackToDestructiveMigration().build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}